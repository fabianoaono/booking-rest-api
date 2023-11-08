package com.fabianoaono.booking.repository;

import com.fabianoaono.booking.entity.Block;
import com.fabianoaono.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {

    List<Block> findAllByPropertyId(Long propertyId);
}
