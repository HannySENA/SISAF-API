
package sisaf_api;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Registrar un usuario en la base de datos.
    @PostMapping("/registro")
    public ResponseEntity<String> registrar(
            @RequestBody Map<String, String> datos) {

        String usuario = datos.get("usuario");
        String password = datos.get("password");

        // Validar los datos ingresados.
        if (usuario == null || usuario.isBlank()
                || password == null || password.isBlank()) {

            return ResponseEntity.badRequest()
                    .body("Error: Debe ingresar usuario y contraseña.");
        }

        // Comprobar si el usuario ya existe.
        if (usuarioRepository.existsByUsuario(usuario)) {

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Error: El usuario ya está registrado.");
        }

        // Proteger la contrasena con BCrypt.
        String passwordProtegida = passwordEncoder.encode(password);

        // Guardar el usuario en H2.
        Usuario nuevoUsuario = new Usuario(usuario, passwordProtegida);
        usuarioRepository.save(nuevoUsuario);

        return ResponseEntity.ok("Usuario registrado correctamente.");
    }

    // Validar las credenciales del usuario.
    @PostMapping("/login")
    public ResponseEntity<String> iniciarSesion(
            @RequestBody Map<String, String> datos) {

        String usuario = datos.get("usuario");
        String password = datos.get("password");

        if (usuario != null && password != null) {

            var usuarioGuardado =
                    usuarioRepository.findByUsuario(usuario);

            if (usuarioGuardado.isPresent()
                    && passwordEncoder.matches(
                            password,
                            usuarioGuardado.get().getPassword())) {

                return ResponseEntity.ok(
                        "Autenticación satisfactoria."
                );
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Error en la autenticación.");
    }
}
