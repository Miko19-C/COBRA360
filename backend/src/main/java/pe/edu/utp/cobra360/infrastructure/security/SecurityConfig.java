package pe.edu.utp.cobra360.infrastructure.security;

import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
    @Bean UserDetailsService users(JdbcTemplate jdbc){
        return username -> jdbc.query("select usuario,password_hash,rol from usuario where usuario=?",
            (rs,i)->User.withUsername(rs.getString(1)).password(rs.getString(2)).roles(rs.getString(3)).build(),username)
            .stream().findFirst().orElseThrow(()->new UsernameNotFoundException("Credenciales incorrectas"));
    }
    @Bean SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(auth->auth
            .requestMatchers("/","/index.html","/styles.css","/src/**","/favicon.svg","/api/session","/actuator/health").permitAll()
            .requestMatchers("/api/asignaciones/automaticas","/api/core/**","/api/auditoria","/api/patrones/**","/api/migraciones").hasRole("SUPERVISOR")
            .anyRequest().authenticated())
            .formLogin(form->form.loginProcessingUrl("/api/login")
                .successHandler((req,res,auth)->{res.setContentType("application/json;charset=UTF-8");res.getWriter().write("{\"ok\":true}");})
                .failureHandler((req,res,ex)->{res.setStatus(401);res.setContentType("application/json;charset=UTF-8");res.getWriter().write("{\"message\":\"Usuario o contraseña incorrectos.\"}");}))
            .logout(logout->logout.logoutUrl("/api/logout").logoutSuccessHandler((req,res,auth)->res.setStatus(204)))
            .exceptionHandling(e->e.authenticationEntryPoint((req,res,ex)->{res.setStatus(401);res.setContentType("application/json;charset=UTF-8");res.getWriter().write("{\"message\":\"Inicia sesión para continuar.\"}");})
                .accessDeniedHandler((req,res,ex)->{
                    res.setStatus(403);res.setContentType("application/json;charset=UTF-8");
                    boolean csrf=ex instanceof org.springframework.security.web.csrf.CsrfException;
                    res.getWriter().write(csrf?"{\"message\":\"La sesión de seguridad venció. Recarga la página.\",\"code\":\"CSRF_INVALID\"}":
                        "{\"message\":\"No tienes permiso para realizar esta operación.\",\"code\":\"FORBIDDEN\"}");
                }))
            .headers(h->h.contentSecurityPolicy(c->c.policyDirectives("default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'; img-src 'self' data:; connect-src 'self'; frame-ancestors 'none'; base-uri 'self'; form-action 'self'")))
            .build();
    }
}
