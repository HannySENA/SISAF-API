
package sisaf_api;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
class UsuarioControllerTests {

    private UsuarioController controller;

    @BeforeEach
    void prepararPrueba() {
        controller = new UsuarioController();
    }

    @Test
    void registrarUsuarioCorrectamente() {
        ResponseEntity<String> respuesta = controller.registrar(
            Map.of("usuario", "usuario1", "password", "Clave123")
        );

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals("Usuario registrado correctamente.", respuesta.getBody());
    }

    @Test
    void rechazarUsuarioDuplicado() {
        Map<String, String> datos =
            Map.of("usuario", "usuario1", "password", "Clave123");

        controller.registrar(datos);
        ResponseEntity<String> respuesta = controller.registrar(datos);

        assertEquals(HttpStatus.CONFLICT, respuesta.getStatusCode());
    }

    @Test
    void iniciarSesionCorrectamente() {
        Map<String, String> datos =
            Map.of("usuario", "usuario1", "password", "Clave123");

        controller.registrar(datos);
        ResponseEntity<String> respuesta = controller.iniciarSesion(datos);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals("Autenticación satisfactoria.", respuesta.getBody());
    }

    @Test
    void rechazarContrasenaIncorrecta() {
        controller.registrar(
            Map.of("usuario", "usuario1", "password", "Clave123")
        );

        ResponseEntity<String> respuesta = controller.iniciarSesion(
            Map.of("usuario", "usuario1", "password", "Incorrecta")
        );

        assertEquals(HttpStatus.UNAUTHORIZED, respuesta.getStatusCode());
    }
}
