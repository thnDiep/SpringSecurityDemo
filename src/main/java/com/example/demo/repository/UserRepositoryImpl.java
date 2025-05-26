package com.example.demo.repository;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.example.demo.dto.filter.UserSearchFilter;
import com.example.demo.entity.User;
import com.example.demo.repository.criteria.UserSearchContext;

public class UserRepositoryImpl implements UserRepositoryCustom {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Page<User> searchUsers(UserSearchFilter filter, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> user = query.from(User.class);
        UserSearchContext ctx = new UserSearchContext(cb, user);

        List<Predicate> predicates = ctx.buildPredicates(filter);
        query.select(user).distinct(true).where(cb.and(predicates.toArray(new Predicate[0])));
        List<User> users = entityManager
                .createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<User> countUser = countQuery.from(User.class);
        UserSearchContext countCtx = new UserSearchContext(cb, countUser);

        List<Predicate> countPredicates = countCtx.buildPredicates(filter);
        countQuery.select(cb.countDistinct(countUser)).where(cb.and(countPredicates.toArray(new Predicate[0])));
        Long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(users, pageable, total);
    }
}
