package io.mitrofanovbp.testdrivebot.service;

import io.mitrofanovbp.testdrivebot.model.User;
import io.mitrofanovbp.testdrivebot.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * User service encapsulating user lookup/creation.
 */
@Service
public class UserService {

    private final UserRepository users;

    public UserService(UserRepository users) {
        this.users = users;
    }

    /**
     * Finds or creates a user by Telegram identifiers.
     */
    @Transactional
    public User findOrCreate(Long telegramId, String username, String name) {
        Optional<User> found = users.findByTelegramId(telegramId);
        if (found.isPresent()) {
            User u = found.get();
            boolean dirty = false;
            if (username != null && !username.equals(u.getUsername())) {
                u.setUsername(username);
                dirty = true;
            }
            if (name != null && !name.equals(u.getName())) {
                u.setName(name);
                dirty = true;
            }
            return dirty ? users.save(u) : u;
        }
        User created = new User(telegramId, username, name);
        return users.save(created);
    }

    /* ===== TELEGRAM helpers ===== */

    @Transactional
    public User getOrCreateByTelegramId(Long telegramId) {
        return findOrCreate(telegramId, null, null);
    }

    @Transactional
    public User getOrCreateByTelegramId(Long telegramId, String username, String name) {
        return findOrCreate(telegramId, username, name);
    }
}
