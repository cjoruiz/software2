package co.unicauca.presentation;

import co.unicauca.solid.access.IUserRepository;
import co.unicauca.solid.domain.Usuario;
import co.unicauca.solid.service.UserService;
import co.unicauca.utilities.exeption.InvalidUserDataException;
import co.unicauca.utilities.exeption.LoginException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    private IUserRepository userRepository;
    private UserService userService;
    private Stage primaryStage;

    public void setUserRepository(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

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

            // Navegar a la pantalla correspondiente
            loadUserDashboard(usuario);

        } catch (LoginException ex) {
            showAlert(Alert.AlertType.ERROR, "Error de Autenticación", ex.getMessage());
            passwordField.clear();
        } catch (InvalidUserDataException ex) {
            showAlert(Alert.AlertType.WARNING, "Datos Inválidos", ex.getMessage());
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Error Inesperado", ex.getMessage());
            ex.printStackTrace();
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
                    fxmlFile = "estudiante";
                    break;
                case "DOCENTE":
                    fxmlFile = "docente";
                    break;
                case "COORDINADOR":
                    fxmlFile = "coordinador";
                    break;
                default:
                    showAlert(Alert.AlertType.WARNING, "Rol no soportado", "El rol de usuario no está soportado.");
                    return;
            }

            App.setRoot(fxmlFile);

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
