package com.fabianoaono.booking.service;

import com.fabianoaono.booking.entity.Block;
import com.fabianoaono.booking.entity.Booking;
import com.fabianoaono.booking.exception.BookingNotFoundException;
import com.fabianoaono.booking.exception.BookingOverlapException;
import com.fabianoaono.booking.exception.BookingOverlapWithBlockException;
import com.fabianoaono.booking.repository.BlockRepository;
import com.fabianoaono.booking.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BlockRepository blockRepository;

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public Booking createBooking(Booking booking) throws BookingOverlapException, BookingOverlapWithBlockException {

        validateBookingOverlaps(booking);

        return bookingRepository.save(booking);
    }

    public Booking updateBooking(Long id, Booking booking) throws BookingOverlapException, BookingNotFoundException, BookingOverlapWithBlockException {

        if (!bookingRepository.existsById(id)) {
            throw new BookingNotFoundException("Booking with id " + id + " does not exist");
        }

        booking.setId(id);

        validateBookingOverlaps(booking);

        return bookingRepository.save(booking);
    }

    public void deleteBooking(Long id) throws BookingNotFoundException {

        if (!bookingRepository.existsById(id)) {
            throw new BookingNotFoundException("Booking with id " + id + " does not exist");
        }

        bookingRepository.deleteById(id);
    }

    private void validateBookingOverlaps(Booking booking) throws BookingOverlapWithBlockException, BookingOverlapException {

        if (hasBlockOverlap(booking)) {
            throw new BookingOverlapWithBlockException("Booking overlaping with an existing block");
        }

        if (hasBookingOverlap(booking)) {
            throw new BookingOverlapException("Booking overlaping with an existing booking");
        }
    }

    public boolean hasBookingOverlap(Booking booking) {

        List<Booking> existingBookings = bookingRepository.findAllByPropertyId(booking.getPropertyId());

        return existingBookings.stream()
                .anyMatch(existingBooking ->
                        !existingBooking.getId().equals(booking.getId()) && isOverlap(existingBooking, booking)
                );
    }

    private boolean hasBlockOverlap(Booking booking) {

        List<Block> existingBlocks = blockRepository.findAllByPropertyId(booking.getPropertyId());

        return existingBlocks.stream()
                .anyMatch(existingBlock -> isOverlap(existingBlock, booking));
    }

    private boolean isOverlap(Booking existingBooking, Booking newBooking) {

        return existingBooking.getStartDate().before(newBooking.getEndDate()) &&
                newBooking.getStartDate().before(existingBooking.getEndDate());
    }

    private boolean isOverlap(Block existingBlock, Booking newBooking) {

        return existingBlock.getStartDate().before(newBooking.getEndDate()) &&
                newBooking.getStartDate().before(existingBlock.getEndDate());
    }
}
