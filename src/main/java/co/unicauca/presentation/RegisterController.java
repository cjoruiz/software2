package co.unicauca.presentation;

import co.unicauca.solid.access.IUserRepository;
import co.unicauca.solid.domain.Coordinador;
import co.unicauca.solid.domain.Docente;
import co.unicauca.solid.domain.Estudiante;
import co.unicauca.solid.domain.Usuario;
import co.unicauca.solid.service.UserService;
import co.unicauca.utilities.exeption.InvalidUserDataException;
import co.unicauca.utilities.exeption.UserAlreadyExistsException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ComboBox<String> programComboBox;

    @FXML
    private ComboBox<String> roleComboBox;

    private IUserRepository userRepository;
    private UserService userService;

    @FXML
    public void initialize() {
        // Inicializar los ComboBox
        programComboBox.getItems().addAll(
                "INGENIERIA_SISTEMAS",
                "INGENIERIA_ELECTRONICA",
                "AUTOMATICA_INDUSTRIAL",
                "TECNOLOGIA_TELEMATICA"
        );
        programComboBox.setValue("INGENIERIA_SISTEMAS");

        roleComboBox.getItems().addAll("ESTUDIANTE", "DOCENTE", "COORDINADOR");
        roleComboBox.setValue("ESTUDIANTE");
    }

    public void setUserRepository(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String nombres = nameField.getText().trim();
        String apellidos = lastNameField.getText().trim();
        String celular = phoneField.getText().trim();
        String programa = programComboBox.getValue();
        String rol = roleComboBox.getValue();
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        try {
            Usuario nuevoUsuario = crearUsuario(rol, email, password, nombres, apellidos, celular, programa);
            userService.registerUser(nuevoUsuario);
            showAlert(Alert.AlertType.INFORMATION, "Registro Exitoso", "Usuario registrado correctamente.");
            loadUserDashboard(nuevoUsuario);

        } catch (UserAlreadyExistsException ex) {
            showAlert(Alert.AlertType.WARNING, "Usuario Duplicado", ex.getMessage());
        } catch (InvalidUserDataException ex) {
            showAlert(Alert.AlertType.WARNING, "Datos Inválidos", ex.getMessage());
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Error Inesperado", ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        try {
            App.setRoot("login");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo cargar la pantalla de login.");
        }
    }

    @FXML
    private void handleCerrarVentana(ActionEvent event) {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    private Usuario crearUsuario(String rol, String email, String password, String nombres, String apellidos, String celular, String programa) throws InvalidUserDataException {
        switch (rol) {
            case "ESTUDIANTE":
                return new Estudiante(email, password, nombres, apellidos, celular, programa);
            case "DOCENTE":
                return new Docente(email, password, nombres, apellidos, celular, programa, "PLANTA");
            case "COORDINADOR":
                return new Coordinador(email, password, nombres, apellidos, celular, programa);
            default:
                throw new InvalidUserDataException("Rol de usuario no válido: " + rol);
        }
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
