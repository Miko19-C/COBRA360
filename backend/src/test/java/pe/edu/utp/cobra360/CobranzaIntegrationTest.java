package pe.edu.utp.cobra360;
import pe.edu.utp.cobra360.application.shared.audit.AuditoriaService;
import pe.edu.utp.cobra360.application.cartera.CarteraQueryService;
import pe.edu.utp.cobra360.application.cartera.AsignacionService;
import pe.edu.utp.cobra360.application.gestion.CobranzaFacade;
import pe.edu.utp.cobra360.application.pagos.RegistroPagoService;
import pe.edu.utp.cobra360.application.reportes.ReporteService;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.*;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.*;
import org.flywaydb.core.Flyway;
import com.fasterxml.jackson.databind.ObjectMapper;

import pe.edu.utp.cobra360.domain.BusinessException;
import pe.edu.utp.cobra360.application.command.Requests.*;

@SpringBootTest @AutoConfigureMockMvc @ActiveProfiles("demo") @Testcontainers
@Transactional @WithMockUser(username="supervisor",roles="SUPERVISOR")
class CobranzaIntegrationTest {
    static { System.setProperty("api.version","1.44"); }
    @Container static MySQLContainer<?> mysql=new MySQLContainer<>("mysql:8.0.44")
        .withDatabaseName("cobra360_test").withUsername("test").withPassword("test");
    @DynamicPropertySource static void database(DynamicPropertyRegistry r){
        r.add("spring.datasource.url",mysql::getJdbcUrl);
        r.add("spring.datasource.username",mysql::getUsername);
        r.add("spring.datasource.password",mysql::getPassword);
    }
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired JdbcTemplate jdbc;
    @Autowired Flyway flyway;
    @Autowired Clock clock;
    @Autowired CobranzaFacade facade;
    @Autowired RegistroPagoService pagos;
    @Autowired AsignacionService asignacion;
    @Autowired ReporteService reportes;
    @Autowired CarteraQueryService consultas;
    @MockitoSpyBean AuditoriaService auditoriaSpy;
    @Autowired jakarta.persistence.EntityManager entityManager;
    private LocalDate hoy(){return LocalDate.now(clock);}
    private RegistrarGestion gestion(Long deuda,Long resultado,BigDecimal monto,LocalDate fecha){
        return new RegistrarGestion(deuda,resultado,deuda,"Conversación de prueba con el titular.",monto,fecha);
    }
    @Test void flywayMigraMysqlYSegundoArranqueNoReaplica(){
        assertThat(flyway.info().applied()).hasSize(3);
        assertThat(flyway.migrate().migrationsExecuted).isZero();
        assertThat(jdbc.queryForObject("select count(*) from information_schema.tables where table_schema=database() and table_name in ('perfil_riesgo','asesor','cliente','telefono','deuda','asignacion','tipo_resultado','gestion','promesa_pago','pago')",Long.class)).isEqualTo(10);
    }
    @Test void guardaGestionPromesaYAuditoriaAtomicamente(){
        long eventosAntes=jdbc.queryForObject("select count(*) from auditoria",Long.class);
        var respuesta=facade.completarGestion(gestion(13L,1L,new BigDecimal("100.00"),hoy().plusDays(2)));
        entityManager.flush();
        assertThat(respuesta.get("promesaId")).isNotEqualTo(0L);
        assertThat(jdbc.queryForObject("select estado from asignacion where id_asignacion=13",String.class)).isEqualTo("GESTIONADA");
        assertThat(jdbc.queryForObject("select count(*) from auditoria",Long.class)).isEqualTo(eventosAntes+3);
    }
    @Test void noGuardaNadaCuandoPromesaSuperaSaldo(){
        long antes=jdbc.queryForObject("select count(*) from gestion",Long.class);
        assertThatThrownBy(()->facade.completarGestion(gestion(13L,1L,new BigDecimal("999999.00"),hoy()))).isInstanceOf(BusinessException.class);
        assertThat(jdbc.queryForObject("select count(*) from gestion",Long.class)).isEqualTo(antes);
    }
    @Test void resultadoSinPromesaRechazaCamposDeCompromiso(){
        assertThatThrownBy(()->facade.completarGestion(gestion(13L,2L,new BigDecimal("100"),hoy()))).isInstanceOf(BusinessException.class);
    }
    @Test void rechazaPromesaPasada(){
        assertThatThrownBy(()->facade.completarGestion(gestion(13L,1L,new BigDecimal("100"),hoy().minusDays(1)))).isInstanceOf(BusinessException.class);
    }
    @Test void rechazaTelefonoDeOtroCliente(){
        assertThatThrownBy(()->facade.completarGestion(new RegistrarGestion(13L,4L,14L,"Intento de contacto",null,null))).isInstanceOf(BusinessException.class);
    }
    @Test void numeroErradoSeMarcaYNoPuedeReutilizarse(){
        facade.completarGestion(gestion(13L,5L,null,null));
        entityManager.flush();
        assertThat(jdbc.queryForObject("select estado from telefono where id_telefono=13",String.class)).isEqualTo("ERRADO");
        assertThatThrownBy(()->facade.completarGestion(gestion(13L,4L,null,null))).isInstanceOf(BusinessException.class);
    }
    @Test void pagoParcialYFinalActualizanPromesaYSaldo(){
        var r=facade.completarGestion(gestion(13L,1L,new BigDecimal("100"),hoy()));
        Long id=((Number)r.get("promesaId")).longValue();
        BigDecimal saldo=jdbc.queryForObject("select saldo_actual from deuda where id_deuda=13",BigDecimal.class);
        pagos.registrarPago(new RegistrarPago(13L,id,new BigDecimal("40"),hoy()),null);
        entityManager.flush();
        assertThat(jdbc.queryForObject("select estado from promesa_pago where id_promesa=?",String.class,id)).isEqualTo("PARCIAL");
        pagos.registrarPago(new RegistrarPago(13L,id,new BigDecimal("60"),hoy()),null);
        entityManager.flush();
        assertThat(jdbc.queryForObject("select estado from promesa_pago where id_promesa=?",String.class,id)).isEqualTo("CUMPLIDA");
        assertThat(jdbc.queryForObject("select saldo_actual from deuda where id_deuda=13",BigDecimal.class)).isEqualByComparingTo(saldo.subtract(new BigDecimal("100")));
    }
    @Test void pagoNoPuedeSuperarSaldo(){
        assertThatThrownBy(()->pagos.registrarPago(new RegistrarPago(13L,null,new BigDecimal("999999"),hoy()),null)).isInstanceOf(BusinessException.class);
    }
    @Test void pagoNoPuedeVincularPromesaDeOtraDeuda(){
        assertThatThrownBy(()->pagos.registrarPago(new RegistrarPago(13L,1L,new BigDecimal("10"),hoy()),null)).isInstanceOf(BusinessException.class);
    }
    @Test void pagoFuturoSeRechaza(){
        assertThatThrownBy(()->pagos.registrarPago(new RegistrarPago(13L,null,new BigDecimal("10"),hoy().plusDays(1)),null)).isInstanceOf(BusinessException.class);
    }
    @Test void abonoExternoEsIdempotente(){
        var req=new RegistrarPago(13L,null,new BigDecimal("10"),hoy());
        pagos.registrarPago(req,"SIM-UNICO");
        assertThatThrownBy(()->pagos.registrarPago(req,"SIM-UNICO")).isInstanceOf(BusinessException.class).hasMessageContaining("ya fue registrado");
        assertThat(jdbc.queryForObject("select count(*) from pago where referencia_externa='SIM-UNICO'",Long.class)).isEqualTo(1);
    }
    @Test @WithMockUser(username="mirko",roles="ASESOR") void asesorSoloVeCarteraPropia() throws Exception {
        mvc.perform(get("/api/cartera")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(8)).andExpect(jsonPath("$[0].id_asesor").value(1));
        mvc.perform(get("/api/promesas")).andExpect(status().isOk());
        mvc.perform(get("/api/resumen")).andExpect(status().isOk()).andExpect(jsonPath("$.asignadas").value(8));
        mvc.perform(get("/api/reportes")).andExpect(status().isOk());
        mvc.perform(get("/api/cartera/2")).andExpect(status().isForbidden());
        mvc.perform(get("/api/auditoria")).andExpect(status().isForbidden());
        mvc.perform(post("/api/asignaciones/automaticas").with(csrf())).andExpect(status().isForbidden());
    }
    @Test @WithMockUser(username="mirko",roles="ASESOR") void asesorNoPuedeRegistrarPagoAjeno() throws Exception {
        mvc.perform(post("/api/pagos").with(csrf()).contentType("application/json")
            .content(mapper.writeValueAsString(new RegistrarPago(2L,null,new BigDecimal("10"),hoy())))).andExpect(status().isForbidden());
    }
    @Test void csrfObligatorioYValidacionDeImporte() throws Exception {
        mvc.perform(post("/api/pagos").contentType("application/json").content("{}")).andExpect(status().isForbidden());
        mvc.perform(post("/api/pagos").with(csrf()).contentType("application/json")
            .content(mapper.writeValueAsString(new RegistrarPago(13L,null,new BigDecimal("-1"),hoy())))).andExpect(status().isBadRequest());
    }
    @Test void asignacionRespetaPerfilYNoDuplica(){
        jdbc.update("delete from asignacion where id_asignacion=24");
        assertThat(asignacion.asignar().get("asignadas")).isEqualTo(1);
        assertThat(jdbc.queryForObject("select id_asesor from asignacion where id_deuda=24 and fecha_asignacion=?",Long.class,hoy())).isEqualTo(3);
        assertThat(asignacion.asignar().get("asignadas")).isEqualTo(0);
    }
    @Test void reporteYExportacionSeGeneranConDatosReales() throws Exception {
        assertThat(reportes.construir()).containsKeys("reporte","arbol","indicadores");
        mvc.perform(get("/api/pagos")).andExpect(status().isOk()).andExpect(jsonPath("$[0].nro_documento").isNotEmpty());
        mvc.perform(get("/api/reportes/cartera.csv")).andExpect(status().isOk()).andExpect(header().string("Content-Disposition","attachment; filename=cartera-cobra360.csv"));
        mvc.perform(get("/api/patrones")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(9));
    }
    @Test void estadoVencidoSeCalculaAunqueElProcesoDiarioNoHayaCorrido(){
        jdbc.update("update promesa_pago set fecha_compromiso=?,estado='PENDIENTE' where id_promesa=1",hoy().minusDays(1));
        var promesa=consultas.promesas().stream().filter(p->((Number)p.get("id_promesa")).longValue()==1L).findFirst().orElseThrow();
        assertThat(promesa.get("estado")).isEqualTo("INCUMPLIDA");
    }
    @Test @Transactional(propagation=Propagation.NOT_SUPPORTED) void falloFinalRevierteGestionPromesaTelefonoYAuditoria(){
        long gestionesAntes=jdbc.queryForObject("select count(*) from gestion",Long.class);
        long promesasAntes=jdbc.queryForObject("select count(*) from promesa_pago",Long.class);
        long auditoriaAntes=jdbc.queryForObject("select count(*) from auditoria",Long.class);
        org.mockito.Mockito.doThrow(new IllegalStateException("Falla inducida de auditoría"))
            .when(auditoriaSpy).registrar(org.mockito.ArgumentMatchers.eq("Facade · Completar gestión"),org.mockito.ArgumentMatchers.anyString());
        try {
            assertThatThrownBy(()->facade.completarGestion(gestion(14L,1L,new BigDecimal("200"),hoy())))
                .isInstanceOf(IllegalStateException.class);
            assertThat(jdbc.queryForObject("select count(*) from gestion",Long.class)).isEqualTo(gestionesAntes);
            assertThat(jdbc.queryForObject("select count(*) from promesa_pago",Long.class)).isEqualTo(promesasAntes);
            assertThat(jdbc.queryForObject("select count(*) from auditoria",Long.class)).isEqualTo(auditoriaAntes);
            assertThat(jdbc.queryForObject("select estado from asignacion where id_asignacion=14",String.class)).isEqualTo("PENDIENTE");
        }finally{org.mockito.Mockito.reset(auditoriaSpy);}
    }
    @Test @WithAnonymousUser void autenticacionRealConHashBcryptYProteccionApi() throws Exception {
        SecurityContextHolder.clearContext();
        mvc.perform(get("/api/cartera")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/login").with(csrf()).param("username","supervisor").param("password","incorrecta"))
            .andExpect(status().isUnauthorized()).andExpect(content().encoding("UTF-8"))
            .andExpect(jsonPath("$.message").value("Usuario o contraseña incorrectos."));
        mvc.perform(post("/api/login").with(csrf()).param("username","supervisor").param("password","Cobra360!2026")).andExpect(status().isOk());
        assertThat(jdbc.queryForObject("select password_hash from usuario where usuario='supervisor'",String.class)).startsWith("$2a$");
    }
    @Test @WithAnonymousUser void frontendSeEmpaquetaComoModulosPublicos() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk()).andExpect(forwardedUrl("index.html"));
        mvc.perform(get("/index.html")).andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("type=\"module\" src=\"/src/main.js\"")));
        mvc.perform(get("/src/main.js")).andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("controllers/session.js")));
        mvc.perform(get("/src/services/api.js")).andExpect(status().isOk());
        mvc.perform(get("/src/views/cartera.js")).andExpect(status().isOk());
    }
    @Test @Transactional(propagation=Propagation.NOT_SUPPORTED) void dosPagosConcurrentesNoSobregiranSaldo() throws Exception {
        jdbc.update("insert into deuda(id_deuda,id_cliente,producto,monto_total,saldo_actual,fecha_vencimiento,tramo_mora) values(1001,1,'Prueba concurrencia',100,100,?,'TEMPRANA')",hoy().minusDays(1));
        ExecutorService pool=Executors.newFixedThreadPool(2);
        CountDownLatch ready=new CountDownLatch(2),start=new CountDownLatch(1);
        AtomicInteger exitos=new AtomicInteger();
        Callable<Void> tarea=()->{
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("supervisor","N/A",List.of(new SimpleGrantedAuthority("ROLE_SUPERVISOR"))));
            ready.countDown();start.await();
            try{pagos.registrarPago(new RegistrarPago(1001L,null,new BigDecimal("80"),hoy()),null);exitos.incrementAndGet();}
            catch(BusinessException expected){assertThat(expected.getMessage()).contains("saldo");}
            finally{SecurityContextHolder.clearContext();}return null;
        };
        try {
            Future<Void> a=pool.submit(tarea),b=pool.submit(tarea);assertThat(ready.await(5,TimeUnit.SECONDS)).isTrue();start.countDown();
            a.get(10,TimeUnit.SECONDS);b.get(10,TimeUnit.SECONDS);
            assertThat(exitos.get()).isEqualTo(1);
            assertThat(jdbc.queryForObject("select saldo_actual from deuda where id_deuda=1001",BigDecimal.class)).isEqualByComparingTo("20.00");
        }finally{pool.shutdownNow();}
    }
    @Test void parametrosInvalidosNoSonErroresInternos() throws Exception {
        mvc.perform(get("/api/cartera/no-es-un-numero")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/login")).andExpect(status().isNotFound());
        mvc.perform(post("/api/cartera").with(csrf())).andExpect(status().isMethodNotAllowed()).andExpect(header().string("Allow", "GET"));
        mvc.perform(post("/api/pagos").with(csrf()).contentType("text/plain").content("{}"))
            .andExpect(status().isUnsupportedMediaType());
    }
    @Test void detallePublicaLimiteDeFechaYPermiteContactoSinTelefono() throws Exception {
        mvc.perform(get("/api/cartera/13")).andExpect(status().isOk())
            .andExpect(jsonPath("$.fechaMaximaCompromiso").value(hoy().plusDays(90).toString()));
        mvc.perform(post("/api/gestiones").with(csrf()).contentType("application/json")
            .content(mapper.writeValueAsString(new RegistrarGestion(13L,4L,null,"Contacto por otro canal registrado.",null,null))))
            .andExpect(status().isOk());
    }
    @Test @WithMockUser(username="mirko",roles="ASESOR") void promesaHistoricaSinCarteraActualNoOfrecePago() {
        var actual=consultas.promesas().stream().filter(p->((Number)p.get("id_deuda")).longValue()==1L).findFirst().orElseThrow();
        assertThat(actual.get("puede_registrar_pago")).isEqualTo(true);
        jdbc.update("update asignacion set fecha_asignacion=? where id_asesor=1 and fecha_asignacion=?",hoy().minusDays(20),hoy());
        var historica=consultas.promesas().stream().filter(p->((Number)p.get("id_deuda")).longValue()==1L).findFirst().orElseThrow();
        assertThat(historica.get("puede_registrar_pago")).isEqualTo(false);
        assertThatThrownBy(()->pagos.registrarPago(new RegistrarPago(1L,null,new BigDecimal("1"),hoy()),null))
            .isInstanceOf(pe.edu.utp.cobra360.domain.AccesoDenegadoException.class);
    }
    @Test void supervisorPuedeCobrarPromesasHistoricas() {
        assertThat(consultas.promesas()).allSatisfy(p->assertThat(p.get("puede_registrar_pago")).isEqualTo(true));
    }

}
