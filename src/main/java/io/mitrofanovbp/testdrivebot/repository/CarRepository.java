package io.mitrofanovbp.testdrivebot.repository;

import io.mitrofanovbp.testdrivebot.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for cars.
 */
public interface CarRepository extends JpaRepository<Car, Long> {
}
