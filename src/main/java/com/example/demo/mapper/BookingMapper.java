package com.example.demo.mapper;

import com.example.demo.dto.response.BookingResponse;
import com.example.demo.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {RoomMapper.class, SeatMapper.class})
public interface BookingMapper {
    @Mapping(target = "room", source = "seats", qualifiedByName = "mapFirstRoom")
    @Mapping(target = "seats", source = "seats")
    BookingResponse toBookingResponse(Booking booking);
}
