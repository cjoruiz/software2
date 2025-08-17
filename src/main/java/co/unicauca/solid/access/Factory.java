package co.unicauca.solid.access;

/**
 *
 * @author ASUS
 */
public class Factory {
    private static Factory instance;
    
    private Factory() {}
    
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
    
}
