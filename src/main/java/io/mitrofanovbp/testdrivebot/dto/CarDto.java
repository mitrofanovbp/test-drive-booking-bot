package io.mitrofanovbp.testdrivebot.dto;

/**
 * Car representation for API.
 */
public class CarDto {

    private Long id;
    private String model;
    private String description;

    public CarDto() {
    }

    public CarDto(Long id, String model, String description) {
        this.id = id;
        this.model = model;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public String getDescription() {
        return description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
