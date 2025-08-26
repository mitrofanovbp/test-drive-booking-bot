package io.mitrofanovbp.testdrivebot.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Car catalog entity.
 */
@Entity
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "car")
    private List<Booking> bookings = new ArrayList<>();

    public Car() {
    }

    public Car(String model, String description) {
        this.model = model;
        this.description = description;
    }


    public Long getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }
}
