
package co.unicauca.solid.access;

import co.unicauca.solid.domain.User;
import java.util.List;

/**
 *
 * @author ASUS
 */
public interface IUserRepository {
    boolean save(User user);
    boolean validateLogin(String email, String password);
    User findByEmail(String email);
    List<User> findByRole(String role);
    List<User> list();
}
