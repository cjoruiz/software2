package co.unicauca.presentation;

import co.unicauca.solid.access.IUserRepository;
import co.unicauca.solid.domain.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class DocenteController {

    @FXML
    private Label profNameLabel;

    @FXML
    private Label profEmailLabel;

    @FXML
    private Label progTextLabel;

    private Usuario usuario;
    private IUserRepository userRepository;

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        actualizarLabels();
    }

    public void setUserRepository(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private void actualizarLabels() {
        if (usuario != null) {
            profNameLabel.setText(usuario.getNombres() + " " + usuario.getApellidos());
            profEmailLabel.setText(usuario.getEmail());
            progTextLabel.setText(usuario.getPrograma());
        }
    }

    @FXML
    private void handleCrearProyecto(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/crear_proyecto.fxml"));
            Parent root = loader.load();

            CrearProyectoController controller = loader.getController();
            controller.setUsuario(usuario);
            controller.setUserRepository(userRepository); // Pasar el repositorio

            Scene scene = new Scene(root, 600, 700);
            Stage stage = new Stage();
            stage.setTitle("Crear Nuevo Proyecto de Grado");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo abrir el formulario de creación de proyecto.");
        }
    }

    @FXML
    private void handleCerrarSesion(ActionEvent event) {
        try {
            App.setRoot("login");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo cerrar la sesión.");
        }
    }

    @FXML
    private void handleDescargarEjemplo(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar PDF de Ejemplo");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        fileChooser.setInitialFileName("formatoA_ejemplo.pdf");

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            // Simulamos la descarga
            showAlert(Alert.AlertType.INFORMATION, "Éxito", "PDF de ejemplo guardado en: " + file.getAbsolutePath());
        }
    }

    @FXML
    private void handleCerrarApp(ActionEvent event) {
        Stage stage = (Stage) profNameLabel.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
