package pe.edu.utp.cobra360.web;

import java.util.Map;
import pe.edu.utp.cobra360.domain.BusinessException;
import pe.edu.utp.cobra360.domain.AccesoDenegadoException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice @Slf4j
public class ApiExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    ResponseEntity<?> business(BusinessException ex){return ResponseEntity.badRequest().body(Map.of("message",ex.getMessage()));}
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<?> validation(MethodArgumentNotValidException ex){
        String message=ex.getBindingResult().getFieldErrors().stream().map(e->e.getField()+": "+e.getDefaultMessage()).findFirst().orElse("Datos inválidos.");
        return ResponseEntity.badRequest().body(Map.of("message",message));
    }
    @ExceptionHandler({AccessDeniedException.class,AccesoDenegadoException.class})
    ResponseEntity<?> denied(RuntimeException ex){return ResponseEntity.status(403).body(Map.of("message",ex.getMessage()));}
    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<?> conflict(DataIntegrityViolationException ex){
        return ResponseEntity.status(409).body(Map.of("message","La operación entra en conflicto con un registro existente. Actualiza y vuelve a intentar."));
    }
    @ExceptionHandler({HttpMessageNotReadableException.class,IllegalArgumentException.class,ArithmeticException.class})
    ResponseEntity<?> malformed(Exception ex){return ResponseEntity.badRequest().body(Map.of("message","Revisa los datos enviados y el formato de fechas e importes."));}
    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    ResponseEntity<?> invalidParameter(Exception ex){return ResponseEntity.badRequest().body(Map.of("message","El identificador o parámetro enviado no es válido."));}
    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    ResponseEntity<?> wrongMethod(org.springframework.web.HttpRequestMethodNotSupportedException ex){
        var response=ResponseEntity.status(405);
        if(ex.getSupportedHttpMethods()!=null)response.allow(ex.getSupportedHttpMethods().toArray(org.springframework.http.HttpMethod[]::new));
        return response.body(Map.of("message","Método HTTP no permitido para esta ruta."));
    }
    @ExceptionHandler(org.springframework.web.HttpMediaTypeNotSupportedException.class)
    ResponseEntity<?> wrongContentType(Exception ex){return ResponseEntity.status(415).body(Map.of("message","Envía los datos con Content-Type application/json."));}
    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    ResponseEntity<?> missingResource(Exception ex){return ResponseEntity.status(404).body(Map.of("message","La ruta solicitada no existe."));}
    @ExceptionHandler(Exception.class)
    ResponseEntity<?> unexpected(Exception ex){log.error("Error no controlado",ex);return ResponseEntity.internalServerError().body(Map.of("message","No se pudo completar la operación. Revisa el registro del servidor."));}
}
