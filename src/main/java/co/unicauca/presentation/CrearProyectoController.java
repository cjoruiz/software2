package co.unicauca.presentation;

import co.unicauca.solid.domain.Docente;
import co.unicauca.solid.domain.ProyectoGrado;
import co.unicauca.solid.domain.Usuario;
import co.unicauca.solid.service.FilePGService;
import co.unicauca.solid.service.ProyectoGradoService;
import co.unicauca.solid.service.UserService;
import co.unicauca.utilities.exeption.InvalidUserDataException;
import co.unicauca.utilities.exeption.UserNotFoundException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.nio.file.Files;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class CrearProyectoController {

    @FXML
    private TextField txtTituloP;
    @FXML
    private ComboBox<String> combModalidad;
    @FXML
    private TextField txtDirector;
    @FXML
    private TextField txtCoodirector;
    @FXML
    private TextField txtEstudiante;
    @FXML
    private TextArea txtObjGeneral;
    @FXML
    private TextArea txtObjEspecifico;
    @FXML
    private Label txtSelectPdf;
    @FXML
    private TextField txtEstudiante1;
    @FXML
    private TextField txtEstudiante2;
    @FXML
    private HBox hboxEstudiante2;
    @FXML
    private HBox hboxCartaAceptacion;
    @FXML
    private Label txtSelectCarta;

    private Usuario usuario;
    private UserService userService;
    private ProyectoGradoService proyectoGradoService; // ← Nombre corregido
    private FilePGService filePGService;
    private byte[] contenidoPDFSeleccionado;
    private String nombreArchivoPDF;
    private byte[] contenidoCartaAceptacion;
    private String nombreArchivoCarta;

    @FXML
    public void initialize() {
        combModalidad.getItems().addAll("INVESTIGACION", "PRACTICA_PROFESIONAL");
        combModalidad.setValue("INVESTIGACION");
        txtSelectPdf.setText("Ningún archivo seleccionado");
        txtSelectCarta.setText("Ningún archivo seleccionado");

        // Inicializar visibilidad
        hboxEstudiante2.setVisible(true);
        hboxCartaAceptacion.setVisible(false);
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if (usuario instanceof Docente) {
            txtDirector.setText(usuario.getEmail());
            txtDirector.setDisable(true);
        }
    }

    // ✅ Solo setters para servicios (sin crearlos internamente)
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setProyectoGradoService(ProyectoGradoService proyectoGradoService) {
        this.proyectoGradoService = proyectoGradoService;
    }

    public void setFilePGService(FilePGService filePGService) {
        this.filePGService = filePGService;
    }

    @FXML
    private void handleAdjuntarPDF(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                contenidoPDFSeleccionado = Files.readAllBytes(file.toPath());
                nombreArchivoPDF = file.getName();
                txtSelectPdf.setText(nombreArchivoPDF);
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "No se pudo leer el archivo PDF.");
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleEnviar(ActionEvent event) {
        String modalidad = combModalidad.getValue();

        // Validaciones comunes
        if (contenidoPDFSeleccionado == null) {
            showAlert(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar el Formato A antes de enviar.");
            return;
        }

        if ("PRACTICA_PROFESIONAL".equals(modalidad)) {
            if (contenidoCartaAceptacion == null) {
                showAlert(Alert.AlertType.WARNING, "Advertencia", "Debe adjuntar la carta de aceptación de la empresa para prácticas profesionales.");
                return;
            }
            if (!txtEstudiante2.getText().trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Advertencia", "En prácticas profesionales solo se permite un estudiante.");
                return;
            }
        }

        try {
            ProyectoGrado proyecto = new ProyectoGrado();
            proyecto.setTitulo(txtTituloP.getText());
            proyecto.setModalidad(modalidad);
            proyecto.setDirector((Docente) usuario);

            // Codirector (opcional)
            if (!txtCoodirector.getText().trim().isEmpty()) {
                var codirector = userService.findByEmail(txtCoodirector.getText().trim());
                if (codirector instanceof Docente) {
                    proyecto.setCodirector((Docente) codirector);
                } else {
                    throw new InvalidUserDataException("El codirector debe ser un docente.");
                }
            }

            // Estudiante 1 (obligatorio)
            String emailEstudiante1 = txtEstudiante1.getText().trim();
            if (emailEstudiante1.isEmpty()) {
                throw new InvalidUserDataException("El email del estudiante 1 es obligatorio.");
            }
            var estudiante1 = userService.findByEmail(emailEstudiante1);
            if (estudiante1 instanceof co.unicauca.solid.domain.Estudiante) {
                proyecto.setEstudiante1((co.unicauca.solid.domain.Estudiante) estudiante1);
            } else {
                throw new InvalidUserDataException("El estudiante 1 no es válido.");
            }

            // Estudiante 2 (solo para investigación y opcional)
            if ("INVESTIGACION".equals(modalidad)) {
                String emailEstudiante2 = txtEstudiante2.getText().trim();
                if (!emailEstudiante2.isEmpty()) {
                    var estudiante2 = userService.findByEmail(emailEstudiante2);
                    if (estudiante2 instanceof co.unicauca.solid.domain.Estudiante) {
                        proyecto.setEstudiante2((co.unicauca.solid.domain.Estudiante) estudiante2);
                    } else {
                        throw new InvalidUserDataException("El estudiante 2 no es válido.");
                    }
                }
                // Si está vacío, no se asigna estudiante2 (es válido)
            }

            proyecto.setObjetivoGeneral(txtObjGeneral.getText());
            proyecto.setObjetivosEspecificos(txtObjEspecifico.getText());

            // Crear proyecto
            int idProyecto = proyectoGradoService.crearProyecto(proyecto);

            // Subir Formato A
            int idDocumentoA = filePGService.subirFormatoA(idProyecto, contenidoPDFSeleccionado, nombreArchivoPDF);

            // Subir carta de aceptación si es práctica
            int idDocumentoCarta = -1;
            if ("PRACTICA_PROFESIONAL".equals(modalidad)) {
                idDocumentoCarta = filePGService.subirCartaEmpresa(idProyecto, contenidoCartaAceptacion,
                 "Carta_Aceptacion_" + idProyecto + ".pdf");
            }

            if (idProyecto > 0 && idDocumentoA > 0
                    && ("INVESTIGACION".equals(modalidad) || idDocumentoCarta > 0)) {

                showAlert(Alert.AlertType.INFORMATION, "Éxito",
                        "Proyecto creado y documentos subidos correctamente.");
                ((Stage) txtTituloP.getScene().getWindow()).close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "No se pudo crear el proyecto o subir los documentos.");
            }

        } catch (InvalidUserDataException | UserNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "Error de Validación", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error Inesperado", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        ((Stage) txtTituloP.getScene().getWindow()).close();
    }

    @FXML
    private void handleCambioModalidad() {
        String modalidad = combModalidad.getValue();
        if ("PRACTICA_PROFESIONAL".equals(modalidad)) {
            // Solo 1 estudiante, mostrar carta de aceptación
            hboxEstudiante2.setVisible(false);
            hboxCartaAceptacion.setVisible(true);
            txtEstudiante2.clear(); // Limpiar por si había algo
        } else {
            // Investigación: 2 estudiantes, sin carta
            hboxEstudiante2.setVisible(true);
            hboxCartaAceptacion.setVisible(false);
            contenidoCartaAceptacion = null;
            nombreArchivoCarta = null;
            txtSelectCarta.setText("Ningún archivo seleccionado");
        }
    }

    @FXML
    private void handleAdjuntarCartaAceptacion(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar carta de aceptación de la empresa");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                contenidoCartaAceptacion = Files.readAllBytes(file.toPath());
                nombreArchivoCarta = file.getName();
                txtSelectCarta.setText(nombreArchivoCarta);
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "No se pudo leer el archivo PDF.");
                e.printStackTrace();
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
