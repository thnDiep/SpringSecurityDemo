package com.example.demo.repository.criteria;

import com.example.demo.dto.filter.UserSearchFilter;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import jakarta.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;

public class UserSearchContext {
    private final CriteriaBuilder cb;
    private final Root<User> root;
    private final Join<User, Role> roleJoin;

    public UserSearchContext(CriteriaBuilder cb, Root<User> root) {
        this.cb = cb;
        this.root = root;
        this.roleJoin = root.join("roles", JoinType.LEFT);
    }

    public List<Predicate> buildPredicates(UserSearchFilter filter) {
        List<Predicate> predicates = new ArrayList<>();

        if (filter.getKeyword() != null && !filter.getKeyword().isEmpty()) {
            String pattern = "%" + filter.getKeyword().toLowerCase() + "%";
            predicates.add(
                    cb.or(
                            cb.like(cb.lower(root.get("username")), pattern),
                            cb.like(cb.lower(root.get("firstName")), pattern),
                            cb.like(cb.lower(root.get("lastName")), pattern)
                    )
            );
        }

        if (filter.getFromDob() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("dob"), filter.getFromDob()));
        }

        if (filter.getToDob() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("dob"), filter.getToDob()));
        }

        if (filter.getRoles() != null && !filter.getRoles().isEmpty()) {
            predicates.add(roleJoin.get("name").in(filter.getRoles()));
        }

        return predicates;
    }
}
