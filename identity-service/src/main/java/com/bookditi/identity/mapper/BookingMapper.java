package com.bookditi.identity.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bookditi.identity.dto.response.BookingResponse;
import com.bookditi.identity.entity.Booking;

@Mapper(
        componentModel = "spring",
        uses = {RoomMapper.class, SeatMapper.class})
public interface BookingMapper {
    @Mapping(target = "room", source = "seats", qualifiedByName = "mapFirstRoom")
    @Mapping(target = "seats", source = "seats")
    BookingResponse toBookingResponse(Booking booking);
}
