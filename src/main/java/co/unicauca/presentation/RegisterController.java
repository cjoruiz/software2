package co.unicauca.presentation;


import co.unicauca.presentation.App;
import co.unicauca.solid.access.IUserRepository;
import co.unicauca.solid.domain.User;
import co.unicauca.solid.service.UserService;
import co.unicauca.utilities.exeption.InvalidUserDataException;
import co.unicauca.utilities.exeption.UserAlreadyExistsException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {

    @FXML private TextField nameText;
    @FXML private TextField lastNameText;
    @FXML private TextField celText;
    @FXML private ComboBox<String> programBox;
    @FXML private ComboBox<String> userTypeBox;
    @FXML private TextField emailText;
    @FXML private PasswordField passwRegText;

    private IUserRepository userRepository;
    private UserService userService;
    private Stage primaryStage;

    public void setUserRepository(IUserRepository userRepository) {
        this.userRepository = userRepository;
        this.userService = new UserService(userRepository);
        initializeComboBoxes();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private void initializeComboBoxes() {
        programBox.setItems(FXCollections.observableArrayList(
            "INGENIERIA_SISTEMAS",
            "INGENIERIA_ELECTRONICA", 
            "AUTOMATICA_INDUSTRIAL",
            "TECNOLOGIA_TELEMATICA"
        ));
        programBox.getSelectionModel().selectFirst();

        userTypeBox.setItems(FXCollections.observableArrayList(
            "ESTUDIANTE",
            "DOCENTE"
        ));
        userTypeBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleRegister() {
        try {
            User newUser = new User(
                emailText.getText().trim(),
                passwRegText.getText(),
                nameText.getText().trim(),
                lastNameText.getText().trim(),
                celText.getText().trim(),
                programBox.getSelectionModel().getSelectedItem(),
                userTypeBox.getSelectionModel().getSelectedItem()
            );

            userService.registerUser(newUser);

            showAlert(Alert.AlertType.INFORMATION, "Registro Exitoso", 
                     "Usuario registrado correctamente");

            if ("ESTUDIANTE".equals(newUser.getRol())) {
                openEstudianteView(newUser);
            } else if ("DOCENTE".equals(newUser.getRol())) {
                openDocenteView(newUser);
            }

        } catch (UserAlreadyExistsException ex) {
            showAlert(Alert.AlertType.WARNING, "Usuario Duplicado", ex.getMessage());
        } catch (InvalidUserDataException ex) {
            showAlert(Alert.AlertType.WARNING, "Datos Inválidos", ex.getMessage());
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Error", 
                     "Error inesperado: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleGoToLogin() {
        App.setRoot("LoginView");
    }

    private void openEstudianteView(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/mavenproject1fx/EstudianteView.fxml"));
            Parent root = loader.load();
            
//            EstudianteController controller = loader.getController();
//            controller.setUser(user);
//            controller.setUserRepository(userRepository);
//            controller.setPrimaryStage(primaryStage);
//            
            primaryStage.getScene().setRoot(root);
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo cargar la vista de estudiante");
            e.printStackTrace();
        }
    }

    private void openDocenteView(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/mavenproject1fx/DocenteView.fxml"));
            Parent root = loader.load();
            
//            DocenteController controller = loader.getController();
//            controller.setUser(user);
//            controller.setUserRepository(userRepository);
//            controller.setPrimaryStage(primaryStage);
//            
            primaryStage.getScene().setRoot(root);
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo cargar la vista de docente");
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(primaryStage);
        alert.showAndWait();
    }
}