package com.bookditi.identity.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bookditi.identity.dto.filter.BookingSearchFilter;
import com.bookditi.identity.entity.Booking;

public interface BookingRepositoryCustom {
    Page<Booking> searchBooking(BookingSearchFilter filter, Pageable pageable);
}
