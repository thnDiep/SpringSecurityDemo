package com.example.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.demo.dto.response.SeatBookingResponse;
import com.example.demo.dto.response.SeatResponse;
import com.example.demo.entity.Seat;

@Mapper(componentModel = "spring")
public interface SeatMapper {
    @Mapping(source = "room.id", target = "roomId")
    SeatResponse toSeatResponse(Seat seat);

    SeatBookingResponse toSeatBookingResponse(Seat seat);
}
