package co.unicauca.solid.access;

import co.unicauca.solid.access.IUserRepository;
import co.unicauca.solid.domain.User;
import java.util.ArrayList;
import java.util.List;

public class MockRepository implements IUserRepository {
    private List<User> users = new ArrayList<>();

    @Override
    public boolean save(User user) {
        return users.add(user);
    }

    @Override
    public User findByEmail(String email) {
        return users.stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<User> findByRole(String role) {
        return users.stream()
                .filter(u -> u.getRol().equalsIgnoreCase(role))
                .toList();
    }

    @Override
    public List<User> list() {
        return users;
    }

    public boolean validateLogin(String email, String password) {
        User user = findByEmail(email);
        return user != null && user.getPassword().equals(password);
    }
}
