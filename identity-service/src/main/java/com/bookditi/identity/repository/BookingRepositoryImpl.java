package com.bookditi.identity.repository;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.bookditi.identity.dto.filter.BookingSearchFilter;
import com.bookditi.identity.entity.Booking;
import com.bookditi.identity.repository.criteria.BookingSearchContext;

public class BookingRepositoryImpl implements BookingRepositoryCustom {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Page<Booking> searchBooking(BookingSearchFilter filter, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Booking> query = cb.createQuery(Booking.class);
        Root<Booking> root = query.from(Booking.class);
        BookingSearchContext ctx = new BookingSearchContext(cb, root);

        List<Predicate> predicates = ctx.buildPredicates(filter);

        query.select(root).distinct(true).where(cb.and(predicates.toArray(new Predicate[0])));
        List<Booking> bookings = entityManager
                .createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Booking> countRoot = countQuery.from(Booking.class);
        BookingSearchContext countCtx = new BookingSearchContext(cb, countRoot);

        List<Predicate> countPredicates = countCtx.buildPredicates(filter);
        countQuery.select(cb.countDistinct(countRoot)).where(cb.and(countPredicates.toArray(new Predicate[0])));

        Long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(bookings, pageable, total);
    }
}
