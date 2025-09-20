
package co.unicauca.solid.access;

import co.unicauca.solid.domain.Usuario;
import java.util.List;

/**
 *
 * @author ASUS
 */
public interface IUserRepository {
    boolean save(Usuario newUser);
    boolean update(Usuario user);
    boolean validateLogin(String email, String password);
    Usuario findByEmail(String email);
    List<Usuario> findByRole(String role);
    List<Usuario> list();
}
