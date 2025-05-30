package com.bookditi.identity.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Named;

import com.bookditi.identity.dto.request.RoomRequest;
import com.bookditi.identity.dto.response.RoomBookingResponse;
import com.bookditi.identity.dto.response.RoomResponse;
import com.bookditi.identity.entity.Room;
import com.bookditi.identity.entity.Seat;

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
