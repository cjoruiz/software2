package co.unicauca.presentation;

import co.unicauca.solid.domain.Coordinador;
import co.unicauca.solid.domain.ProyectoGrado;
import co.unicauca.solid.domain.enums.EstadoEnum;
import co.unicauca.solid.service.FilePGService;
import co.unicauca.solid.service.ProyectoGradoService;
import co.unicauca.solid.service.UserService;
import java.io.File;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class HomepageCoordinadorController {

    @FXML
    private Button btnDescargarFormatoA;
    @FXML
    private Button btnDescargarCarta;
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
    private TableColumn<ProyectoGrado, String> colEstudiante;
    @FXML
    private TableColumn<ProyectoGrado, String> colDirector;
    @FXML
    private TableColumn<ProyectoGrado, String> colEstado;
    @FXML
    private TableColumn<ProyectoGrado, Integer> colIntento;
    @FXML
    private TableColumn<ProyectoGrado, Void> colAcciones;
    @FXML
    private Label lblTitulo;
    @FXML
    private Label lblEstudiante;
    @FXML
    private Label lblDirector;
    @FXML
    private Label lblEstado;
    @FXML
    private Label lblIntento;
    @FXML
    private RadioButton radioAprobar;
    @FXML
    private RadioButton radioRechazar;
    @FXML
    private TextArea txtObservaciones;
    @FXML
    private VBox vistaProyectosPendientes;
    @FXML
    private VBox vistaEvaluarProyecto;
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

    // === Vistas dinámicas (NO inyectadas por FXML) ===
    private VBox vistaTodosProyectosDinamica = null;

    private ProyectoGrado proyectoSeleccionado;
    private boolean esVistaTodosProyectos = false;
    private Coordinador coordinadorActual;
    private ProyectoGradoService proyectoGradoService;
    private UserService userService;
    private Stage primaryStage;
    private FilePGService filePGService;

    public void setFilePGService(FilePGService filePGService) {
        this.filePGService = filePGService;
    }

    public void setUsuario(Coordinador coordinador) {
        this.coordinadorActual = coordinador;
        if (nombreLabel != null) {
            nombreLabel.setText(coordinador.getNombres() + " " + coordinador.getApellidos());
            programaLabel.setText(coordinador.getPrograma());
            celularLabel.setText(coordinador.getCelular() != null ? coordinador.getCelular() : "No registrado");
        }

        if (primaryStage != null) {
            primaryStage.setWidth(950);
            primaryStage.setHeight(800);
            primaryStage.centerOnScreen();
        }

        if (tablaProyectos != null) {
            cargarProyectosPendientes();
        }
    }

    public void setProyectoGradoService(ProyectoGradoService proyectoGradoService) {
        this.proyectoGradoService = proyectoGradoService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    public void initialize() {
        configurarTabla();
        mostrarVistaProyectosPendientes();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idProyecto"));
        colTitulo.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getTitulo()));
        colEstudiante.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(
                p.getValue().getEstudiante1() != null ? p.getValue().getEstudiante1().getEmail() : "N/A"
        ));
        colDirector.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(
                p.getValue().getDirector() != null ? p.getValue().getDirector().getEmail() : "N/A"
        ));
        colEstado.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(
                proyectoGradoService.obtenerEstadoLegible(p.getValue().getEstadoActual())
        ));
        colIntento.setCellValueFactory(new PropertyValueFactory<>("numeroIntento"));

        colAcciones.setCellFactory(param -> new TableCell<ProyectoGrado, Void>() {
            private final Button evaluarButton = new Button("Evaluar");

            {
                evaluarButton.setOnAction(event -> {
                    ProyectoGrado proyecto = getTableView().getItems().get(getIndex());
                    mostrarVistaEvaluarProyecto(proyecto);
                });
                evaluarButton.setStyle("-fx-background-color: #00cc99; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    ProyectoGrado proyecto = getTableView().getItems().get(getIndex());
                    if (puedeSerEvaluado(proyecto.getEstadoActual())) {
                        setGraphic(evaluarButton);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
    }

    @FXML
    private void handleVerProyectosPendientes() {
        esVistaTodosProyectos = false;
        mostrarVistaProyectosPendientes();
    }

    private void mostrarVistaProyectosPendientes() {
        vistaProyectosPendientes.setVisible(true);
        vistaEvaluarProyecto.setVisible(false);
        vistaDetallesProyecto.setVisible(false);
        if (vistaTodosProyectosDinamica != null) {
            vistaTodosProyectosDinamica.setVisible(false);
        }
        tituloVista.setText("Proyectos Pendientes de Evaluación");
    }

    private void mostrarVistaEvaluarProyecto(ProyectoGrado proyecto) {
        this.proyectoSeleccionado = proyecto;
        this.esVistaTodosProyectos = false;
        lblTitulo.setText(proyecto.getTitulo());
        lblEstudiante.setText(proyecto.getEstudiante1().getEmail());
        lblDirector.setText(proyecto.getDirector().getEmail());
        lblEstado.setText(proyectoGradoService.obtenerEstadoLegible(proyecto.getEstadoActual()));
        lblIntento.setText(String.valueOf(proyecto.getNumeroIntento()));

        vistaProyectosPendientes.setVisible(false);
        vistaEvaluarProyecto.setVisible(true);
        vistaDetallesProyecto.setVisible(false);
        if (vistaTodosProyectosDinamica != null) {
            vistaTodosProyectosDinamica.setVisible(false);
        }
        tituloVista.setText("Evaluar Proyecto");
    }

    @FXML
    private void handleEvaluar() {
        boolean aprobado = radioAprobar.isSelected();
        String observaciones = txtObservaciones.getText().trim();

        try {
            boolean exito = proyectoGradoService.evaluarFormatoA(
                    proyectoSeleccionado.getIdProyecto(),
                    aprobado,
                    observaciones,
                    coordinadorActual.getEmail()
            );

            if (exito) {
                mostrarExito("Evaluación realizada correctamente");
                mostrarVistaProyectosPendientes();
                cargarProyectosPendientes();
            } else {
                mostrarError("No se pudo realizar la evaluación");
            }
        } catch (Exception e) {
            mostrarError("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelarEvaluacion() {
        mostrarVistaProyectosPendientes();
    }

    private void cargarProyectosPendientes() {
        try {
            if (coordinadorActual == null) {
                return;
            }

            List<ProyectoGrado> todosPendientes = proyectoGradoService.obtenerProyectosPendientesEvaluacion();
            String programaCoordinador = coordinadorActual.getPrograma();
            List<ProyectoGrado> proyectosDelPrograma = todosPendientes.stream()
                    .filter(p -> p.getDirector().getPrograma().equals(programaCoordinador))
                    .collect(Collectors.toList());

            ObservableList<ProyectoGrado> lista = FXCollections.observableArrayList(proyectosDelPrograma);
            tablaProyectos.setItems(lista);

        } catch (Exception e) {
            e.printStackTrace();
            tablaProyectos.setItems(FXCollections.observableArrayList());
        }
    }

    private boolean puedeSerEvaluado(String estadoActual) {
        return estadoActual != null && (estadoActual.equals(EstadoEnum.EN_PRIMERA_EVALUACION_FORMATO_A.getValor())
                || estadoActual.equals(EstadoEnum.EN_SEGUNDA_EVALUACION_FORMATO_A.getValor())
                || estadoActual.equals(EstadoEnum.EN_TERCERA_EVALUACION_FORMATO_A.getValor()));
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

    @FXML
    private void handleCerrarSesion() {
        App.setRoot("login");
    }

    @FXML
    private void handleVerEstadisticas() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/estadisticasCoordinador.fxml"));
            Parent root = loader.load();

            EstadisticasCoordinadorController controller = loader.getController();
            controller.setProyectoGradoService(proyectoGradoService);
            controller.setPrimaryStage(primaryStage);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Estadísticas de Proyectos");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("No se pudo abrir la ventana de estadísticas.");
        }
    }

    @FXML
    private void handleVerTodosProyectos() {
        esVistaTodosProyectos = true;
        vistaProyectosPendientes.setVisible(false);
        vistaEvaluarProyecto.setVisible(false);
        vistaDetallesProyecto.setVisible(false);
        tituloVista.setText("Todos los Proyectos");

        if (vistaTodosProyectosDinamica != null) {
            vistaTodosProyectosDinamica.setVisible(true);
            // Actualizar datos
            if (vistaTodosProyectosDinamica.getChildren().size() > 1) {
                TableView<ProyectoGrado> tabla = (TableView<ProyectoGrado>) vistaTodosProyectosDinamica.getChildren().get(1);
                cargarTodosProyectos(tabla);
            }
            return;
        }

        crearVistaTodosProyectos();
    }

    private void crearVistaTodosProyectos() {
        vistaTodosProyectosDinamica = new VBox(10);
        vistaTodosProyectosDinamica.setPadding(new Insets(20));
        vistaTodosProyectosDinamica.setVisible(true);

        Label titulo = new Label("Todos los Proyectos");
        titulo.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        TableView<ProyectoGrado> tablaTodos = new TableView<>();
        tablaTodos.setPrefHeight(400);

        TableColumn<ProyectoGrado, Integer> colId = new TableColumn<>("ID");
        colId.setPrefWidth(50);
        colId.setCellValueFactory(new PropertyValueFactory<>("idProyecto"));

        TableColumn<ProyectoGrado, String> colTitulo = new TableColumn<>("Título");
        colTitulo.setPrefWidth(200);
        colTitulo.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(p.getValue().getTitulo()));

        TableColumn<ProyectoGrado, String> colEstudiante = new TableColumn<>("Estudiante");
        colEstudiante.setPrefWidth(150);
        colEstudiante.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(
                p.getValue().getEstudiante1() != null ? p.getValue().getEstudiante1().getEmail() : "N/A"
        ));

        TableColumn<ProyectoGrado, String> colDirector = new TableColumn<>("Director");
        colDirector.setPrefWidth(150);
        colDirector.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(
                p.getValue().getDirector() != null ? p.getValue().getDirector().getEmail() : "N/A"
        ));

        TableColumn<ProyectoGrado, String> colEstado = new TableColumn<>("Estado");
        colEstado.setPrefWidth(120);
        colEstado.setCellValueFactory(p -> new javafx.beans.property.SimpleStringProperty(
                proyectoGradoService.obtenerEstadoLegible(p.getValue().getEstadoActual())
        ));

        TableColumn<ProyectoGrado, Integer> colIntento = new TableColumn<>("Intento");
        colIntento.setPrefWidth(80);
        colIntento.setCellValueFactory(new PropertyValueFactory<>("numeroIntento"));

        TableColumn<ProyectoGrado, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setPrefWidth(100);
        colAcciones.setCellFactory(param -> new TableCell<ProyectoGrado, Void>() {
            private final Button verButton = new Button("Ver");

            {
                verButton.setOnAction(event -> {
                    ProyectoGrado proyecto = getTableView().getItems().get(getIndex());
                    mostrarDetallesProyecto(proyecto);
                });
                verButton.setStyle("-fx-background-color: #00cc99; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(verButton);
                }
            }
        });

        tablaTodos.getColumns().addAll(colId, colTitulo, colEstudiante, colDirector, colEstado, colIntento, colAcciones);

        tablaTodos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                mostrarDetallesProyecto(newSelection);
            }
        });

        vistaTodosProyectosDinamica.getChildren().addAll(titulo, tablaTodos);

        // Añadir al StackPane (solo una vez)
        StackPane contenedor = (StackPane) vistaProyectosPendientes.getParent();
        contenedor.getChildren().add(vistaTodosProyectosDinamica);

        // Cargar datos
        cargarTodosProyectos(tablaTodos);
    }

    @FXML
    private void handleDescargarCartaEmpresa() {
        if (proyectoSeleccionado == null) {
            mostrarError("No se ha seleccionado un proyecto.");
            return;
        }

        try {
            co.unicauca.solid.domain.FilePG cartaEmpresa = filePGService.obtenerCartaEmpresa(proyectoSeleccionado.getIdProyecto());

            if (cartaEmpresa == null || cartaEmpresa.getContenido() == null) {
                mostrarError("No se encontró la carta de aceptación de la empresa para este proyecto.");
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Carta de Aceptación");
            fileChooser.setInitialFileName(cartaEmpresa.getNombreArchivo());
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

            File archivoDestino = fileChooser.showSaveDialog(primaryStage);

            if (archivoDestino != null) {
                String nombreArchivo = archivoDestino.getName();
                if (!nombreArchivo.toLowerCase().endsWith(".pdf")) {
                    archivoDestino = new File(archivoDestino.getParentFile(), nombreArchivo + ".pdf");
                }

                Files.write(archivoDestino.toPath(), cartaEmpresa.getContenido());
                mostrarExito("Carta de aceptación guardada exitosamente en:\n" + archivoDestino.getAbsolutePath());
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al descargar la carta de aceptación:\n" + e.getMessage());
        }
    }

    private void cargarTodosProyectos(TableView<ProyectoGrado> tabla) {
        try {
            List<ProyectoGrado> todosProyectos = proyectoGradoService.obtenerTodosProyectos();
            String programaCoordinador = coordinadorActual.getPrograma();
            List<ProyectoGrado> proyectosDelPrograma = todosProyectos.stream()
                    .filter(p -> p.getDirector().getPrograma().equals(programaCoordinador))
                    .collect(Collectors.toList());

            ObservableList<ProyectoGrado> lista = FXCollections.observableArrayList(proyectosDelPrograma);
            tabla.setItems(lista);

        } catch (Exception e) {
            e.printStackTrace();
        }
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

        vistaProyectosPendientes.setVisible(false);
        vistaEvaluarProyecto.setVisible(false);
        if (vistaTodosProyectosDinamica != null) {
            vistaTodosProyectosDinamica.setVisible(false);
        }
        vistaDetallesProyecto.setVisible(true);
        if ("PRACTICA_PROFESIONAL".equals(proyecto.getModalidad())) {
            btnDescargarFormatoA.setVisible(true);
            btnDescargarCarta.setVisible(true);
        } else {
            btnDescargarFormatoA.setVisible(true);
            btnDescargarCarta.setVisible(false);
        }
        tituloVista.setText("Detalles del Proyecto");
    }

    @FXML
    private void handleVolverDeDetallesProyecto() {
        vistaDetallesProyecto.setVisible(false);

        if (esVistaTodosProyectos) {
            if (vistaTodosProyectosDinamica != null) {
                vistaTodosProyectosDinamica.setVisible(true);
            }
            vistaProyectosPendientes.setVisible(false);
            tituloVista.setText("Todos los Proyectos");
        } else {
            vistaProyectosPendientes.setVisible(true);
            if (vistaTodosProyectosDinamica != null) {
                vistaTodosProyectosDinamica.setVisible(false);
            }
            tituloVista.setText("Proyectos Pendientes de Evaluación");
        }
    }

    @FXML
    private void handleDescargarFormatoA() {
        if (proyectoSeleccionado == null) {
            mostrarError("No se ha seleccionado un proyecto.");
            return;
        }

        try {
            co.unicauca.solid.domain.FilePG formatoA = filePGService.obtenerFormatoAActual(proyectoSeleccionado.getIdProyecto());

            if (formatoA == null || formatoA.getContenido() == null) {
                mostrarError("No se encontró el Formato A para este proyecto.");
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Formato A");
            fileChooser.setInitialFileName(formatoA.getNombreArchivo());
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

            File archivoDestino = fileChooser.showSaveDialog(primaryStage);

            if (archivoDestino != null) {
                String nombreArchivo = archivoDestino.getName();
                if (!nombreArchivo.toLowerCase().endsWith(".pdf")) {
                    archivoDestino = new File(archivoDestino.getParentFile(), nombreArchivo + ".pdf");
                }

                Files.write(archivoDestino.toPath(), formatoA.getContenido());
                mostrarExito("Formato A guardado exitosamente en:\n" + archivoDestino.getAbsolutePath());
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al descargar el Formato A:\n" + e.getMessage());
        }
    }
}
