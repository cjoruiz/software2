package co.unicauca.solid.access;

import co.unicauca.solid.service.UserService;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Fábrica Singleton para crear instancias de los repositorios.
 * Lee el tipo de cada repositorio desde application.properties.
 */
public class Factory {

    private static volatile Factory instance;

    private final String userRepoType;
    private final String filePGRepoType;
    private final String proyectoGradoRepoType;
    private final String programRepoType;
    private final String mensajeInternoRepoType;

    private UserService userService;

    private Factory() {
        Properties props = loadProperties();
        this.userRepoType = props.getProperty("user.repository.type", "default").trim();
        this.filePGRepoType = props.getProperty("filepg.repository.type", "default").trim();
        this.proyectoGradoRepoType = props.getProperty("proyectogrado.repository.type", "default").trim();
        this.programRepoType = props.getProperty("program.repository.type", "default").trim();
        this.mensajeInternoRepoType = props.getProperty("mensajeinterno.repository.type", "default").trim();
    }

    private Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                props.load(input);
            } else {
                System.err.println("️ application.properties no encontrado. Usando valores por defecto.");
            }
        } catch (IOException e) {
            System.err.println(" Error al cargar application.properties: " + e.getMessage());
        }
        return props;
    }

    public static void initialize() {
        if (instance != null) {
            throw new IllegalStateException("La fábrica ya fue inicializada.");
        }
        instance = new Factory();
    }

    public static Factory getInstance() {
        if (instance == null) {
            throw new IllegalStateException("La fábrica no ha sido inicializada.");
        }
        return instance;
    }

    public void setUserService(UserService userService) {
        if (this.userService != null) {
            throw new IllegalStateException("UserService ya fue inyectado.");
        }
        this.userService = userService;
    }



    public IUserRepository getUserRepository() {
        return createRepository(userRepoType, "user", () -> new UserRepository());
    }

    public IFilePGRepository getFileRepository() {
        return createRepository(filePGRepoType, "filepg", () -> new FilePGRepository());
    }

    public IProyectoGradoRepository getProyectoGradoRepository() {
        return createRepository(proyectoGradoRepoType, "proyectogrado", () -> {
            if (userService == null) {
                throw new IllegalStateException("UserService no ha sido inyectado en la fábrica.");
            }
            return new ProyectoGradoRepository(userService);
        });
    }

    public IProgramRepository getProgramRepository() {
        return createRepository(programRepoType, "program", () -> new ProgramRepository());
    }

    public IMensajeInternoRepository getMensajeInternoRepository() {
        return createRepository(mensajeInternoRepoType, "mensajeinterno", () -> new MensajeInternoRepository());
    }

    private <T> T createRepository(String type, String repoName, java.util.function.Supplier<T> defaultSupplier) {
        switch (type) {
            case "default":
                return defaultSupplier.get();
            default:
                throw new IllegalArgumentException("Tipo de repositorio no soportado para '" + repoName + "': " + type);
        }
    }
}