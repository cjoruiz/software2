package co.unicauca.solid.access;

/**
 *
 * @author ASUS
 */
public class Factory {

    private static Factory instance;

    private Factory() {
    }

    public static Factory getInstance() {
        if (instance == null) {
            instance = new Factory();
        }
        return instance;
    }

    public IUserRepository getUserRepository(String type) {
        IUserRepository result = null;
        switch (type) {
            case "default":
                result = new UserRepository();
                break;
        }
        return result;
    }

    public IFormatoARepository getFileRepository(String type) {
        IFormatoARepository result = null;
        switch (type) {
            case "default":
                result = new FormatoARepository();
                break;
        }
        return result;
    }

    public IProyectoGradoRepository getProyectoGradoRepository(String type) {
        IProyectoGradoRepository result = null;
        switch (type) {
            case "default":
                result = new ProyectoGradoRepository();
                break;
        }
        return result;
    }

    public IProgramRepository getProgramRepository(String type) {
        IProgramRepository result = null;
        switch (type) {
            case "default":
                result = new ProgramRepository();
                break;
        }
        return result;
    }
}
