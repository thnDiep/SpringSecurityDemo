package com.bookditi.identity.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;

import com.bookditi.identity.constant.BookingStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToMany
    List<Seat> seats;

    @Enumerated(EnumType.STRING)
    BookingStatus status;

    LocalDateTime bookingTime;
}
