package com.example.demo.service;

import com.example.demo.dto.request.RoomRequest;
import com.example.demo.dto.response.RoomResponse;
import com.example.demo.entity.Room;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.RoomMapper;
import com.example.demo.repository.RoomRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class RoomService {
    RoomRepository roomRepository;
    SeatService seatService;
    RoomMapper roomMapper;

    public RoomResponse getRoomById(Long id) {
        Room room = roomRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_EXIST));

        var seats = room.getSeats();
        return roomMapper.toRoomResponse(room);
    }

    public List<RoomResponse> getRooms() {
        return roomRepository.findAllWithSeats().stream().map(roomMapper::toRoomResponse).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public RoomResponse createRoom(RoomRequest request) {
        if(roomRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.ROOM_NAME_EXISTED);
        }

        if(roomRepository.existsByPrefixCode(request.getPrefixCode())) {
            throw new AppException(ErrorCode.ROOM_CODE_PREFIX_EXISTED);
        }

        Room room = roomRepository.save(roomMapper.toRoom(request));
        seatService.createSeatList(room, 20);

        return roomMapper.toRoomResponse(room);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }
}
