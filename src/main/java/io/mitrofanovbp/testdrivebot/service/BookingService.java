package io.mitrofanovbp.testdrivebot.service;

import io.mitrofanovbp.testdrivebot.dto.BookingAdminDto;
import io.mitrofanovbp.testdrivebot.dto.BookingDto;
import io.mitrofanovbp.testdrivebot.exception.BadRequestException;
import io.mitrofanovbp.testdrivebot.exception.ConflictException;
import io.mitrofanovbp.testdrivebot.exception.NotFoundException;
import io.mitrofanovbp.testdrivebot.model.Booking;
import io.mitrofanovbp.testdrivebot.model.BookingStatus;
import io.mitrofanovbp.testdrivebot.model.Car;
import io.mitrofanovbp.testdrivebot.model.User;
import io.mitrofanovbp.testdrivebot.repository.BookingRepository;
import io.mitrofanovbp.testdrivebot.repository.CarRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * Booking business logic.
 */
@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookings;
    private final CarRepository cars;

    public BookingService(BookingRepository bookings, CarRepository cars) {
        this.bookings = bookings;
        this.cars = cars;
    }

    /* ===================== TELEGRAM: free slots ===================== */

    /**
     * Returns a list of available time slots in UTC for cars for the day.
     * Window: 09:00-18:00 UTC (starting points 9..17).
     * We check the elapsed time and employment via count().
     */
    @Transactional(readOnly = true)
    public List<OffsetDateTime> freeSlotsUtc(Long carId, LocalDate dayUtc) {
        ZoneOffset Z = ZoneOffset.UTC;
        OffsetDateTime now = OffsetDateTime.now(Z);

        List<OffsetDateTime> result = new ArrayList<>();
        for (int hour = 9; hour <= 17; hour++) {
            OffsetDateTime slot = dayUtc.atTime(hour, 0).atOffset(Z);
            if (slot.isAfter(now)) {
                long taken = bookings.countByCarIdAndDatetimeAndStatus(carId, slot, BookingStatus.CONFIRMED);
                if (taken == 0) {
                    result.add(slot);
                }
            }
        }
        return result;
    }

    /* ===================== logics ===================== */

    /**
     * Creates a booking if business rules allow.
     */
    @Transactional
    public BookingDto createBooking(User user, Long carId, OffsetDateTime datetime) {
        Car car = cars.findById(carId).orElseThrow(() -> new NotFoundException("Car not found: " + carId));

        OffsetDateTime slotUtc = datetime.withOffsetSameInstant(ZoneOffset.UTC);

        validateSlot(slotUtc);

        long conflicts = bookings.countByCarIdAndDatetimeAndStatus(car.getId(), slotUtc, BookingStatus.CONFIRMED);
        if (conflicts > 0) {
            throw new ConflictException("This slot is already booked for the selected car");
        }

        Booking b = new Booking(user, car, slotUtc, BookingStatus.CONFIRMED);
        try {
            b = bookings.save(b);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("This slot is already booked for the selected car");
        }
        return toDto(b);
    }

    /**
     * Returns all active bookings (CONFIRMED) for a user, ordered by time.
     */
    @Transactional(readOnly = true)
    public List<BookingDto> getActiveForUser(User user) {
        return bookings.findByUserIdAndStatusOrderByDatetimeAsc(user.getId(), BookingStatus.CONFIRMED)
                .stream().map(this::toDto).toList();
    }

    /**
     * Cancels a booking by user. Idempotent.
     */
    @Transactional
    public void cancelByUser(User user, Long bookingId) {
        Booking b = bookings.findByIdAndUserId(bookingId, user.getId())
                .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));
        if (b.getStatus() == BookingStatus.CANCELED) {
            return;
        }
        b.setStatus(BookingStatus.CANCELED);
        bookings.save(b);
    }

    /**
     * Admin: list all bookings with user and car details.
     */
    @Transactional(readOnly = true)
    public List<BookingAdminDto> getAllForAdmin() {
        return bookings.findAllByOrderByDatetimeDesc().stream().map(this::toAdminDto).toList();
    }

    /**
     * Admin: delete booking (hard delete).
     */
    @Transactional
    public void deleteByAdmin(Long bookingId) {
        Booking b = bookings.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));
        bookings.delete(b);
    }

    /**
     * Business rules validation.
     */
    private void validateSlot(OffsetDateTime slotUtc) {
        OffsetDateTime nowUtc = OffsetDateTime.now(ZoneOffset.UTC);
        if (!slotUtc.isAfter(nowUtc)) {
            throw new BadRequestException("Please select a future time slot");
        }
        if (slotUtc.getMinute() != 0 || slotUtc.getSecond() != 0 || slotUtc.getNano() != 0) {
            throw new BadRequestException("Time must be aligned to the top of the hour");
        }
        LocalTime time = slotUtc.toLocalTime();
        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(18, 0);
        if (time.isBefore(start) || !time.isBefore(end)) {
            throw new BadRequestException("Time must be within working hours 09:00–18:00 UTC");
        }
    }

    private BookingDto toDto(Booking b) {
        return new BookingDto(
                b.getId(),
                b.getCar().getId(),
                b.getCar().getModel(),
                b.getDatetime(),
                b.getStatus().name()
        );
    }

    private BookingAdminDto toAdminDto(Booking b) {
        return new BookingAdminDto(
                b.getId(),
                b.getUser().getId(),
                b.getUser().getTelegramId(),
                b.getUser().getName(),
                b.getUser().getUsername(),
                b.getCar().getId(),
                b.getCar().getModel(),
                b.getDatetime(),
                b.getStatus().name()
        );
    }
}
