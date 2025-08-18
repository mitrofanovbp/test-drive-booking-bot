package io.mitrofanovbp.testdrivebot.repository;

import io.mitrofanovbp.testdrivebot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for users.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByTelegramId(Long telegramId);
}
