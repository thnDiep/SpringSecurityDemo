package com.bookditi.identity.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bookditi.identity.dto.response.SeatBookingResponse;
import com.bookditi.identity.dto.response.SeatResponse;
import com.bookditi.identity.entity.Seat;

@Mapper(componentModel = "spring")
public interface SeatMapper {
    @Mapping(source = "room.id", target = "roomId")
    SeatResponse toSeatResponse(Seat seat);

    SeatBookingResponse toSeatBookingResponse(Seat seat);
}
