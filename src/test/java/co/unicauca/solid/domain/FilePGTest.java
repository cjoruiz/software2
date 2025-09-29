package co.unicauca.solid.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FilePGTest {

    @Test
    public void testConstructorBasico() {
        FilePG doc = new FilePG(1, "FORMATO_A", new byte[]{1,2,3}, "formato.pdf");
        assertEquals(1, doc.getIdProyecto());
        assertEquals("FORMATO_A", doc.getTipoDocumento());
        assertEquals("pdf", doc.getExtension());
        assertEquals(3L, doc.getTamaño());
        assertTrue(doc.esPDF());
    }

    @Test
    public void testEsFormatoA() {
        FilePG doc = new FilePG(1, "FORMATO_A", new byte[]{1}, "f.pdf");
        assertTrue(doc.esFormatoA());
        assertFalse(doc.esCartaEmpresa());
    }

    @Test
    public void testEsCartaEmpresa() {
        FilePG doc = new FilePG(1, "CARTA_EMPRESA", new byte[]{1}, "carta.pdf");
        assertTrue(doc.esCartaEmpresa());
        assertFalse(doc.esFormatoA());
    }

    @Test
    public void testGenerarNombreUnico() {
        FilePG doc = new FilePG(5, "FORMATO_A", 2, "mi_formato.pdf");
        assertEquals("Formato_A_v2_Proyecto_5.pdf", doc.generarNombreUnico());
    }

    @Test
    public void testTamañoFormateado() {
        FilePG doc = new FilePG(1, "FORMATO_A", new byte[2048], "archivo.pdf");
        assertEquals("2,0 KB", doc.getTamañoFormateado());
    }

    @Test
    public void testAprobarYRechazar() {
        FilePG doc = new FilePG(1, "FORMATO_A", new byte[]{1}, "f.pdf");
        doc.aprobar();
        assertEquals("APROBADO", doc.getEstado());

        doc.rechazar("Faltan firmas");
        assertEquals("RECHAZADO", doc.getEstado());
        assertEquals("Faltan firmas", doc.getObservaciones());
    }
}