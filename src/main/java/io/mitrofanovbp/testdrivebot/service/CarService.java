package io.mitrofanovbp.testdrivebot.service;

import io.mitrofanovbp.testdrivebot.dto.CarCreateRequest;
import io.mitrofanovbp.testdrivebot.dto.CarDto;
import io.mitrofanovbp.testdrivebot.dto.CarUpdateRequest;
import io.mitrofanovbp.testdrivebot.exception.NotFoundException;
import io.mitrofanovbp.testdrivebot.model.Car;
import io.mitrofanovbp.testdrivebot.repository.CarRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Car management service.
 */
@Service
public class CarService {

    private final CarRepository cars;

    public CarService(CarRepository cars) {
        this.cars = cars;
    }

    /* ===================== REST (DTO) ===================== */

    /**
     * Lists all cars.
     */
    @Transactional(readOnly = true)
    public List<CarDto> listAll() {
        return cars.findAll().stream().map(this::toDto).toList();
    }

    /**
     * Gets a car by id.
     */
    @Transactional(readOnly = true)
    public CarDto get(Long id) {
        Car car = cars.findById(id).orElseThrow(() -> new NotFoundException("Car not found: " + id));
        return toDto(car);
    }

    /**
     * Creates a car.
     */
    @Transactional
    public CarDto create(CarCreateRequest req) {
        Car car = new Car(req.getModel(), req.getDescription());
        return toDto(cars.save(car));
    }

    /**
     * Updates a car.
     */
    @Transactional
    public CarDto update(Long id, CarUpdateRequest req) {
        Car car = cars.findById(id).orElseThrow(() -> new NotFoundException("Car not found: " + id));
        car.setModel(req.getModel());
        car.setDescription(req.getDescription());
        return toDto(cars.save(car));
    }

    /**
     * Deletes a car.
     */
    @Transactional
    public void delete(Long id) {
        Car car = cars.findById(id).orElseThrow(() -> new NotFoundException("Car not found: " + id));
        cars.delete(car);
    }

    private CarDto toDto(Car c) {
        return new CarDto(c.getId(), c.getModel(), c.getDescription());
    }

    /* ===================== TELEGRAM (entity helpers) ===================== */

    /**
     * Used by bot: raw list of entities.
     */
    @Transactional(readOnly = true)
    public List<Car> findAll() {
        return cars.findAll();
    }

    /**
     * Used by bot: raw entity by id.
     */
    @Transactional(readOnly = true)
    public Car getById(Long id) {
        return cars.findById(id).orElseThrow(() -> new NotFoundException("Car not found: " + id));
    }

    /**
     * Used by bot: convenient accessor for model.
     */
    @Transactional(readOnly = true)
    public String getModel(Long id) {
        return getById(id).getModel();
    }
}
