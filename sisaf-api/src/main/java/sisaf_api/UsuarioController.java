package sisaf_api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    // Almacenamiento temporal para probar el servicio web.
    private final Map<String, String> usuarios = new ConcurrentHashMap<>();

    // Servicio para registrar un usuario.
    @PostMapping("/registro")
    public ResponseEntity<String> registrar(
            @RequestBody Map<String, String> datos) {

        String usuario = datos.get("usuario");
        String password = datos.get("password");

        // Validar que los campos estén completos.
        if (usuario == null || usuario.isBlank()
                || password == null || password.isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Error: Debe ingresar usuario y contraseña.");
        }

        // Evitar registrar usuarios repetidos.
        if (usuarios.putIfAbsent(usuario, password) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Error: El usuario ya está registrado.");
        }

        return ResponseEntity.ok("Usuario registrado correctamente.");
    }

    // Servicio para iniciar sesión.
    @PostMapping("/login")
    public ResponseEntity<String> iniciarSesion(
            @RequestBody Map<String, String> datos) {

        String usuario = datos.get("usuario");
        String password = datos.get("password");

        // Comprobar que los datos coincidan.
        if (usuario != null && password != null
                && password.equals(usuarios.get(usuario))) {
            return ResponseEntity.ok("Autenticación satisfactoria.");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Error en la autenticación.");
    }
}