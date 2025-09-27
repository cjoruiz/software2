package co.unicauca.presentation;

import co.unicauca.solid.access.Factory;
import co.unicauca.solid.access.IUserRepository;
import co.unicauca.solid.service.FilePGService;
import co.unicauca.solid.service.MensajeInternoService;
import co.unicauca.solid.service.ProyectoGradoService;
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
    private double xOffset = 0;
    private double yOffset = 0;

    // ✅ Servicios globales (singleton)
    private static UserService userService;
    private static ProyectoGradoService proyectoGradoService;
    private static FilePGService filePGService;
    private static MensajeInternoService mensajeInternoService;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        inicializarServicios();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();

        LoginController controller = loader.getController();
        controller.setUserService(userService);
        controller.setPrimaryStage(primaryStage);

        scene = new Scene(root, 730, 610);
        scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());

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
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml"));
            Parent root = loader.load();

            Object controller = loader.getController();

            if (controller instanceof RegisterController) {
                RegisterController registerController = (RegisterController) controller;
                registerController.setUserService(userService);
                registerController.setPrimaryStage(primaryStage);
                primaryStage.setWidth(400);
                primaryStage.setHeight(750);
                primaryStage.centerOnScreen();

            } else if (controller instanceof LoginController) {
                LoginController loginController = (LoginController) controller;
                loginController.setUserService(userService);
                loginController.setPrimaryStage(primaryStage);
                primaryStage.setWidth(730);
                primaryStage.setHeight(610);
                primaryStage.centerOnScreen();

            }

            scene.setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static UserService getUserService() {
        return userService;
    }

    public static ProyectoGradoService getProyectoGradoService() {
        return proyectoGradoService;
    }

    public static FilePGService getFilePGService() {
        return filePGService;
    }

    public static MensajeInternoService getMensajeInternoService() {
        return mensajeInternoService;
    }

    private static void inicializarServicios() {
        Factory.initialize();
        Factory factory = Factory.getInstance();

        IUserRepository userRepository = factory.getUserRepository();

        userService = new UserService(userRepository);

        factory.setUserService(userService);

        proyectoGradoService = new ProyectoGradoService(
                factory.getProyectoGradoRepository(),
                userService
        );

        filePGService = new FilePGService(
                factory.getFileRepository(),
                factory.getProyectoGradoRepository()
        );

        mensajeInternoService = new MensajeInternoService(
                factory.getMensajeInternoRepository(),
                userService
        );

    }
}
