package com.example.demo.repository;

import com.example.demo.dto.UserSearchFilter;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public class UserRepositoryImpl implements UserRepositoryCustom {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Page<User> searchUsers(UserSearchFilter filter, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> user = query.from(User.class);
        Join<User, Role> roleJoin = user.join("roles", JoinType.LEFT);

        List<Predicate> predicates = buildSearchUserPredicates(filter, cb, user, roleJoin);
        query.select(user).distinct(true).where(cb.and(predicates.toArray(new Predicate[0])));
        List<User> users = entityManager.createQuery(query).setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize()).getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<User> countUser = countQuery.from(User.class);
        Join<User, Role> countRoleJoin = countUser.join("roles", JoinType.LEFT);

        List<Predicate> countPredicates = buildSearchUserPredicates(filter, cb, countUser, countRoleJoin);
        countQuery.select(cb.countDistinct(countUser)).where(cb.and(countPredicates.toArray(new Predicate[0])));
        Long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(users, pageable, total);
    }

    private List<Predicate> buildSearchUserPredicates(UserSearchFilter filter, CriteriaBuilder cb, Root<User> user, Join<User, Role> roleJoin) {
        List<Predicate> predicates = new ArrayList<>();

        if (filter.getKeyword() != null && !filter.getKeyword().isEmpty()) {
            String pattern = "%" + filter.getKeyword().toLowerCase() + "%";
            predicates.add(
                    cb.or(
                            cb.like(cb.lower(user.get("username")), pattern),
                            cb.like(cb.lower(user.get("firstName")), pattern),
                            cb.like(cb.lower(user.get("lastName")), pattern)
                    )
            );
        }

        if (filter.getFromDob() != null) {
            predicates.add(cb.greaterThanOrEqualTo(user.get("dob"), filter.getFromDob()));
        }

        if (filter.getToDob() != null) {
            predicates.add(cb.lessThanOrEqualTo(user.get("dob"), filter.getToDob()));
        }

        if (filter.getRoles() != null && !filter.getRoles().isEmpty()) {
            predicates.add(roleJoin.get("name").in(filter.getRoles()));
        }

        return predicates;
    }
}
