package co.unicauca.presentation;

import co.unicauca.presentation.App;
import co.unicauca.solid.access.Factory;
import co.unicauca.solid.access.IMensajeInternoRepository;
import co.unicauca.solid.domain.Estudiante;
import co.unicauca.solid.domain.MensajeInterno;
import co.unicauca.solid.domain.ProyectoGrado;
import co.unicauca.solid.service.FilePGService;
import co.unicauca.solid.service.MensajeInternoService;
import co.unicauca.solid.service.ProyectoGradoService;
import co.unicauca.solid.service.UserService;
import co.unicauca.utilities.exeption.InvalidUserDataException;
import co.unicauca.utilities.exeption.UserNotFoundException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HomepageEstudianteController {

    @FXML
    private VBox vistaDetallesProyecto;
    @FXML
    private Label lblTituloProyecto;
    @FXML
    private Label lblModalidadProyecto;
    @FXML
    private Label lblDirectorProyecto;
    @FXML
    private Label lblCodirectorProyecto;
    @FXML
    private Label lblEstudiante1Proyecto;
    @FXML
    private Label lblEstudiante2Proyecto;
    @FXML
    private Label lblEstadoProyecto;
    @FXML
    private Label lblIntentoProyecto;
    @FXML
    private Label lblFechaCreacionProyecto;
    @FXML
    private TextArea txtObjetivoGeneralProyecto;
    @FXML
    private TextArea txtObjetivosEspecificosProyecto;
    @FXML
    private TextArea txtObservacionesEvaluacion;
    @FXML
    private Label nombreLabel;
    @FXML
    private Label programaLabel;
    @FXML
    private Label celularLabel;
    @FXML
    private Label tituloVista;
    @FXML
    private TableView<ProyectoGrado> tablaProyectos;
    @FXML
    private TableColumn<ProyectoGrado, Integer> colId;
    @FXML
    private TableColumn<ProyectoGrado, String> colTitulo;
    @FXML
    private TableColumn<ProyectoGrado, String> colDirector;
    @FXML
    private TableColumn<ProyectoGrado, String> colEstado;
    @FXML
    private TableColumn<ProyectoGrado, Integer> colIntento;
    @FXML
    private TextField txtDestinatarios;
    @FXML
    private TextField txtAsunto;
    @FXML
    private TextArea txtCuerpo;
    @FXML
    private Label lblArchivo;
    @FXML
    private ListView<String> listaMensajesEstudiante;
    @FXML
    private ListView<String> listaArchivosEstudiante;
    @FXML
    private VBox vistaProyectos;
    @FXML
    private VBox vistaEnviarMensaje;
    @FXML
    private VBox vistaRecibidos;
    @FXML
    private VBox vistaArchivosEnviados;
    @FXML
    private Button btnDescargarFormatoA;

    private FilePGService filePGService;
    private Estudiante estudianteActual;
    private ProyectoGradoService proyectoGradoService;
    private UserService userService;
    private MensajeInternoService mensajeInternoService; // ✅ Nuevo servicio
    private byte[] archivoAdjunto;
    private String nombreArchivo;
    private Stage primaryStage;
    private ProyectoGrado proyectoSeleccionado;

    public void setUsuario(Estudiante estudiante) {
        this.estudianteActual = estudiante;
        if (nombreLabel != null) {
            nombreLabel.setText(estudiante.getNombres() + " " + estudiante.getApellidos());
            programaLabel.setText(estudiante.getPrograma());
            celularLabel.setText(estudiante.getCelular() != null ? estudiante.getCelular() : "No registrado");
        }
        if (primaryStage != null) {
            primaryStage.setWidth(950);
            primaryStage.setHeight(800);
            primaryStage.centerOnScreen();
        }
        if (tablaProyectos != null) {
            cargarProyectosDelEstudiante();
        }
    }

    public void setFilePGService(FilePGService filePGService) {
        this.filePGService = filePGService;
    }

    public void setProyectoGradoService(ProyectoGradoService proyectoGradoService) {
        this.proyectoGradoService = proyectoGradoService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    // ✅ Setter para el nuevo servicio
    public void setMensajeInternoService(MensajeInternoService mensajeInternoService) {
        this.mensajeInternoService = mensajeInternoService;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    public void initialize() {
        configurarTablaProyectos();
        mostrarVistaProyectos();
    }

    @FXML
    private void handleDescargarPlantillaFormatoA() {
        try {
            // Cargar la plantilla desde el classpath (archivo .doc)
            InputStream plantillaStream = getClass().getResourceAsStream("/plantillas/formatoA.doc");

            if (plantillaStream == null) {
                mostrarError("No se encontró la plantilla del Formato A. Contacte al administrador.");
                return;
            }

            // Configurar el diálogo de guardado para archivos .doc
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Plantilla Formato A");
            fileChooser.setInitialFileName("formatoA.doc");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Documentos Word", "*.doc"));

            // Mostrar diálogo y obtener archivo seleccionado
            File archivoDestino = fileChooser.showSaveDialog(primaryStage);

            if (archivoDestino != null) {
                // Asegurar que tenga extensión .doc
                String nombreArchivo = archivoDestino.getName();
                if (!nombreArchivo.toLowerCase().endsWith(".doc")) {
                    archivoDestino = new File(archivoDestino.getParentFile(), nombreArchivo + ".doc");
                }

                // Guardar el archivo
                try (InputStream input = plantillaStream) {
                    Files.copy(input, archivoDestino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }

                mostrarExito("Plantilla guardada exitosamente en:\n" + archivoDestino.getAbsolutePath());
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al descargar la plantilla:\n" + e.getMessage());
        }
    }

    private void configurarTablaProyectos() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idProyecto"));
        colTitulo.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getTitulo()));
        colDirector.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(
                p.getValue().getDirector() != null ? p.getValue().getDirector().getEmail() : "N/A"
        ));
        colEstado.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(
                proyectoGradoService.obtenerEstadoLegible(p.getValue().getEstadoActual())
        ));
        colIntento.setCellValueFactory(new PropertyValueFactory<>("numeroIntento"));

        // Configurar selección de fila
        tablaProyectos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                mostrarDetallesProyecto(newSelection);
            }
        });
    }

