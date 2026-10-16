package pe.edu.utp.cobra360.web.session;
import java.util.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import lombok.RequiredArgsConstructor;
import pe.edu.utp.cobra360.application.shared.security.AccesoService;
@RestController @RequestMapping("/api") @RequiredArgsConstructor
public class SessionController {
    private final AccesoService acceso;
    @GetMapping("/session")
    public Map<String,Object> session(Authentication auth,HttpServletRequest request){
        CsrfToken token=(CsrfToken)request.getAttribute(CsrfToken.class.getName());
        var data=new LinkedHashMap<String,Object>();
        data.put("csrf",token.getToken());data.put("csrfHeader",token.getHeaderName());
        boolean authenticated=auth!=null&&auth.isAuthenticated();data.put("authenticated",authenticated);
        if(authenticated)data.put("user",acceso.actual());return data;
    }
}
