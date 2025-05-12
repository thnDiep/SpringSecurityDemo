package com.example.demo.mapper;

import com.example.demo.constant.BookingStatus;
import com.example.demo.constant.SeatStatus;
import com.example.demo.dto.response.BookingResponse;
import com.example.demo.dto.response.SeatResponse;
import com.example.demo.entity.Seat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SeatMapper {
    SeatResponse toSeatResponse (Seat seat);

    @Mapping(target = "roomName", source = "room.name")
    @Mapping(target = "seatCode", source = "code")
    @Mapping(target = "status", expression = "java(determineBookingStatus(seat))")
    BookingResponse toBookingResponse(Seat seat);

    // Custom logic to derive booking status
    default BookingStatus determineBookingStatus(Seat seat) {
        return switch (seat.getStatus()) {
            case BLOCKED -> BookingStatus.WAITING_PAYMENT;
            case BOOKED -> BookingStatus.SUCCESS;
            case AVAILABLE -> BookingStatus.FAIL; // hoặc NONE nếu bạn muốn rõ hơn
        };
    }
}
