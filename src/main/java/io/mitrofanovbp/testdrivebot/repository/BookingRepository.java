package io.mitrofanovbp.testdrivebot.repository;

import io.mitrofanovbp.testdrivebot.model.Booking;
import io.mitrofanovbp.testdrivebot.model.BookingStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for bookings.
 */
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Returns bookings for a user with given status ordered by datetime.
     */
    List<Booking> findByUserIdAndStatusOrderByDatetimeAsc(Long userId, BookingStatus status);

    /**
     * Eagerly fetch user and car for admin listings.
     */
    @EntityGraph(attributePaths = {"user", "car"})
    List<Booking> findAllByOrderByDatetimeDesc();

    /**
     * Find booking by id and user id (used for idempotent cancel).
     */
    Optional<Booking> findByIdAndUserId(Long id, Long userId);

    /**
     * Count confirmed bookings for given car and datetime (shadow check; DB enforces partial unique index).
     */
    long countByCarIdAndDatetimeAndStatus(Long carId, OffsetDateTime datetime, BookingStatus status);
}
