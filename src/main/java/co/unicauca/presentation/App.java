package co.unicauca.presentation;

import co.unicauca.solid.access.Factory;
import co.unicauca.solid.access.FilePGRepository;
import co.unicauca.solid.access.IUserRepository;
import co.unicauca.solid.access.ProyectoGradoRepository;
import co.unicauca.solid.access.UserRepository;
import co.unicauca.solid.domain.FilePG;
import co.unicauca.solid.domain.ProyectoGrado;
import co.unicauca.solid.domain.User;
import co.unicauca.solid.service.ProyectoGradoService;
import co.unicauca.solid.service.UserService;
import co.unicauca.utilities.exeption.InvalidUserDataException;
import co.unicauca.utilities.exeption.LoginException;
import co.unicauca.utilities.exeption.UserAlreadyExistsException;
import co.unicauca.utilities.exeption.UserNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Application {

    private static Scene scene;
    private static Stage primaryStage;
    private static IUserRepository userRepository;
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        
        // Inicializar repositorio
        userRepository = Factory.getInstance().getUserRepository("default");
        
        // Cargar vista de login
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/unicauca/mavenproject1fx/LoginView.fxml"));
        Parent root = loader.load();
        
        // Configurar controlador
        LoginController controller = loader.getController();
        controller.setUserRepository(userRepository);
        controller.setPrimaryStage(primaryStage);
        
        scene = new Scene(root, 730, 610);
        
        // Hacer la ventana arrastrable
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        
        root.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });
        
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.setTitle("Sistema de Proyectos de Grado");
        stage.show();
    }

    public static void setRoot(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/co/unicauca/mavenproject1fx/" + fxml + ".fxml"));
            Parent root = loader.load();
            
            // Configurar dependencias según el controlador
            Object controller = loader.getController();
            
            if (controller instanceof RegisterController) {
                RegisterController registerController = (RegisterController) controller;
                registerController.setUserRepository(userRepository);
                registerController.setPrimaryStage(primaryStage);
            } else if (controller instanceof LoginController) {
                LoginController loginController = (LoginController) controller;
                loginController.setUserRepository(userRepository);
                loginController.setPrimaryStage(primaryStage);
            }
            // Agregar más controladores aquí
            
            scene.setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InvalidUserDataException {
//        
        
        IUserRepository userRepo = Factory.getInstance().getUserRepository("default");
        UserService userService = new UserService(userRepo);

        System.out.println("=== EJEMPLO 1: REGISTRO DE USUARIOS ===\n");

        // 1. Registrar un Docente de Planta
        User docentePlanta = new User();
        docentePlanta.setEmail("director.planta@unicauca.edu.co");
        docentePlanta.setPassword("Dir123!Plant");
        docentePlanta.setNombres("María");
        docentePlanta.setApellidos("López");
        docentePlanta.setCelular("3101112233");
        docentePlanta.setPrograma("Ingeniería de Sistemas");
        docentePlanta.setRol("DOCENTE");
        docentePlanta.setTipoDocente("PLANTA");

        try {
            userService.registerUser(docentePlanta);
            System.out.println("✅ Docente de planta registrado exitosamente.");
        } catch (UserAlreadyExistsException e) {
            System.out.println("⚠️  ERROR: " + e.getMessage());
        } catch (InvalidUserDataException e) {
            System.out.println("❌ ERROR DE VALIDACIÓN: " + e.getMessage());
        }

        // 2. Registrar un Docente Ocasional
        User docenteOcasional = new User();
        docenteOcasional.setEmail("director.ocasional@unicauca.edu.co");
        docenteOcasional.setPassword("Ocas456!Dir");
        docenteOcasional.setNombres("Carlos");
        docenteOcasional.setApellidos("Ramírez");
        docenteOcasional.setPrograma("Ingeniería Electrónica");
        docenteOcasional.setRol("DOCENTE");
        docenteOcasional.setTipoDocente("OCASIONAL");

        try {
            userService.registerUser(docenteOcasional);
            System.out.println("✅ Docente ocasional registrado exitosamente.");
        } catch (Exception e) {
            System.out.println("❌ ERROR: " + e.getMessage());
        }

        // 3. Registrar un Estudiante
        User estudiante = new User();
        estudiante.setEmail("estudiante.uno@unicauca.edu.co");
        estudiante.setPassword("Estud789!Uno");
        estudiante.setNombres("Ana");
        estudiante.setApellidos("Gómez");
        estudiante.setPrograma("Ingeniería de Sistemas");
        estudiante.setRol("ESTUDIANTE");

        try {
            userService.registerUser(estudiante);
            System.out.println("✅ Estudiante registrado exitosamente.");
        } catch (Exception e) {
            System.out.println("❌ ERROR: " + e.getMessage());
        }

        // 4. Registrar un Coordinador
        User coordinador = new User();
        coordinador.setEmail("coordinador.fiet@unicauca.edu.co");
        coordinador.setPassword("Coord!2025FIET");
        coordinador.setNombres("Coordinador");
        coordinador.setApellidos("FIET");
        coordinador.setPrograma("Ingeniería de Sistemas");
        coordinador.setRol("COORDINADOR");

        try {
            userService.registerUser(coordinador);
            System.out.println("✅ Coordinador registrado exitosamente.\n");
        } catch (Exception e) {
            System.out.println("❌ ERROR: " + e.getMessage());
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static IUserRepository getUserRepository() {
        return userRepository;
    }
}