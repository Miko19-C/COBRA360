package pe.edu.utp.cobra360;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@org.springframework.scheduling.annotation.EnableScheduling
public class Cobra360Application {
    public static void main(String[] args) { SpringApplication.run(Cobra360Application.class, args); }
    @Bean public Clock businessClock() { return Clock.system(ZoneId.of("America/Lima")); }
}
