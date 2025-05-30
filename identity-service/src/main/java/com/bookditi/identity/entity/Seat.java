package com.bookditi.identity.entity;

import jakarta.persistence.*;

import com.bookditi.identity.constant.SeatStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "seat", uniqueConstraints = @UniqueConstraint(columnNames = {"room_id", "code"}))
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String code;

    @Enumerated(EnumType.STRING)
    SeatStatus status = SeatStatus.AVAILABLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    Room room;

    @Version
    Integer version; // Optimistic lock
}
