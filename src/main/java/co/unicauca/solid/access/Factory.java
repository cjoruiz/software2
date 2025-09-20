package co.unicauca.solid.access;

import co.unicauca.solid.service.UserService;

/**
 * Fábrica para crear instancias de los repositorios. Implementa el patrón de
 * diseño Factory Method.
 *
 * @author ASUS
 */
public class Factory {

    private static Factory instance;
    private final UserService userService;

    private Factory(UserService userService) {
        this.userService = userService;
    }

    public static Factory getInstance(UserService userService) {
        if (instance == null) {
            instance = new Factory(userService);
        }
        return instance;
    }

    public IUserRepository getUserRepository(String type) {
        IUserRepository result = null;
        switch (type) {
            case "default":
                result = new UserRepository();
                break;
            default:
                throw new IllegalArgumentException("Tipo de repositorio no soportado: " + type);
        }
        return result;
    }

    public IFilePGRepository getFileRepository(String type) {
        IFilePGRepository result = null;
        switch (type) {
            case "default":
                result = new FilePGRepository();
                break;
            default:
                throw new IllegalArgumentException("Tipo de repositorio no soportado: " + type);
        }
        return result;
    }

    public IProyectoGradoRepository getProyectoGradoRepository(String type) {
        IProyectoGradoRepository result = null;
        switch (type) {
            case "default":
                result = new ProyectoGradoRepository(userService); // <-- ¡¡¡ CORREGIDO !!!
                break;
            default:
                throw new IllegalArgumentException("Tipo de repositorio no soportado: " + type);
        }
        return result;
    }

    public IProgramRepository getProgramRepository(String type) {
        IProgramRepository result = null;
        switch (type) {
            case "default":
                result = new ProgramRepository();
                break;
            default:
                throw new IllegalArgumentException("Tipo de repositorio no soportado: " + type);
        }
        return result;
    }

    public IMensajeInternoRepository getMensajeInternoRepository(String type) {
        IMensajeInternoRepository result = null;
        switch (type) {
            case "default":
                result = new MensajeInternoRepository();
                break;
            default:
                throw new IllegalArgumentException("Tipo de repositorio no soportado: " + type);
        }
        return result;
    }
}
