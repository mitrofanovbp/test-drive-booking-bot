package io.mitrofanovbp.testdrivebot.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Application user (Telegram user).
 */
@Entity
@Table(name = "users",
        indexes = {
                @Index(name = "idx_users_telegram_id", columnList = "telegram_id", unique = true)
        })
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "telegram_id", nullable = false, unique = true)
    private Long telegramId;

    @Column(name = "username")
    private String username;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "user")
    private List<Booking> bookings = new ArrayList<>();

    public User() {
    }

    public User(Long telegramId, String username, String name) {
        this.telegramId = telegramId;
        this.username = username;
        this.name = name;
    }


    public Long getId() {
        return id;
    }

    public Long getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(Long telegramId) {
        this.telegramId = telegramId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }
}
