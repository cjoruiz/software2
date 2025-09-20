package co.unicauca.presentation;

import co.unicauca.solid.access.IUserRepository;
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
import java.time.LocalDateTime;
import javafx.scene.control.Label;

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

    private Usuario usuario;
    private IUserRepository userRepository;
    private UserService userService;
    private ProyectoGradoService proyectoService;
    private FilePGService filePGService;
    private byte[] contenidoPDFSeleccionado;
    private String nombreArchivoPDF;

    @FXML
    public void initialize() {
        combModalidad.getItems().addAll("INVESTIGACION", "PRACTICA_PROFESIONAL");
        combModalidad.setValue("INVESTIGACION");
        txtSelectPdf.setText("Ningún archivo seleccionado");
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        // Si el usuario es docente, pre-llenar su email como director
        if (usuario instanceof Docente) {
            txtDirector.setText(usuario.getEmail());
            txtDirector.setDisable(true); // No se puede modificar
        }
    }

    public void setUserRepository(IUserRepository userRepository) {
        this.userRepository = userRepository;
        // Inicializar servicios cuando se establece el repositorio
        this.userService = new UserService(userRepository);
        this.proyectoService = new ProyectoGradoService(
            new co.unicauca.solid.access.ProyectoGradoRepository(userService),
            userService
        );
        this.filePGService = new FilePGService(
            new co.unicauca.solid.access.FilePGRepository(),
            new co.unicauca.solid.access.ProyectoGradoRepository(userService)
        );
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
        if (contenidoPDFSeleccionado == null) {
            showAlert(Alert.AlertType.WARNING, "Advertencia", "Debe seleccionar un archivo PDF antes de enviar.");
            return;
        }

        try {
            // Crear proyecto
            ProyectoGrado proyecto = new ProyectoGrado();
            proyecto.setTitulo(txtTituloP.getText());
            proyecto.setModalidad(combModalidad.getValue());
            proyecto.setDirector((Docente) usuario); // El docente actual es el director
            
            if (!txtCoodirector.getText().trim().isEmpty()) {
                // Buscar codirector por email
                var codirector = userService.findByEmail(txtCoodirector.getText().trim());
                if (codirector instanceof Docente) {
                    proyecto.setCodirector((Docente) codirector);
                } else {
                    throw new InvalidUserDataException("El codirector debe ser un docente.");
                }
            }
            
            var estudiante = userService.findByEmail(txtEstudiante.getText().trim());
            if (estudiante instanceof co.unicauca.solid.domain.Estudiante) {
                proyecto.setEstudiante1((co.unicauca.solid.domain.Estudiante) estudiante);
            } else {
                throw new InvalidUserDataException("El estudiante no es válido.");
            }
            
            proyecto.setObjetivoGeneral(txtObjGeneral.getText());
            proyecto.setObjetivosEspecificos(txtObjEspecifico.getText());

            int idProyecto = proyectoService.crearProyecto(proyecto);

            // Subir el Formato A
            int idDocumento = filePGService.subirFormatoA(idProyecto, contenidoPDFSeleccionado, nombreArchivoPDF);

            if (idProyecto > 0 && idDocumento > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Éxito", "Proyecto creado y Formato A subido correctamente.");
                ((Stage) txtTituloP.getScene().getWindow()).close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "No se pudo crear el proyecto o subir el documento.");
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

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}