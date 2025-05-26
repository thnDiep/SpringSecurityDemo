package com.example.demo.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Named;

import com.example.demo.dto.request.RoomRequest;
import com.example.demo.dto.response.RoomBookingResponse;
import com.example.demo.dto.response.RoomResponse;
import com.example.demo.entity.Room;
import com.example.demo.entity.Seat;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    Room toRoom(RoomRequest request);

    RoomResponse toRoomResponse(Room room);

    RoomBookingResponse toRoomBookingResponse(Room room);

    @Named("mapFirstRoom")
    default RoomBookingResponse toRoomBookingResponseFromSeatList(List<Seat> seats) {
        return seats == null || seats.isEmpty()
                ? null
                : toRoomBookingResponse(seats.get(0).getRoom());
    }
}
