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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
    private Stage primaryStage;
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
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
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
