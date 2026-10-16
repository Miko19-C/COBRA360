package pe.edu.utp.cobra360.infrastructure.demo;

import java.time.*;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

/** Datos ficticios exclusivamente con perfil demo. No reemplaza datos ni ejecuta DROP. */
@Component @Profile("demo") @RequiredArgsConstructor
public class DemoDataInitializer implements ApplicationRunner {
    private final JdbcTemplate jdbc;
    private final PasswordEncoder encoder;
    private final Clock clock;
    @org.springframework.beans.factory.annotation.Value("${demo.password:Cobra360!2026}")
    private String demoPassword;
    @Override @Transactional
    public void run(ApplicationArguments args){
        if(jdbc.queryForObject("select count(*) from cliente",Long.class)>0)return;
        String[] asesores={"Mirko Coronel","Lucía Lozada","Diego Salazar"};
        String[] usuarios={"mirko","lucia","diego"};
        for(int i=0;i<3;i++){
            jdbc.update("insert into asesor(id_asesor,id_perfil,nombre,usuario,estado) values(?,?,?,?, 'A')",i+1,i+1,asesores[i],usuarios[i]);
            jdbc.update("insert into usuario(usuario,password_hash,nombre,rol,id_asesor) values(?,?,?,'ASESOR',?)",
                usuarios[i],encoder.encode(demoPassword),asesores[i],i+1);
        }
        jdbc.update("insert into usuario(usuario,password_hash,nombre,rol) values(?,?,?,'SUPERVISOR')",
            "supervisor",encoder.encode(demoPassword),"Andrea Mendoza");
        String[] nombres={"Valeria Rojas","Carlos Mendoza","Mariana Torres","José Castillo","Camila Vargas","Luis Fernández",
            "Daniela Herrera","Miguel Quispe","Andrea Salas","Jorge Ramírez","Luciana Paredes","Renato Flores",
            "Paola Gutiérrez","Sergio Medina","Gabriela Soto","Fernando Ríos","Adriana Chávez","Pedro Aguilar",
            "Natalia Campos","Alonso Vega","Claudia León","Ricardo Peña","Isabel Navarro","Mateo Cárdenas"};
        String[] tramos={"TEMPRANA","TARDIA","CASTIGO"};
        String[] productos={"Tarjeta de crédito","Préstamo personal","Crédito vehicular"};
        LocalDate hoy=LocalDate.now(clock);
        for(int i=1;i<=24;i++){
            int tramo=(i-1)%3;
            BigDecimal total=BigDecimal.valueOf(1200L+i*395L).setScale(2);
            jdbc.update("insert into cliente(id_cliente,tipo_documento,nro_documento,nombre,correo) values(?,'DNI',?,?,?)",
                i,String.valueOf(90000000+i),nombres[i-1],"cliente"+i+"@example.com");
            jdbc.update("insert into telefono(id_telefono,id_cliente,numero,estado,es_principal) values(?,?,?,'ACTIVO',true)",
                i,i,"900"+String.format("%06d",i));
            jdbc.update("insert into deuda(id_deuda,id_cliente,producto,monto_total,saldo_actual,fecha_vencimiento,tramo_mora) values(?,?,?,?,?,?,?)",
                i,i,productos[tramo],total,total,hoy.minusDays(new int[]{12,56,120}[tramo]+i%8),tramos[tramo]);
            jdbc.update("insert into asignacion(id_asignacion,id_deuda,id_asesor,fecha_asignacion,estado) values(?,?,?,?,'PENDIENTE')",
                i,i,tramo+1,hoy);
            if(i<=12){
                jdbc.update("insert into asignacion(id_asignacion,id_deuda,id_asesor,fecha_asignacion,estado) values(?,?,?,?,'GESTIONADA')",
                    100+i,i,tramo+1,hoy.minusDays(5));
                jdbc.update("insert into gestion(id_gestion,id_asignacion,id_tipo_resultado,id_telefono,fecha_contacto,observacion) values(?,?,1,?,?,?)",
                    i,100+i,i,hoy.minusDays(5).atTime(9+i%8,15),"Cliente confirma compromiso de pago. Registro de demostración.");
                BigDecimal monto=total.multiply(new BigDecimal("0.20")).setScale(2);
                int escenario=i%4;
                LocalDate compromiso=hoy.plusDays(escenario==0?-2:escenario==1?0:escenario==2?3:1);
                String estado=escenario==0?"INCUMPLIDA":escenario==2?"CUMPLIDA":escenario==3?"PARCIAL":"PENDIENTE";
                jdbc.update("insert into promesa_pago(id_promesa,id_gestion,monto_comprometido,fecha_compromiso,estado) values(?,?,?,?,?)",
                    i,i,monto,compromiso,estado);
                if(escenario==2 || escenario==3){
                    BigDecimal abono=escenario==2?monto:monto.divide(new BigDecimal("2"));
                    jdbc.update("insert into pago(id_deuda,id_promesa,monto,fecha_pago) values(?,?,?,?)",i,i,abono,hoy.minusDays(i%3));
                    jdbc.update("update deuda set saldo_actual=saldo_actual-? where id_deuda=?",abono,i);
                }
            }
        }
    }
}
