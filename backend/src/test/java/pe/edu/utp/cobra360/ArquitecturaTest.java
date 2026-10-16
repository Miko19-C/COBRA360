package pe.edu.utp.cobra360;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import com.tngtech.archunit.junit.*;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.core.importer.ImportOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

/** Contratos ejecutables de arquitectura: evitan degradar la separación al añadir funcionalidades. */
@AnalyzeClasses(packages="pe.edu.utp.cobra360",importOptions=ImportOption.DoNotIncludeTests.class)
class ArquitecturaTest {
    @ArchTest static final ArchRule dominioNoDependeDeCapasExternas=noClasses()
        .that().resideInAPackage("..domain..")
        .should().dependOnClassesThat().resideInAnyPackage("..application..","..web..","..infrastructure..","org.springframework..");

    @ArchTest static final ArchRule aplicacionSoloUsaContratos=noClasses()
        .that().resideInAPackage("..application..")
        .should().dependOnClassesThat().resideInAnyPackage("..web..","..infrastructure..","org.springframework.jdbc..","org.springframework.data..","java.sql..");

    @ArchTest static final ArchRule controladoresNoAccedenALaBase=noClasses()
        .that().resideInAPackage("..web..")
        .should().dependOnClassesThat().resideInAnyPackage("..infrastructure..","org.springframework.jdbc..","org.springframework.data..","org.flywaydb..","jakarta.persistence..","java.sql..");

    @ArchTest static final ArchRule puertosSinFrameworks=noClasses()
        .that().resideInAPackage("..domain.ports..")
        .should().dependOnClassesThat().resideInAnyPackage("org.springframework..","jakarta.persistence..","org.flywaydb..");

    @ArchTest static final ArchRule jpaSoloEnInfraestructura=classes()
        .that().areAssignableTo(JpaRepository.class)
        .should().resideInAPackage("..infrastructure.repository..");

    @ArchTest static final ArchRule serviciosEnAplicacion=classes()
        .that().areAnnotatedWith(Service.class)
        .should().resideInAPackage("..application..");

    @ArchTest static final ArchRule modulosDeAplicacionSinCiclos=slices()
        .matching("pe.edu.utp.cobra360.application.(*)..")
        .should().beFreeOfCycles();
}
