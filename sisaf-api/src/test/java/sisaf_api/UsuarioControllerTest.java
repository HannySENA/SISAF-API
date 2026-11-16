
package sisaf_api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

class UsuarioControllerTest {

    private UsuarioRepository usuarioRepository;
    private PasswordEncoder passwordEncoder;
    private UsuarioController controlador;

    @BeforeEach
    void configurar() {
        usuarioRepository = mock(UsuarioRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();

        controlador = new UsuarioController(
                usuarioRepository,
                passwordEncoder
        );
    }

    @Test
    void registrarUsuarioCorrectamente() {
        when(usuarioRepository.existsByUsuario("hanny"))
                .thenReturn(false);

        ResponseEntity<String> respuesta = controlador.registrar(
                Map.of("usuario", "hanny",
                       "password", "Clave123*")
        );

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(
                "Usuario registrado correctamente.",
                respuesta.getBody()
        );

        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void evitarUsuariosDuplicados() {
        when(usuarioRepository.existsByUsuario("hanny"))
                .thenReturn(true);

        ResponseEntity<String> respuesta = controlador.registrar(
                Map.of("usuario", "hanny",
                       "password", "Clave123*")
        );

        assertEquals(
                HttpStatus.CONFLICT,
                respuesta.getStatusCode()
        );

        verify(usuarioRepository, never())
                .save(any(Usuario.class));
    }

    @Test
    void iniciarSesionCorrectamente() {
        String claveProtegida =
                passwordEncoder.encode("Clave123*");

        Usuario usuario = new Usuario(
                "hanny",
                claveProtegida
        );

        when(usuarioRepository.findByUsuario("hanny"))
                .thenReturn(Optional.of(usuario));

        ResponseEntity<String> respuesta =
                controlador.iniciarSesion(
                        Map.of("usuario", "hanny",
                               "password", "Clave123*")
                );

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(
                "Autenticación satisfactoria.",
                respuesta.getBody()
        );
    }

    @Test
    void rechazarContrasenaIncorrecta() {
        String claveProtegida =
                passwordEncoder.encode("Clave123*");

        Usuario usuario = new Usuario(
                "hanny",
                claveProtegida
        );

        when(usuarioRepository.findByUsuario("hanny"))
                .thenReturn(Optional.of(usuario));

        ResponseEntity<String> respuesta =
                controlador.iniciarSesion(
                        Map.of("usuario", "hanny",
                               "password", "Incorrecta123")
                );

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                respuesta.getStatusCode()
        );
    }
}
