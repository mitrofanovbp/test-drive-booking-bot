package io.mitrofanovbp.testdrivebot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.OffsetDateTime;

/**
 * Booking representation for admin with expanded info.
 */
public class BookingAdminDto {

    private Long id;

    private Long userId;
    private Long userTelegramId;
    private String userName;
    private String userUsername;

    private Long carId;
    private String carModel;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private OffsetDateTime datetime;

    private String status;

    public BookingAdminDto() {
    }

    public BookingAdminDto(Long id, Long userId, Long userTelegramId, String userName, String userUsername,
                           Long carId, String carModel, OffsetDateTime datetime, String status) {
        this.id = id;
        this.userId = userId;
        this.userTelegramId = userTelegramId;
        this.userName = userName;
        this.userUsername = userUsername;
        this.carId = carId;
        this.carModel = carModel;
        this.datetime = datetime;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getUserTelegramId() {
        return userTelegramId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserUsername() {
        return userUsername;
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

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUserTelegramId(Long userTelegramId) {
        this.userTelegramId = userTelegramId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserUsername(String userUsername) {
        this.userUsername = userUsername;
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
