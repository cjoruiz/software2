package co.unicauca.solid.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProyectoGradoTest {

    @Test
    public void testConstructorBasico() {
        Docente dir = new Docente("dir@unicauca.edu.co", "pass", "D", "A", "123", "ING", "PLANTA");
        Estudiante est = new Estudiante("est@unicauca.edu.co", "pass", "E", "B", "123", "ING");
        ProyectoGrado p = new ProyectoGrado(
            "Sistema Académico", "INVESTIGACION", dir, null, est, "Obj G", "Obj E"
        );
        assertEquals("Sistema Académico", p.getTitulo());
        assertEquals("INVESTIGACION", p.getModalidad());
        assertEquals(1, p.getNumeroIntento());
        assertEquals("EN_PRIMERA_EVALUACION_FORMATO_A", p.getEstadoActual());
        assertFalse(p.estaRechazadoDefinitivamente());
    }

    @Test
    public void testPuedeReintentar() {
        ProyectoGrado p = new ProyectoGrado();
        assertTrue(p.puedeReintentar());
        p.setNumeroIntento(2);
        assertTrue(p.puedeReintentar());
        p.setNumeroIntento(3);
        assertFalse(p.puedeReintentar());
    }

    @Test
    public void testRechazoDefinitivoTrasTercerIntento() {
        ProyectoGrado p = new ProyectoGrado();
        p.setNumeroIntento(3);
        p.rechazarFormatoA("Mal");
        assertTrue(p.estaRechazadoDefinitivamente());
        assertEquals("RECHAZADO_DEFINITIVO", p.getEstadoActual());
    }

    @Test
    public void testNoPermitirEstudiante2EnPracticaProfesional() {
        ProyectoGrado p = new ProyectoGrado();
        p.setModalidad("PRACTICA_PROFESIONAL");
        Estudiante e1 = new Estudiante("e1@unicauca.edu.co", "pass", "A", "B", "123", "ING");
        Estudiante e2 = new Estudiante("e2@unicauca.edu.co", "pass", "C", "D", "123", "ING");
        p.setEstudiante1(e1);
        assertThrows(IllegalArgumentException.class, () -> p.setEstudiante2(e2));
    }

    @Test
    public void testRequiereCartaEmpresa() {
        ProyectoGrado p1 = new ProyectoGrado();
        p1.setModalidad("INVESTIGACION");
        assertFalse(p1.requiereCartaEmpresa());

        ProyectoGrado p2 = new ProyectoGrado();
        p2.setModalidad("PRACTICA_PROFESIONAL");
        assertTrue(p2.requiereCartaEmpresa());
    }
}