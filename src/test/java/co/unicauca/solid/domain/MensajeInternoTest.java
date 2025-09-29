package co.unicauca.solid.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MensajeInternoTest {

    @Test
    public void testConstructorBasico() {
        MensajeInterno m = new MensajeInterno(
            "est@unicauca.edu.co", "dir@unicauca.edu.co", "Propuesta", "Hola"
        );
        assertEquals("est@unicauca.edu.co", m.getRemitenteEmail());
        assertEquals("dir@unicauca.edu.co", m.getDestinatariosEmail());
        assertEquals("Propuesta", m.getAsunto());
        assertEquals("ENVIADO", m.getEstado());
    }

    @Test
    public void testEstadoInicial() {
        MensajeInterno m = new MensajeInterno();
        assertEquals("ENVIADO", m.getEstado());
    }

    @Test
    public void testEnumEstadoMensaje() {
        assertEquals("ENVIADO", MensajeInterno.EstadoMensaje.ENVIADO.getValor());
        assertEquals("LEIDO", MensajeInterno.EstadoMensaje.LEIDO.getValor());
        assertEquals("RESPONDIDO", MensajeInterno.EstadoMensaje.RESPONDIDO.getValor());
    }
}