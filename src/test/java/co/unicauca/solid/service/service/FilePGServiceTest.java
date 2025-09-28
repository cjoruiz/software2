package co.unicauca.solid.service;

import co.unicauca.solid.access.*;
import co.unicauca.solid.domain.*;
import co.unicauca.utilities.exeption.*;
import java.util.List;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilePGServiceTest {

    @Mock private IFilePGRepository fileRepo;
    @Mock private IProyectoGradoRepository pgRepo;
    @InjectMocks private FilePGService service;

    private FilePG docBasico() {
    FilePG d = new FilePG();
    d.setIdProyecto(1);
    d.setNombreArchivo("a.pdf");
    d.setTipoDocumento("FORMATO_A");
    d.setContenido(new byte[1024]);
    return d;
}
    private ProyectoGrado proyectoExistente(int id) {
        ProyectoGrado p = new ProyectoGrado();
        p.setIdProyecto(id);
        return p;
    }
    
    @Test
    void insertarDocumento_ok() throws Exception {
        when(fileRepo.insertarDocumento(any())).thenReturn(7);

        FilePG doc = docBasico();
        assertEquals(7, service.insertarDocumento(doc));
    }

    /*@Test
        void insertarDocumento_proyectoNoExiste() throws InvalidUserDataException {
    FilePG doc = docBasico();   // datos VÁLIDOS
    doc.setIdProyecto(99);      // proyecto inexistente
    when(pgRepo.obtenerProyectoPorId(99)).thenReturn(null);

    // El servicio primero valida → luego lanza UserNotFoundException
    Exception ex = assertThrows(Exception.class,
                                () -> service.insertarDocumento(doc));
    assertTrue(ex instanceof UserNotFoundException);
}*/
    
    @Test
    void subirFormatoA_extensionIncorrecta() throws InvalidUserDataException, InvalidUserDataException, InvalidUserDataException, InvalidUserDataException, InvalidUserDataException, InvalidUserDataException, InvalidUserDataException {
        ProyectoGrado p = new ProyectoGrado();
        p.setIdProyecto(1);
        when(pgRepo.obtenerProyectoPorId(1)).thenReturn(p);

        Exception ex = assertThrows(InvalidUserDataException.class,
                () -> service.subirFormatoA(1, new byte[1024], "formatoA.docx"));
        assertTrue(ex.getMessage().contains("solo puede ser un archivo PDF"));
    }

    @Test
    void subirFormatoA_tamanioExcedido() throws InvalidUserDataException, InvalidUserDataException, InvalidUserDataException, InvalidUserDataException, InvalidUserDataException, InvalidUserDataException, InvalidUserDataException {
        ProyectoGrado p = new ProyectoGrado();
        p.setIdProyecto(1);
        when(pgRepo.obtenerProyectoPorId(1)).thenReturn(p);

        byte[] grande = new byte[25 * 1024 * 1024 + 1]; // 25 MB
        Exception ex = assertThrows(InvalidUserDataException.class,
                () -> service.subirFormatoA(1, grande, "formatoA.pdf"));
        assertTrue(ex.getMessage().contains("20MB"));
    }

    @Test
    void actualizarEstadoDocumento_estadoInvalido() {
        lenient().when(fileRepo.obtenerDocumentoPorId(1)).thenReturn(docBasico());
        assertThrows(InvalidUserDataException.class,
                     () -> service.actualizarEstadoDocumento(1, "INEXISTENTE", null));
    }

    @Test
    void eliminarDocumento_idInexistente() {
        when(fileRepo.obtenerDocumentoPorId(999)).thenReturn(null);

        assertThrows(UserNotFoundException.class,
                () -> service.eliminarDocumento(999));
    }

    @Test
    void descargarDocumento_rutaVacia() {
        lenient().when(fileRepo.obtenerDocumentoPorId(1)).thenReturn(docBasico());
        assertThrows(InvalidUserDataException.class,
                     () -> service.descargarDocumento(1, "", "file.pdf"));
    }

    @Test
    void obtenerFormatoAActual_sinFormato() throws InvalidUserDataException {
        ProyectoGrado p = new ProyectoGrado();
        p.setIdProyecto(1);
        when(pgRepo.obtenerProyectoPorId(1)).thenReturn(p);
        when(fileRepo.obtenerFormatoAActual(1)).thenReturn(null);

        assertThrows(UserNotFoundException.class,
                () -> service.obtenerFormatoAActual(1));
    }
    @Test
    void obtenerDocumentoPorId_existe() throws Exception {
        FilePG d = docBasico();
        d.setIdDocumento(1);
        when(fileRepo.obtenerDocumentoPorId(1)).thenReturn(d);

        FilePG result = service.obtenerDocumentoPorId(1);
        assertEquals(d, result);
    }

    @Test
    void obtenerDocumentoPorId_noExiste() {
        when(fileRepo.obtenerDocumentoPorId(99)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> service.obtenerDocumentoPorId(99));
    }

    @Test
    void obtenerDocumentosPorProyecto_ok() throws Exception {
        when(pgRepo.obtenerProyectoPorId(1)).thenReturn(proyectoExistente(1));
        when(fileRepo.obtenerDocumentosPorProyecto(1)).thenReturn(List.of(docBasico()));

        List<FilePG> lista = service.obtenerDocumentosPorProyecto(1);
        assertEquals(1, lista.size());
    }

    @Test
    void obtenerDocumentosPorProyecto_vacio() throws Exception {
        when(pgRepo.obtenerProyectoPorId(1)).thenReturn(proyectoExistente(1));
        when(fileRepo.obtenerDocumentosPorProyecto(1)).thenReturn(List.of());

        assertThrows(UserNotFoundException.class, () -> service.obtenerDocumentosPorProyecto(1));
    }

    @Test
    void obtenerDocumentosPorProyectoYTipo_ok() throws Exception {
        when(pgRepo.obtenerProyectoPorId(1)).thenReturn(proyectoExistente(1));
        when(fileRepo.obtenerDocumentosPorProyectoYTipo(1, "FORMATO_A")).thenReturn(List.of(docBasico()));

        List<FilePG> lista = service.obtenerDocumentosPorProyectoYTipo(1, "FORMATO_A");
        assertEquals(1, lista.size());
    }

    @Test
    void actualizarEstadoDocumento_ok() throws Exception {
        lenient().when(fileRepo.obtenerDocumentoPorId(1)).thenReturn(docBasico());
        when(fileRepo.actualizarEstadoDocumento(1, "APROBADO", "Ok")).thenReturn(true);

        boolean ok = service.actualizarEstadoDocumento(1, "APROBADO", "Ok");
        assertTrue(ok);
    }
    
    @Test
    void eliminarDocumento_ok() throws Exception {
        lenient().when(fileRepo.obtenerDocumentoPorId(1)).thenReturn(docBasico());
        when(fileRepo.eliminarDocumento(1)).thenReturn(true);

        assertTrue(service.eliminarDocumento(1));
    }

    @Test
    void eliminarDocumento_noExiste() {
        when(fileRepo.obtenerDocumentoPorId(99)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> service.eliminarDocumento(99));
    }

    @Test
    void descargarDocumento_ok() throws Exception {
        lenient().when(fileRepo.obtenerDocumentoPorId(1)).thenReturn(docBasico());
        when(fileRepo.guardarEnDisco(1, "C:/tmp", "a.pdf")).thenReturn(true);

        assertTrue(service.descargarDocumento(1, "C:/tmp", "a.pdf"));
    }

    @Test
    void subirFormatoA_ok() throws Exception {
        when(pgRepo.obtenerProyectoPorId(1)).thenReturn(proyectoExistente(1));
        when(fileRepo.subirFormatoA(1, new byte[1024], "formatoA.pdf")).thenReturn(5);

        int id = service.subirFormatoA(1, new byte[1024], "formatoA.pdf");
        assertEquals(5, id);
    }

    @Test
    void obtenerFormatoAActual_existe() throws Exception {
        when(pgRepo.obtenerProyectoPorId(1)).thenReturn(proyectoExistente(1));
        FilePG formato = docBasico();
        when(fileRepo.obtenerFormatoAActual(1)).thenReturn(formato);

        FilePG result = service.obtenerFormatoAActual(1);
        assertEquals(formato, result);
    }

    @Test
    void obtenerFormatoAActual_noExiste() throws InvalidUserDataException {
        when(pgRepo.obtenerProyectoPorId(1)).thenReturn(proyectoExistente(1));
        when(fileRepo.obtenerFormatoAActual(1)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> service.obtenerFormatoAActual(1));
    }

    @Test
    void obtenerHistorialFormatoA_ok() throws Exception {
        when(pgRepo.obtenerProyectoPorId(1)).thenReturn(proyectoExistente(1));
        when(fileRepo.obtenerHistorialFormatoA(1)).thenReturn(List.of(docBasico()));

        List<FilePG> lista = service.obtenerHistorialFormatoA(1);
        assertEquals(1, lista.size());
    }

    @Test
    void obtenerHistorialFormatoA_vacio() throws InvalidUserDataException {
        when(pgRepo.obtenerProyectoPorId(1)).thenReturn(proyectoExistente(1));
        when(fileRepo.obtenerHistorialFormatoA(1)).thenReturn(List.of());

        assertThrows(UserNotFoundException.class, () -> service.obtenerHistorialFormatoA(1));
    }

    @Test
    void tieneCartaEmpresa_true() throws Exception {
        when(pgRepo.obtenerProyectoPorId(1)).thenReturn(proyectoExistente(1));
        when(fileRepo.tieneCartaEmpresa(1)).thenReturn(true);

        assertTrue(service.tieneCartaEmpresa(1));
    }

    @Test
    void tieneCartaEmpresa_false() throws Exception {
        when(pgRepo.obtenerProyectoPorId(1)).thenReturn(proyectoExistente(1));
        when(fileRepo.tieneCartaEmpresa(1)).thenReturn(false);

        assertFalse(service.tieneCartaEmpresa(1));
    }

    @Test
    void validarDocumentosRequeridos_practicaProfesional() throws Exception {
        when(pgRepo.obtenerProyectoPorId(1)).thenReturn(proyectoExistente(1));
        when(fileRepo.validarDocumentosRequeridos(1, true)).thenReturn(true);

        assertTrue(service.validarDocumentosRequeridos(1, "PRACTICA_PROFESIONAL"));
    }

    @Test
    void obtenerEstadisticasProyecto_ok() throws Exception {
        when(pgRepo.obtenerProyectoPorId(1)).thenReturn(proyectoExistente(1));
        when(fileRepo.obtenerEstadisticasProyecto(1)).thenReturn("Stats: 3 docs, 2 aprobados");

        String stats = service.obtenerEstadisticasProyecto(1);
        assertEquals("Stats: 3 docs, 2 aprobados", stats);
    }
}