@FXML
private void handleVerProyectos() {
    vistaProyectos.setVisible(true);
    vistaEnviarMensaje.setVisible(false);
    vistaRecibidos.setVisible(false);
    vistaArchivosEnviados.setVisible(false);
    vistaDetallesProyecto.setVisible(false);
    tituloVista.setText("Mis Proyectos");
    cargarProyectosDelEstudiante();
}

    @FXML
    private void handleVerEnviarMensaje() {
        vistaProyectos.setVisible(false);
        vistaEnviarMensaje.setVisible(true);
        vistaRecibidos.setVisible(false);
        vistaArchivosEnviados.setVisible(false);
        tituloVista.setText("Enviar Mensaje");
        limpiarFormularioMensaje();
    }

    @FXML
    private void handleVerRecibidos() {
        mostrarVistaRecibidos();
        cargarMensajesRecibidos();
    }

    @FXML
    private void handleVerArchivosEnviados() {
        mostrarVistaArchivosEnviados();
        cargarArchivosEnviados(); // ✅ Ahora carga los reales
    }

    @FXML
    private void handleAdjuntarArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo para adjuntar");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Todos los archivos", "*.*"));

        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            try {
                archivoAdjunto = Files.readAllBytes(file.toPath());
                nombreArchivo = file.getName();
                lblArchivo.setText(nombreArchivo);
            } catch (Exception e) {
                mostrarError("No se pudo leer el archivo: " + e.getMessage());
                archivoAdjunto = null;
                nombreArchivo = null;
            }
        }
    }

    @FXML
    private void handleEnviarMensaje() {
        try {
            String destinatarios = txtDestinatarios.getText().trim();
            String asunto = txtAsunto.getText().trim();
            String cuerpo = txtCuerpo.getText().trim();

            if (destinatarios.isEmpty() || asunto.isEmpty() || cuerpo.isEmpty()) {
                mostrarError("Todos los campos son obligatorios");
                return;
            }

            int idMensaje = mensajeInternoService.enviarMensaje(
                    estudianteActual.getEmail(),
                    destinatarios,
                    asunto,
                    cuerpo,
                    archivoAdjunto,
                    nombreArchivo
            );

            if (idMensaje > 0) {
                mostrarExito("Mensaje enviado exitosamente con ID: " + idMensaje);
                handleCancelarMensaje();
            } else {
                mostrarError("❌ Error al enviar el mensaje.");
            }

        } catch (InvalidUserDataException | UserNotFoundException e) {
            mostrarError("❌ Error: " + e.getMessage());
        } catch (Exception e) {
            mostrarError("❌ Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelarMensaje() {
        mostrarVistaProyectos();
        limpiarFormularioMensaje();
    }

private void mostrarVistaProyectos() {
    vistaProyectos.setVisible(true);
    vistaEnviarMensaje.setVisible(false);
    vistaRecibidos.setVisible(false);
    vistaArchivosEnviados.setVisible(false);
    vistaDetallesProyecto.setVisible(false);
    tituloVista.setText("Mis Proyectos de Grado");
}

    private void mostrarVistaRecibidos() {
        vistaProyectos.setVisible(false);
        vistaEnviarMensaje.setVisible(false);
        vistaRecibidos.setVisible(true);
        vistaArchivosEnviados.setVisible(false);
        tituloVista.setText("Mensajes Recibidos");
    }

    private void mostrarVistaArchivosEnviados() {
        vistaProyectos.setVisible(false);
        vistaEnviarMensaje.setVisible(false);
        vistaRecibidos.setVisible(false);
        vistaArchivosEnviados.setVisible(true);
        tituloVista.setText("Archivos Enviados");
    }

    private void limpiarFormularioMensaje() {
        if (txtDestinatarios != null) {
            txtDestinatarios.clear();
            txtAsunto.clear();
            txtCuerpo.clear();
            lblArchivo.setText("Ningún archivo seleccionado");
            archivoAdjunto = null;
            nombreArchivo = null;
        }
    }

    private void cargarProyectosDelEstudiante() {
        try {
            if (estudianteActual == null) {
                System.out.println("Advertencia: estudianteActual es null, no se pueden cargar proyectos");
                return;
            }

            List<ProyectoGrado> proyectos = proyectoGradoService.obtenerProyectosPorEstudiante(estudianteActual.getEmail());
            ObservableList<ProyectoGrado> lista = FXCollections.observableArrayList(proyectos);
            tablaProyectos.setItems(lista);
        } catch (Exception e) {
            e.printStackTrace();
            tablaProyectos.setItems(FXCollections.observableArrayList());
        }
    }

    private void cargarMensajesRecibidos() {
        listaMensajesEstudiante.getItems().clear();
        // Los estudiantes no reciben mensajes en este flujo
        listaMensajesEstudiante.getItems().add("Los estudiantes solo envían mensajes a docentes y coordinadores.");
    }

    // ✅ Método corregido: carga los archivos reales enviados
    private void cargarArchivosEnviados() {
        listaArchivosEstudiante.getItems().clear();

        if (estudianteActual == null || mensajeInternoService == null) {
            listaArchivosEstudiante.getItems().add("No hay datos disponibles.");
            return;
        }

        try {
            List<MensajeInterno> mensajesEnviados = mensajeInternoService
                    .obtenerMensajesEnviadosPorEstudiante(estudianteActual.getEmail());

            if (mensajesEnviados.isEmpty()) {
                listaArchivosEstudiante.getItems().add("No has enviado ningún mensaje con archivo adjunto.");
            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                boolean hayArchivos = false;
                for (MensajeInterno mensaje : mensajesEnviados) {
                    // ✅ Verificar que haya documento adjunto (no nulo y con tamaño > 0)
                    if (mensaje.getDocumentoAdjunto() != null && mensaje.getDocumentoAdjunto().length > 0) {
                        // ✅ Verificar que el nombre del archivo no sea nulo ni vacío
                        if (mensaje.getNombreArchivo() != null && !mensaje.getNombreArchivo().trim().isEmpty()) {
                            String fecha = mensaje.getFechaEnvio() != null
                                    ? mensaje.getFechaEnvio().format(formatter)
                                    : "Fecha no disponible";
                            String item = mensaje.getNombreArchivo() + " - Enviado el " + fecha;
                            listaArchivosEstudiante.getItems().add(item);
                            hayArchivos = true;
                        }
                    }
                }
                if (!hayArchivos) {
                    listaArchivosEstudiante.getItems().add("No has enviado archivos adjuntos.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            listaArchivosEstudiante.getItems().add("Error al cargar archivos enviados.");
        }
    }

    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarDetallesProyecto(ProyectoGrado proyecto) {
        this.proyectoSeleccionado = proyecto;

        // Llenar los datos del proyecto
        lblTituloProyecto.setText("Título: " + (proyecto.getTitulo() != null ? proyecto.getTitulo() : "Sin título"));
        lblModalidadProyecto.setText(proyecto.getModalidad() != null ? proyecto.getModalidad() : "N/A");
        lblDirectorProyecto.setText(proyecto.getDirector() != null ? proyecto.getDirector().getEmail() : "N/A");
        lblCodirectorProyecto.setText(proyecto.getCodirector() != null ? proyecto.getCodirector().getEmail() : "N/A");
        lblEstudiante1Proyecto.setText(proyecto.getEstudiante1() != null ? proyecto.getEstudiante1().getEmail() : "N/A");
        lblEstudiante2Proyecto.setText(proyecto.getEstudiante2() != null ? proyecto.getEstudiante2().getEmail() : "N/A");
        lblEstadoProyecto.setText(proyectoGradoService.obtenerEstadoLegible(proyecto.getEstadoActual()));
        lblIntentoProyecto.setText(String.valueOf(proyecto.getNumeroIntento()));

        String fechaCreacion = proyecto.getFechaCreacion() != null
                ? proyecto.getFechaCreacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : "No disponible";
        lblFechaCreacionProyecto.setText(fechaCreacion);

        txtObjetivoGeneralProyecto.setText(proyecto.getObjetivoGeneral() != null ? proyecto.getObjetivoGeneral() : "");
        txtObjetivosEspecificosProyecto.setText(proyecto.getObjetivosEspecificos() != null ? proyecto.getObjetivosEspecificos() : "");
        txtObservacionesEvaluacion.setText(proyecto.getObservacionesEvaluacion() != null ? proyecto.getObservacionesEvaluacion() : "");

        // Cambiar a la vista de detalles
        vistaProyectos.setVisible(false);
        vistaEnviarMensaje.setVisible(false);
        vistaRecibidos.setVisible(false);
        vistaArchivosEnviados.setVisible(false);
        vistaDetallesProyecto.setVisible(true);
        tituloVista.setText("Detalles del Proyecto");
    }

    @FXML
    private void handleVolverDeDetallesProyecto() {
        vistaDetallesProyecto.setVisible(false);
        vistaProyectos.setVisible(true);
        vistaEnviarMensaje.setVisible(false);
        vistaRecibidos.setVisible(false);
        vistaArchivosEnviados.setVisible(false);
        tituloVista.setText("Mis Proyectos de Grado");
        cargarProyectosDelEstudiante();
    }

    @FXML
    private void handleCerrarSesion() {
        App.setRoot("login");
    }
}
