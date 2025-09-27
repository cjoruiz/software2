/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package co.unicauca.presentation;


/**
 * FXML Controller class
 *
 * @author crist
 */


import co.unicauca.solid.domain.ProyectoGrado;
import co.unicauca.solid.service.ProyectoGradoService;
import co.unicauca.solid.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class EvaluarFormatoAController {

    @FXML private Label lblTitulo;
    @FXML private Label lblEstudiante;
    @FXML private Label lblDirector;
    @FXML private Label lblEstado;
    @FXML private Label lblIntento;
    @FXML private RadioButton radioAprobar;
    @FXML private RadioButton radioRechazar;
    @FXML private TextArea txtObservaciones;

    private ProyectoGrado proyecto;
    private ProyectoGradoService proyectoGradoService;
    private UserService userService;
    private String emailCoordinador;

    private ToggleGroup grupoEvaluacion;

    public void setProyecto(ProyectoGrado proyecto) {
        this.proyecto = proyecto;
        if (lblTitulo != null) {
            lblTitulo.setText(proyecto.getTitulo());
            lblEstudiante.setText(proyecto.getEstudiante1().getEmail());
            lblDirector.setText(proyecto.getDirector().getEmail());
            lblEstado.setText(proyecto.getEstadoDescriptivo());
            lblIntento.setText(String.valueOf(proyecto.getNumeroIntento()));
        }
    }

    public void setProyectoGradoService(ProyectoGradoService proyectoGradoService) {
        this.proyectoGradoService = proyectoGradoService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setEmailCoordinador(String email) {
        this.emailCoordinador = email;
    }

    @FXML
    public void initialize() {
        grupoEvaluacion = new ToggleGroup();
        radioAprobar.setToggleGroup(grupoEvaluacion);
        radioRechazar.setToggleGroup(grupoEvaluacion);
        radioAprobar.setSelected(true);
    }

    @FXML
    private void handleEvaluar() {
        boolean aprobado = radioAprobar.isSelected();
        String observaciones = txtObservaciones.getText().trim();

        try {
            boolean exito = proyectoGradoService.evaluarFormatoA(
                proyecto.getIdProyecto(),
                aprobado,
                observaciones,
                emailCoordinador
            );

            if (exito) {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                alert.setTitle("Éxito");
                alert.setHeaderText(null);
                alert.setContentText("Evaluación realizada correctamente.");
                alert.showAndWait();
                ((Stage) lblTitulo.getScene().getWindow()).close();
            } else {
                mostrarError("No se pudo realizar la evaluación.");
            }
        } catch (Exception e) {
            mostrarError("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelar() {
        ((Stage) lblTitulo.getScene().getWindow()).close();
    }

    private void mostrarError(String mensaje) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}