package com.bookditi.profile.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Node
public class UserProfile {
    @Id
    @GeneratedValue(generatorClass = GeneratedValue.UUIDGenerator.class)
    String id;

    @Property("userId")
    Long userId;

    String firstName;
    String lastName;
    LocalDate dob;
    String city;
}
