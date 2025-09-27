package co.unicauca.presentation;

import co.unicauca.solid.service.ProyectoGradoService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class EstadisticasCoordinadorController {

    @FXML
    private Label lblTotalProyectos;
    @FXML
    private Label lblAprobados;
    @FXML
    private Label lblPorAprobar;
    @FXML
    private Label lblRechazados;
    @FXML
    private ListView<String> listaEstados;

    private ProyectoGradoService proyectoGradoService;
    private Stage primaryStage;

    public void setProyectoGradoService(ProyectoGradoService proyectoGradoService) {
        this.proyectoGradoService = proyectoGradoService;
        proyectoGradoService.addEstadisticasListener(this::cargarEstadisticas);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    public void initialize() {
        cargarEstadisticas();
    }

    public void cargarEstadisticas() {
        try {
            String estadisticas = proyectoGradoService.obtenerEstadisticasGenerales();
            String[] lineas = estadisticas.split("\n");

            // Parsear estadísticas generales
            for (String linea : lineas) {
                if (linea.startsWith("Total de proyectos:")) {
                    lblTotalProyectos.setText(linea.replace("Total de proyectos: ", ""));
                } else if (linea.startsWith("Proyectos de Investigación:")) {
                    // Ignorar por ahora
                } else if (linea.startsWith("Proyectos de Práctica Profesional:")) {
                    // Ignorar por ahora
                }
            }

            // Contar estados específicos
            int aprobados = 0, porAprobar = 0, rechazados = 0;
            listaEstados.getItems().clear();

            for (String linea : lineas) {
                if (linea.contains("Formato A aprobado:")) {
                    aprobados = Integer.parseInt(linea.replaceAll("[^0-9]", ""));
                    lblAprobados.setText(String.valueOf(aprobados));
                } else if (linea.contains("En primera evaluación formato A:")
                        || linea.contains("En segunda evaluación formato A:")
                        || linea.contains("En tercera evaluación formato A:")) {
                    porAprobar += Integer.parseInt(linea.replaceAll("[^0-9]", ""));
                } else if (linea.contains("Formato A rechazado:")
                        || linea.contains("Rechazado definitivamente:")) {
                    rechazados += Integer.parseInt(linea.replaceAll("[^0-9]", ""));
                }

                if (linea.contains(": ") && !linea.startsWith("===")) {
                    listaEstados.getItems().add(linea);
                }
            }

            lblPorAprobar.setText(String.valueOf(porAprobar));
            lblRechazados.setText(String.valueOf(rechazados));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleActualizar() {
        cargarEstadisticas();
    }

}
