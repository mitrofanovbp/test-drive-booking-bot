package io.mitrofanovbp.testdrivebot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.OffsetDateTime;

/**
 * Booking representation for customer-readable responses.
 */
public class BookingDto {

    private Long id;
    private Long carId;
    private String carModel;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime datetime;

    private String status;

    public BookingDto() {
    }

    public BookingDto(Long id, Long carId, String carModel, OffsetDateTime datetime, String status) {
        this.id = id;
        this.carId = carId;
        this.carModel = carModel;
        this.datetime = datetime;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getCarId() {
        return carId;
    }

    public String getCarModel() {
        return carModel;
    }

    public OffsetDateTime getDatetime() {
        return datetime;
    }

    public String getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public void setDatetime(OffsetDateTime datetime) {
        this.datetime = datetime;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
