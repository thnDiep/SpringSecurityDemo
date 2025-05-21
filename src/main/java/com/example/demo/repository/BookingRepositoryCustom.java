package com.example.demo.repository;

import com.example.demo.dto.filter.BookingSearchFilter;
import com.example.demo.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingRepositoryCustom {
    Page<Booking> searchBooking(BookingSearchFilter filter, Pageable pageable);
}
