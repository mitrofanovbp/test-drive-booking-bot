package io.mitrofanovbp.testdrivebot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request to update a car.
 */
public class CarUpdateRequest {

    @NotBlank
    @Size(max = 255)
    private String model;

    @Size(max = 2000)
    private String description;

    public CarUpdateRequest() {
    }

    public String getModel() {
        return model;
    }

    public String getDescription() {
        return description;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
