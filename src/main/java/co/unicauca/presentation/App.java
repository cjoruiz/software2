package co.unicauca.presentation;

import co.unicauca.solid.access.Factory;
import co.unicauca.solid.access.IUserRepository;
import co.unicauca.solid.service.UserService;
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
    private static UserService userService; // AÑADIDO
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        // ✅ INICIALIZACIÓN CORRECTA - igual que en consola
        inicializarServicios(); // AÑADIDO

        // Cargar vista de login - RUTA ACTUALIZADA para /views/
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();

        // Configurar controlador
        LoginController controller = loader.getController();
        controller.setUserRepository(userRepository);
        controller.setPrimaryStage(primaryStage);
        controller.setUserService(userService); // AÑADIDO si es necesario

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

        // Añadir styles.css si existe
        try {
            scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("Styles.css no encontrado, continuando sin estilos");
        }

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.setTitle("Sistema de Proyectos de Grado");
        stage.show();
    }

    public static void setRoot(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml"));
                    Parent root = loader.load();

        // Configurar dependencias según el controlador
        Object controller = loader.getController();

        if (controller instanceof RegisterController) {
            RegisterController registerController = (RegisterController) controller;
            registerController.setUserRepository(userRepository);
            registerController.setUserService(userService);
            
            // Ajustar tamaño de la ventana para el registro
            primaryStage.setWidth(400);  // Ancho adecuado para el formulario
            primaryStage.setHeight(750); // Alto suficiente para mostrar todos los campos
            primaryStage.centerOnScreen();
            
        } else if (controller instanceof LoginController) {
            LoginController loginController = (LoginController) controller;
            loginController.setUserRepository(userRepository);
            loginController.setUserService(userService);
            loginController.setPrimaryStage(primaryStage);
            
            // Volver al tamaño original para login
            primaryStage.setWidth(730);
            primaryStage.setHeight(610);
            primaryStage.centerOnScreen();
        }
            // Agregar más controladores aquí

            scene.setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args); // ✅ EJECUTAR JavaFX, no las pruebas de consola
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static IUserRepository getUserRepository() {
        return userRepository;
    }

    /**
     * ✅ INICIALIZACIÓN IDÉNTICA A LA VERSIÓN DE CONSOLA
     */
    private static void inicializarServicios() {
        System.out.println("Inicializando servicios...");
        
        // Crear UserService primero
        userRepository = new co.unicauca.solid.access.UserRepository(); // Crear directamente
        userService = new UserService(userRepository);
        
        // Inicializar Factory con el UserService (según tu Factory actual)
        Factory factory = Factory.getInstance(userService);
        
        System.out.println("✅ Servicios inicializados correctamente");
    }
    
}