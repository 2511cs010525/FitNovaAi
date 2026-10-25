package ai.fitnova.api;
import ai.fitnova.api.ApiDtos.MessageResponse; import org.springframework.http.*; import org.springframework.security.authentication.BadCredentialsException; import org.springframework.web.bind.MethodArgumentNotValidException; import org.springframework.web.bind.annotation.*; import java.util.NoSuchElementException;
@RestControllerAdvice public class ApiExceptionHandler {
 @ExceptionHandler(BadCredentialsException.class) ResponseEntity<MessageResponse> unauthorized(BadCredentialsException e){return ResponseEntity.status(401).body(new MessageResponse(e.getMessage()));}
 @ExceptionHandler(IllegalArgumentException.class) ResponseEntity<MessageResponse> conflict(IllegalArgumentException e){return ResponseEntity.status(409).body(new MessageResponse(e.getMessage()));}
 @ExceptionHandler(NoSuchElementException.class) ResponseEntity<MessageResponse> missing(NoSuchElementException e){return ResponseEntity.status(404).body(new MessageResponse(e.getMessage()));}
 @ExceptionHandler(IllegalStateException.class) ResponseEntity<MessageResponse> unavailable(IllegalStateException e){return ResponseEntity.status(503).body(new MessageResponse(e.getMessage()));}
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<MessageResponse> invalid(MethodArgumentNotValidException e){return ResponseEntity.badRequest().body(new MessageResponse("Please check the submitted values."));}}
