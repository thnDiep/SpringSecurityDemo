package com.example.demo.repository;

import com.example.demo.dto.UserSearchFilter;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

public class UserRepositoryImpl implements UserRepositoryCustom {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<User> searchUsers(UserSearchFilter filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);

        Root<User> user = query.from(User.class);
        Join<User, Role> roleJoin = user.join("roles", JoinType.LEFT);

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

        if(filter.getFromDob() != null) {
            predicates.add(cb.greaterThanOrEqualTo(user.get("dob"), filter.getFromDob()));
        }

        if(filter.getToDob() != null) {
            predicates.add(cb.lessThanOrEqualTo(user.get("dob"), filter.getToDob()));
        }

        if(filter.getRoles() != null && !filter.getRoles().isEmpty()) {
            predicates.add(roleJoin.get("name").in(filter.getRoles()));
        }

        query.select(user).distinct(true).where(cb.and(predicates.toArray(new Predicate[0])));
        return entityManager.createQuery(query).getResultList();
    }
}
