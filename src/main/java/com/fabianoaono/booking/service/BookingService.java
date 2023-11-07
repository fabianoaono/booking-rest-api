package com.fabianoaono.booking.service;

import com.fabianoaono.booking.entity.Booking;
import com.fabianoaono.booking.exception.BookingNotFoundException;
import com.fabianoaono.booking.exception.BookingOverlapException;
import com.fabianoaono.booking.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public Booking createBooking(Booking booking) throws BookingOverlapException {

        if (hasBookingOverlap(booking)) {
            throw new BookingOverlapException("Booking overlaping with an existing booking");
        }

        return bookingRepository.save(booking);
    }

    public Booking updateBooking(Long id, Booking booking) throws BookingOverlapException, BookingNotFoundException {
        if (!bookingRepository.existsById(id)) {
            throw new BookingNotFoundException("Booking with id " + id + " does not exist");
        }

        if (hasBookingOverlap(booking)) {
            throw new BookingOverlapException("Booking overlaping with an existing booking");
        }

        booking.setId(id);
        return bookingRepository.save(booking);

    }

    public void deleteBooking(Long id) throws BookingNotFoundException {

        if (!bookingRepository.existsById(id)) {
            throw new BookingNotFoundException("Booking with id " + id + " does not exist");
        }

        bookingRepository.deleteById(id);
    }

    public boolean hasBookingOverlap(Booking booking) {
        List<Booking> existingBookings = bookingRepository.findAllByPropertyId(booking.getPropertyId());

        for (Booking existingBooking : existingBookings) {
            if (existingBooking.getPropertyId().equals(booking.getPropertyId()) &&
                    isOverlap(existingBooking, booking)) {
                return true;
            }
        }
        return false;
    }

    private boolean isOverlap(Booking existingBooking, Booking newBooking) {
        return existingBooking.getStartDate().before(newBooking.getEndDate()) &&
                newBooking.getStartDate().before(existingBooking.getEndDate());
    }
}
