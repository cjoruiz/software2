package co.unicauca.presentation;

import co.unicauca.solid.domain.Docente;
import co.unicauca.solid.domain.MensajeInterno;
import co.unicauca.solid.domain.ProyectoGrado;
import co.unicauca.solid.service.FilePGService;
import co.unicauca.solid.service.MensajeInternoService;
import co.unicauca.solid.service.ProyectoGradoService;
import co.unicauca.solid.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HomepageDocenteController {

    @FXML
    private Label profNameLabel;
    @FXML
    private Label profEmailLabel;
    @FXML
    private Label progTextLabel;
    @FXML
    private Label tituloVista;
    @FXML
    private ListView<String> listaMensajesDocente;
    @FXML
    private ListView<String> listaArchivosEnviados;
    @FXML
    private VBox vistaRecibidos;
    @FXML
    private VBox vistaArchivosEnviados;
    @FXML
    private VBox vistaProyectosDocente;
    @FXML
    private TableView<ProyectoGrado> tablaProyectosDocente;
    @FXML
    private TableColumn<ProyectoGrado, Integer> colIdDoc;
    @FXML
    private TableColumn<ProyectoGrado, String> colTituloDoc;
    @FXML
    private TableColumn<ProyectoGrado, String> colEstudianteDoc;
    @FXML
    private TableColumn<ProyectoGrado, String> colEstadoDoc;
    @FXML
    private TableColumn<ProyectoGrado, Integer> colIntentoDoc;
    @FXML
    private TableColumn<ProyectoGrado, Void> colAccionesDoc;
    @FXML
    private VBox vistaSubirNuevaVersion;
    @FXML
    private Label lblNuevoArchivo;
    @FXML
    private VBox vistaDetallesMensaje;
    @FXML
    private Label lblAsuntoMensaje;
    @FXML
    private Label lblRemitenteMensaje;
    @FXML
    private Label lblFechaMensaje;
    @FXML
    private TextArea txtCuerpoMensaje;
    @FXML
    private Button btnDescargarArchivo;
    @FXML
    private ScrollPane scrollPaneDetallesProyecto;
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
    private Button btnSubirNuevaVersionProyecto;

    private ProyectoGrado proyectoSeleccionado;
    private MensajeInterno mensajeSeleccionado;

    private Docente usuarioActual;
    private UserService userService;
    private ProyectoGradoService proyectoGradoService;
    private FilePGService filePGService;
    private Stage primaryStage;
    private MensajeInternoService mensajeInternoService;
    private ProyectoGrado proyectoSeleccionadoParaReintento;
    private byte[] nuevoContenidoPDF;
    private String nuevoNombreArchivo;

    public void setUsuario(Docente usuario) {
        this.usuarioActual = usuario;
        if (profNameLabel != null) {
            profNameLabel.setText(usuario.getNombres() + " " + usuario.getApellidos());
            profEmailLabel.setText(usuario.getEmail());
            progTextLabel.setText(usuario.getPrograma());
        }

        // Establecer el tamaño de la ventana
        if (primaryStage != null) {
            primaryStage.setWidth(950);
            primaryStage.setHeight(800);
            primaryStage.centerOnScreen();
        }

        if (listaMensajesDocente != null) {
            cargarMensajesRecibidos();
            cargarArchivosEnviados();
        }
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setProyectoGradoService(ProyectoGradoService proyectoGradoService) {
        this.proyectoGradoService = proyectoGradoService;
    }

    public void setFilePGService(FilePGService filePGService) {
        this.filePGService = filePGService;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setMensajeInternoService(MensajeInternoService mensajeInternoService) {
        this.mensajeInternoService = mensajeInternoService;
    }

    @FXML
    public void initialize() {
        configurarTablaProyectosDocente();
        mostrarVistaRecibidos();
    }

    private void configurarTablaProyectosDocente() {
        colIdDoc.setCellValueFactory(new PropertyValueFactory<>("idProyecto"));
        colTituloDoc.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getTitulo()));
        colEstudianteDoc.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(
                p.getValue().getEstudiante1() != null ? p.getValue().getEstudiante1().getEmail() : "N/A"
        ));
        colEstadoDoc.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(
                proyectoGradoService.obtenerEstadoLegible(p.getValue().getEstadoActual())
        ));
        colIntentoDoc.setCellValueFactory(new PropertyValueFactory<>("numeroIntento"));

        tablaProyectosDocente.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                mostrarDetallesProyecto(newSelection);
            }
        });

        colAccionesDoc.setCellFactory(param -> new TableCell<ProyectoGrado, Void>() {
            private final Button subirButton = new Button("Subir Nueva Versión");

            {
                subirButton.setOnAction(event -> {
                    ProyectoGrado proyecto = getTableView().getItems().get(getIndex());
                    manejarSubirNuevaVersion(proyecto);
                });
                subirButton.setStyle("-fx-background-color: #00cc99; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    ProyectoGrado proyecto = getTableView().getItems().get(getIndex());
                    if ("FORMATO_A_RECHAZADO".equals(proyecto.getEstadoActual()) && proyecto.getNumeroIntento() < 3) {
                        setGraphic(subirButton);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
    }

    private void manejarSubirNuevaVersion(ProyectoGrado proyecto) {
        if ("FORMATO_A_RECHAZADO".equals(proyecto.getEstadoActual()) && proyecto.getNumeroIntento() < 3) {
            proyectoSeleccionadoParaReintento = proyecto;
            vistaSubirNuevaVersion.setVisible(true);
            lblNuevoArchivo.setText("Ningún archivo seleccionado");
            nuevoContenidoPDF = null;
            nuevoNombreArchivo = null;
        } else if (proyecto.getNumeroIntento() >= 3) {
            mostrarError("Este proyecto fue rechazado definitivamente. No se permiten más intentos.");
        } else {
            mostrarError("Solo se puede subir nueva versión a proyectos con formato A rechazado.");
        }
    }

    @FXML
    private void handleCrearProyecto() {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/crear_proyecto.fxml"));
            Parent root = loader.load();

            CrearProyectoController controller = loader.getController();
            controller.setUsuario(usuarioActual);
            controller.setUserService(userService);
            controller.setProyectoGradoService(proyectoGradoService);
            controller.setFilePGService(filePGService);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Crear Proyecto de Grado");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("No se pudo abrir la ventana de creación de proyecto:\n" + e.getMessage());
        }
    }

    @FXML
    private void handleVerProyectosDocente() {
        vistaRecibidos.setVisible(false);
        vistaProyectosDocente.setVisible(true);
        vistaArchivosEnviados.setVisible(false);
        scrollPaneDetallesProyecto.setVisible(false);
        vistaSubirNuevaVersion.setVisible(false);
        vistaDetallesMensaje.setVisible(false);
        tituloVista.setText("Mis Proyectos de Grado");
        cargarProyectosDelDocente();
    }

    @FXML
    private void handleVerRecibidos() {
        vistaRecibidos.setVisible(true);
        vistaProyectosDocente.setVisible(false);
        vistaArchivosEnviados.setVisible(false);
        scrollPaneDetallesProyecto.setVisible(false);
        vistaDetallesMensaje.setVisible(false);
        tituloVista.setText("Mensajes Recibidos");
        cargarMensajesRecibidos();
    }

    @FXML
    private void handleVerArchivosEnviados() {
        vistaRecibidos.setVisible(false);
        vistaProyectosDocente.setVisible(false);
        vistaArchivosEnviados.setVisible(true);
        scrollPaneDetallesProyecto.setVisible(false);
        tituloVista.setText("Archivos Enviados");
        cargarArchivosEnviados();
    }

    private void mostrarVistaRecibidos() {
        vistaRecibidos.setVisible(true);
        vistaProyectosDocente.setVisible(false);
        vistaArchivosEnviados.setVisible(false);
        scrollPaneDetallesProyecto.setVisible(false);
        vistaDetallesMensaje.setVisible(false);
        tituloVista.setText("Mensajes Recibidos");
    }

    private void cargarProyectosDelDocente() {
        try {
            List<ProyectoGrado> proyectos = proyectoGradoService.obtenerProyectosPorDirector(usuarioActual.getEmail());
            ObservableList<ProyectoGrado> lista = FXCollections.observableArrayList(proyectos);
            tablaProyectosDocente.setItems(lista);
        } catch (Exception e) {
            e.printStackTrace();
            tablaProyectosDocente.setItems(FXCollections.observableArrayList());
            mostrarError("Error al cargar los proyectos.");
        }
    }

    private void cargarMensajesRecibidos() {
        listaMensajesDocente.getItems().clear();
        listaMensajesDocente.getSelectionModel().clearSelection();

        if (usuarioActual == null || mensajeInternoService == null) {
            listaMensajesDocente.getItems().add("No hay datos disponibles.");
            return;
        }

        try {
            List<MensajeInterno> mensajesRecibidos = mensajeInternoService
                    .obtenerMensajesRecibidosPorDocente(usuarioActual.getEmail());

            if (mensajesRecibidos.isEmpty()) {
                listaMensajesDocente.getItems().add("No tienes mensajes nuevos.");
            } else {
                listaMensajesDocente.getItems().clear();

                listaMensajesDocente.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                    if (newSelection != null && !newSelection.equals("No tienes mensajes nuevos.")) {
                        for (MensajeInterno mensaje : mensajesRecibidos) {
                            String fecha = mensaje.getFechaEnvio() != null
                                    ? mensaje.getFechaEnvio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                                    : "Fecha no disponible";
                            String item = "De: " + mensaje.getRemitenteEmail() + " | " + mensaje.getAsunto() + " | " + fecha;
                            if (item.equals(newSelection)) {
                                mostrarDetallesMensaje(mensaje);
                                break;
                            }
                        }
                    }
                });

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                for (MensajeInterno mensaje : mensajesRecibidos) {
                    String fecha = mensaje.getFechaEnvio() != null
                            ? mensaje.getFechaEnvio().format(formatter)
                            : "Fecha no disponible";
                    String item = "De: " + mensaje.getRemitenteEmail() + " | " + mensaje.getAsunto() + " | " + fecha;
                    listaMensajesDocente.getItems().add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            listaMensajesDocente.getItems().add("Error al cargar mensajes recibidos.");
        }
    }

    private void mostrarDetallesMensaje(MensajeInterno mensaje) {
        this.mensajeSeleccionado = mensaje;

        lblAsuntoMensaje.setText("Asunto: " + (mensaje.getAsunto() != null ? mensaje.getAsunto() : "Sin asunto"));
        lblRemitenteMensaje.setText("De: " + mensaje.getRemitenteEmail());
        String fecha = mensaje.getFechaEnvio() != null
                ? mensaje.getFechaEnvio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : "Fecha no disponible";
        lblFechaMensaje.setText("Fecha: " + fecha);
        txtCuerpoMensaje.setText(mensaje.getCuerpo() != null ? mensaje.getCuerpo() : "");

        boolean tieneArchivo = mensaje.getDocumentoAdjunto() != null
                && mensaje.getDocumentoAdjunto().length > 0
                && mensaje.getNombreArchivo() != null
                && !mensaje.getNombreArchivo().trim().isEmpty();
        btnDescargarArchivo.setVisible(tieneArchivo);

        vistaRecibidos.setVisible(false);
        vistaProyectosDocente.setVisible(false);
        vistaArchivosEnviados.setVisible(false);
        scrollPaneDetallesProyecto.setVisible(false);
        vistaDetallesMensaje.setVisible(true);
        tituloVista.setText("Detalles del Mensaje");
    }

    @FXML
    private void handleDescargarArchivoAdjunto() {
        if (mensajeSeleccionado == null || mensajeSeleccionado.getDocumentoAdjunto() == null) {
            mostrarError("No hay archivo adjunto para descargar.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar archivo adjunto");
        fileChooser.setInitialFileName(mensajeSeleccionado.getNombreArchivo());

        if (mensajeSeleccionado.getNombreArchivo().toLowerCase().endsWith(".pdf")) {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        }

        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            try {
                Files.write(file.toPath(), mensajeSeleccionado.getDocumentoAdjunto());
                mostrarExito("Archivo guardado exitosamente en: " + file.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
                mostrarError("Error al guardar el archivo: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleVolverDeDetalles() {
        vistaDetallesMensaje.setVisible(false);
        vistaRecibidos.setVisible(true);
        vistaProyectosDocente.setVisible(false);
        vistaArchivosEnviados.setVisible(false);
        scrollPaneDetallesProyecto.setVisible(false);
        tituloVista.setText("Mensajes Recibidos");
        cargarMensajesRecibidos();
    }

    private void cargarArchivosEnviados() {
        listaArchivosEnviados.getItems().clear();

        if (usuarioActual == null || mensajeInternoService == null) {
            listaArchivosEnviados.getItems().add("No hay datos disponibles.");
            return;
        }

        try {
            List<MensajeInterno> mensajesEnviados = mensajeInternoService
                    .obtenerMensajesRecibidosPorDocente(usuarioActual.getEmail());

            if (mensajesEnviados.isEmpty()) {
                listaArchivosEnviados.getItems().add("No has enviado ningún mensaje con archivo adjunto.");
            } else {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                boolean hayArchivos = false;
                for (MensajeInterno mensaje : mensajesEnviados) {
                    if (mensaje.getDocumentoAdjunto() != null && mensaje.getDocumentoAdjunto().length > 0) {
                        if (mensaje.getNombreArchivo() != null && !mensaje.getNombreArchivo().trim().isEmpty()) {
                            String fecha = mensaje.getFechaEnvio() != null
                                    ? mensaje.getFechaEnvio().format(formatter)
                                    : "Fecha no disponible";
                            String item = mensaje.getNombreArchivo() + " - Enviado el " + fecha;
                            listaArchivosEnviados.getItems().add(item);
                            hayArchivos = true;
                        }
                    }
                }
                if (!hayArchivos) {
                    listaArchivosEnviados.getItems().add("No has enviado archivos adjuntos.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            listaArchivosEnviados.getItems().add("Error al cargar archivos enviados.");
        }
    }

    @FXML
    private void handleSeleccionarNuevoPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar nueva versión del Formato A");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            try {
                nuevoContenidoPDF = Files.readAllBytes(file.toPath());
                nuevoNombreArchivo = file.getName();
                lblNuevoArchivo.setText(nuevoNombreArchivo);
            } catch (Exception e) {
                mostrarError("No se pudo leer el archivo PDF.");
                e.printStackTrace();
            }
        }
    }

@FXML
private void handleSubirNuevaVersion() {
    if (nuevoContenidoPDF == null) {
        mostrarError("Debe seleccionar un archivo PDF.");
        return;
    }
    if (proyectoSeleccionadoParaReintento == null) {
        mostrarError("No se ha seleccionado un proyecto.");
        return;
    }

    try {
        // Procesar reintento (cambia estado a "en reintento")
        boolean reintentoOk = proyectoGradoService.procesarReintentoFormatoA(proyectoSeleccionadoParaReintento.getIdProyecto());
        if (!reintentoOk) {
            mostrarError("No se pudo procesar el reintento. Verifique el estado del proyecto.");
            return;
        }

        // Recargar el proyecto para obtener el número de intento actualizado
        ProyectoGrado proyectoActualizado = proyectoGradoService.obtenerProyecto(proyectoSeleccionadoParaReintento.getIdProyecto());
        
        // ✅ USAR EL NUMERO DE INTENTO COMO VERSIÓN (no +1)
        int versionActual = proyectoActualizado.getNumeroIntento();
        
        // Generar nombre de archivo con la versión correcta
        String nombreArchivo = String.format("FormatoA_Proyecto_%d_v%d.pdf",
                proyectoActualizado.getIdProyecto(),
                versionActual  // ← Versión = número de intento actual
        );

        int idDocumento = filePGService.subirFormatoA(
                proyectoActualizado.getIdProyecto(),
                nuevoContenidoPDF,
                nombreArchivo
        );

        if (idDocumento > 0) {
            mostrarExito("Versión " + versionActual + " subida correctamente.");
            cargarProyectosDelDocente(); // Refrescar tabla
            handleCancelarSubir();
        } else {
            mostrarError("Error al subir el nuevo archivo.");
        }

    } catch (Exception e) {
        mostrarError("Error: " + e.getMessage());
        e.printStackTrace();
    }
}

    @FXML
    private void handleCancelarSubir() {
        vistaSubirNuevaVersion.setVisible(false);
        nuevoContenidoPDF = null;
        nuevoNombreArchivo = null;
        lblNuevoArchivo.setText("Ningún archivo seleccionado");
        proyectoSeleccionadoParaReintento = null;
    }

    @FXML
    private void handleCerrarSesion() {
        App.setRoot("login");
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

        boolean puedeSubirNuevaVersion = "FORMATO_A_RECHAZADO".equals(proyecto.getEstadoActual()) && proyecto.getNumeroIntento() < 3;
        btnSubirNuevaVersionProyecto.setVisible(puedeSubirNuevaVersion);

        vistaRecibidos.setVisible(false);
        vistaProyectosDocente.setVisible(false);
        vistaArchivosEnviados.setVisible(false);
        vistaSubirNuevaVersion.setVisible(false);
        vistaDetallesMensaje.setVisible(false);
        scrollPaneDetallesProyecto.setVisible(true);
        tituloVista.setText("Detalles del Proyecto");
    }

    @FXML
    private void handleSubirNuevaVersionDesdeDetalles() {
        if (proyectoSeleccionado == null) {
            mostrarError("No se ha seleccionado un proyecto.");
            return;
        }

        scrollPaneDetallesProyecto.setVisible(false);
        vistaSubirNuevaVersion.setVisible(true);
        tituloVista.setText("Subir Nueva Versión del Formato A");
        proyectoSeleccionadoParaReintento = proyectoSeleccionado;
        lblNuevoArchivo.setText("Ningún archivo seleccionado");
        nuevoContenidoPDF = null;
        nuevoNombreArchivo = null;
    }

    @FXML
    private void handleVolverDeDetallesProyecto() {
        scrollPaneDetallesProyecto.setVisible(false);
        vistaRecibidos.setVisible(false);
        vistaProyectosDocente.setVisible(true);
        vistaArchivosEnviados.setVisible(false);
        vistaSubirNuevaVersion.setVisible(false);
        vistaDetallesMensaje.setVisible(false);
        tituloVista.setText("Mis Proyectos de Grado");
        cargarProyectosDelDocente();
    }
}
