package com.example.demo.repository.criteria;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.*;

import com.example.demo.dto.filter.BookingSearchFilter;
import com.example.demo.entity.*;

public class BookingSearchContext {
    private final CriteriaBuilder cb;
    private final Root<Booking> root;
    private final Join<Seat, Room> roomJoin;
    private final Join<Booking, User> userJoin;

    public BookingSearchContext(CriteriaBuilder cb, Root<Booking> root) {
        this.cb = cb;
        this.root = root;
        Join<Booking, Seat> seatsJoin = root.join("seats", JoinType.INNER);
        this.roomJoin = seatsJoin.join("room", JoinType.INNER);
        this.userJoin = root.join("user", JoinType.INNER);
    }

    public List<Predicate> buildPredicates(BookingSearchFilter filter) {
        List<Predicate> predicates = new ArrayList<>();

        if (filter.getUsername() != null && !filter.getUsername().isEmpty()) {
            predicates.add(cb.equal(userJoin.get("username"), filter.getUsername()));
        }

        if (filter.getRoomName() != null && !filter.getRoomName().isEmpty()) {
            String pattern = "%" + filter.getRoomName().toLowerCase() + "%";
            predicates.add(cb.like(cb.lower(roomJoin.get("name")), pattern));
        }

        if (filter.getStatus() != null && !filter.getStatus().isEmpty()) {
            predicates.add(root.get("status").in(filter.getStatus()));
        }

        if (filter.getFromTime() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("bookingTime"), filter.getFromTime()));
        }

        if (filter.getToTime() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("bookingTime"), filter.getToTime()));
        }
        return predicates;
    }
}
