package com.fabianoaono.booking.controller;

import com.fabianoaono.booking.entity.Booking;
import com.fabianoaono.booking.service.BookingService;
import com.fabianoaono.booking.exception.BookingNotFoundException;
import com.fabianoaono.booking.exception.BookingOverlapException;
import com.fabianoaono.booking.exception.BookingOverlapWithBlockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {

        this.bookingService = bookingService;
    }

    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {

        return bookingService.getBookingById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestBody Booking booking) {

        if (booking == null || booking.getPropertyId() == null ||
                booking.getStartDate() == null || booking.getEndDate() == null) {
            return ResponseEntity.badRequest().body("Booking data is invalid.");
        }

        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(bookingService.createBooking(booking));
        } catch (BookingOverlapException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Booking overlap detected: " + e.getMessage());
        } catch (BookingOverlapWithBlockException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Booking overlap with block detected: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateBooking(@PathVariable Long id, @RequestBody Booking booking) {

        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(bookingService.updateBooking(id, booking));
        } catch (BookingOverlapException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Booking overlap detected: " + e.getMessage());
        } catch (BookingOverlapWithBlockException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Booking overlap with block detected: " + e.getMessage());
        } catch (BookingNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteBooking(@PathVariable Long id) {

        try {
            bookingService.deleteBooking(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (BookingNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking not found: " + e.getMessage());
        }
    }
}
