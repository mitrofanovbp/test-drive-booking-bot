package io.mitrofanovbp.testdrivebot.controller;

import io.mitrofanovbp.testdrivebot.dto.BookingAdminDto;
import io.mitrofanovbp.testdrivebot.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin REST controller for bookings.
 */
@RestController
@RequestMapping("/api/admin/bookings")
public class AdminBookingController {

    private final BookingService bookingService;

    public AdminBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<BookingAdminDto> listAll() {
        return bookingService.getAllForAdmin();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        bookingService.deleteByAdmin(id);
    }
}
