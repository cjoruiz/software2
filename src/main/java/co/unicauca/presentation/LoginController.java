// src/main/java/co/unicauca/presentation/LoginController.java
package co.unicauca.presentation;

import co.unicauca.solid.domain.Coordinador;
import co.unicauca.solid.domain.Docente;
import co.unicauca.solid.domain.Estudiante;
import co.unicauca.solid.domain.Usuario;
import co.unicauca.solid.service.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;

    private UserService userService;
    private Stage primaryStage;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        try {
            Usuario usuario = userService.login(email, password);
            showAlert(Alert.AlertType.INFORMATION, "Login Exitoso", "Bienvenido, " + usuario.getNombres() + "!");

            // Navegar a dashboard según rol
            loadUserDashboard(usuario);

        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Error de Autenticación", ex.getMessage());
            passwordField.clear();
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        try {
            App.setRoot("register");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo cargar la pantalla de registro.");
        }
    }

    @FXML
    private void handleCerrarApp(ActionEvent event) {
        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.close();
    }

    private void loadUserDashboard(Usuario usuario) {
        try {
            String fxmlFile;
            switch (usuario.getRol()) {
                case "ESTUDIANTE":
                    fxmlFile = "homepageEstudiante";
                    break;
                case "DOCENTE":
                    fxmlFile = "homepageDocente";
                    break;
                case "COORDINADOR":
                    fxmlFile = "homepageCoordinador";
                    break;
                default:
                    throw new RuntimeException("Rol no soportado: " + usuario.getRol());
            }

            FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/" + fxmlFile + ".fxml"));
            Parent root = loader.load();

            Object controller = loader.getController();

            if (controller instanceof HomepageDocenteController && usuario instanceof Docente) {
                HomepageDocenteController docenteController = (HomepageDocenteController) controller;
                docenteController.setUserService(userService);
                docenteController.setProyectoGradoService(App.getProyectoGradoService());
                docenteController.setFilePGService(App.getFilePGService());
                docenteController.setMensajeInternoService(App.getMensajeInternoService()); // ← INYECCIÓN CLAVE
                docenteController.setPrimaryStage(primaryStage);
                docenteController.setUsuario((Docente) usuario);

            } else if (controller instanceof HomepageEstudianteController && usuario instanceof Estudiante) {
                HomepageEstudianteController estudianteController = (HomepageEstudianteController) controller;
                estudianteController.setUserService(userService);
                estudianteController.setProyectoGradoService(App.getProyectoGradoService());
                estudianteController.setMensajeInternoService(App.getMensajeInternoService()); // ← INYECCIÓN CLAVE
                estudianteController.setPrimaryStage(primaryStage);
                estudianteController.setUsuario((Estudiante) usuario);

            } else if (controller instanceof HomepageCoordinadorController && usuario instanceof Coordinador) {
                HomepageCoordinadorController coordinadorController = (HomepageCoordinadorController) controller;
                coordinadorController.setUserService(userService);
                coordinadorController.setFilePGService(App.getFilePGService());
                coordinadorController.setProyectoGradoService(App.getProyectoGradoService());
                coordinadorController.setPrimaryStage(primaryStage);
                coordinadorController.setUsuario((Coordinador) usuario);
            }

            primaryStage.getScene().setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo cargar el dashboard.");
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
