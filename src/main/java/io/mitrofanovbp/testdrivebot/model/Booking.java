package io.mitrofanovbp.testdrivebot.model;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

/**
 * Booking record for a car test drive.
 * Datetime is stored as TIMESTAMPTZ in UTC (handled at JDBC driver level).
 */
@Entity
@Table(name = "bookings",
        indexes = {
                @Index(name = "idx_bookings_user_id", columnList = "user_id"),
                @Index(name = "idx_bookings_car_id", columnList = "car_id"),
                @Index(name = "idx_bookings_datetime", columnList = "datetime")
        })
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Owner user.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_bookings_user"))
    private User user;

    /**
     * Booked car.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "car_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_bookings_car"))
    private Car car;

    /**
     * Start of the booked hour slot, stored as TIMESTAMPTZ.
     * Business rules ensure it is top-of-the-hour in UTC.
     */
    @Column(name = "datetime", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime datetime;

    /**
     * Booking status.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status = BookingStatus.CONFIRMED;

    public Booking() {
    }

    public Booking(User user, Car car, OffsetDateTime datetime, BookingStatus status) {
        this.user = user;
        this.car = car;
        this.datetime = datetime;
        this.status = status;
    }


    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public OffsetDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(OffsetDateTime datetime) {
        this.datetime = datetime;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
}
