package br.edu.ifal.meetingbook.meetingroom;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/rooms")
public class RoomController {
    
    @Autowired
    private IRoomRepository roomRepository;

    @PostMapping("/")
    public ResponseEntity<Object> create(@RequestBody RoomModel roomModel) {
        var room = this.roomRepository.findByRoomNumber(roomModel.getRoomNumber());

        if(room != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sala de reunião ");
        }

        var roomCreated = this.roomRepository.save(roomModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(roomCreated);
    }

   @GetMapping("/")
    public List<RoomModel> listAll(HttpServletRequest request) {
        var rooms = this.roomRepository.findAll();
        return rooms;
    }

     @GetMapping("/{id}")
    public ResponseEntity<Object> listOne(@PathVariable UUID id) {
        var room = roomRepository.findById(id);

        if (room == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sala não encontrada");
        }

        return ResponseEntity.status(HttpStatus.OK).body(room);
    }

    @DeleteMapping("/")
    public ResponseEntity<Void> deleteAll() {
        roomRepository.deleteAll();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    
}
