package com.bookditi.identity.service;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookditi.identity.dto.request.RoomRequest;
import com.bookditi.identity.dto.response.RoomResponse;
import com.bookditi.identity.entity.Room;
import com.bookditi.identity.exception.AppException;
import com.bookditi.identity.exception.ErrorCode;
import com.bookditi.identity.mapper.RoomMapper;
import com.bookditi.identity.repository.RoomRepository;
import com.bookditi.identity.utility.RoomCreationQueueManager;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class RoomService {
    RoomCreationQueueManager queueManager;
    RoomRepository roomRepository;
    RoomMapper roomMapper;

    public List<RoomResponse> getRooms() {
        return roomRepository.findAllWithSeats().stream()
                .map(roomMapper::toRoomResponse)
                .toList();
    }

    public RoomResponse getRoomById(Long id) {
        Room room = roomRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_EXIST));
        return roomMapper.toRoomResponse(room);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void createRoom(RoomRequest request) {
        if (roomRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.ROOM_NAME_EXISTED);
        }

        Room room = roomRepository.save(roomMapper.toRoom(request));
        queueManager.submit(room);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }
}
