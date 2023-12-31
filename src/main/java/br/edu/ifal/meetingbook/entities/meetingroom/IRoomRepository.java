package br.edu.ifal.meetingbook.entities.meetingroom;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface IRoomRepository extends JpaRepository<RoomModel, UUID> {
    RoomModel findByRoomNumber(int roomNumber);
    List<RoomModel> findByCapacity(int capacity);
    List<RoomModel> findByRoomType(String roomType);
}
