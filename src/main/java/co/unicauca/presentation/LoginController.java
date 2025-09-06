/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.unicauca.presentation;


import co.unicauca.solid.access.IUserRepository;
import co.unicauca.solid.domain.User;
import co.unicauca.solid.service.UserService;
import co.unicauca.utilities.exeption.InvalidUserDataException;
import co.unicauca.utilities.exeption.LoginException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField userText;
    @FXML private PasswordField passwordField;
    
    private IUserRepository userRepository;
    private UserService userService;
    private Stage primaryStage;

    public void setUserRepository(IUserRepository userRepository) {
        this.userRepository = userRepository;
        this.userService = new UserService(userRepository);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    private void handleLogin() {
        try {
            String email = userText.getText().trim();
            String password = passwordField.getText();
            
            User user = userService.login(email, password);
            
            showAlert(Alert.AlertType.INFORMATION, "Login Exitoso", 
                     "Bienvenido, " + user.getNombres() + "!");
            
            if ("ESTUDIANTE".equals(user.getRol())) {
                openEstudianteView(user);
            } else if ("DOCENTE".equals(user.getRol())) {
                openDocenteView(user);
            }
            
        } catch (LoginException ex) {
            showAlert(Alert.AlertType.ERROR, "Error de Autenticación", ex.getMessage());
            passwordField.clear();
        } catch (InvalidUserDataException ex) {
            showAlert(Alert.AlertType.WARNING, "Datos Inválidos", ex.getMessage());
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error inesperado: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleRegister() {
        App.setRoot("RegisterView");
    }

    @FXML
    private void handleClose() {
        System.exit(0);
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