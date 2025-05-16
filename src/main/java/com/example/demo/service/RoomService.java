package com.example.demo.service;

import com.example.demo.dto.request.RoomRequest;
import com.example.demo.dto.response.RoomResponse;
import com.example.demo.entity.Room;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.RoomMapper;
import com.example.demo.repository.RoomRepository;
import com.example.demo.utility.RoomCreationQueueManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.BlockingQueue;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class RoomService {
    RoomCreationQueueManager queueManager;
    RoomRepository roomRepository;
    RoomMapper roomMapper;

    public List<RoomResponse> getRooms() {
        return roomRepository.findAllWithSeats().stream().map(roomMapper::toRoomResponse).toList();
    }

    public RoomResponse getRoomById(Long id) {
        Room room = roomRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_EXIST));
        return roomMapper.toRoomResponse(room);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void createRoom(RoomRequest request) {
        if(roomRepository.existsByName(request.getName())) {
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
