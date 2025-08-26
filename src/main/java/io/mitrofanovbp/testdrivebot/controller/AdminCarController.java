package io.mitrofanovbp.testdrivebot.controller;

import io.mitrofanovbp.testdrivebot.dto.CarCreateRequest;
import io.mitrofanovbp.testdrivebot.dto.CarDto;
import io.mitrofanovbp.testdrivebot.dto.CarUpdateRequest;
import io.mitrofanovbp.testdrivebot.service.CarService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin REST controller for cars.
 */
@RestController
@RequestMapping("/api/admin/cars")
public class AdminCarController {

    private final CarService carService;

    public AdminCarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping
    public List<CarDto> list() {
        return carService.listAll();
    }

    @GetMapping("/{id}")
    public CarDto get(@PathVariable Long id) {
        return carService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CarDto create(@RequestBody @Valid CarCreateRequest req) {
        return carService.create(req);
    }

    @PutMapping("/{id}")
    public CarDto update(@PathVariable Long id, @RequestBody @Valid CarUpdateRequest req) {
        return carService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        carService.delete(id);
    }
}
