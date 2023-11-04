package br.edu.ifal.meetingbook.entities.booking;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifal.meetingbook.entities.meetingroom.IRoomRepository;
import br.edu.ifal.meetingbook.entities.resource.ResourceModel;
import br.edu.ifal.meetingbook.entities.user.IUserRepository;
import br.edu.ifal.meetingbook.entities.user.UserModel;
import br.edu.ifal.meetingbook.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    
    @Autowired
    private IBookingRepository bookingRepository;

    @Autowired
    private IRoomRepository roomRepository;

    @Autowired
    private IUserRepository userRepository;
    

    @PostMapping("/")
    public ResponseEntity<Object> create(@RequestBody BookingModel bookingModel) {
        var booking = this.bookingRepository.findByBookingNumber(bookingModel.getBookingNumber());   
        
        if(booking != null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reserva já existe");
        }
        
        if(bookingModel.getRoomId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sala não informada");
        }
        
        var bookingRoom = this.roomRepository.findById(bookingModel.getRoomId());
        
        if(bookingRoom == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sala da reserva não existe");
        }   
        
        UserModel user = bookingModel.getUser();
        UUID userId = user.getId();
        
        var bookingUser = this.userRepository.findById(userId);

        if(bookingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário dono da reserva não existe");
        }
        
        int startTime = bookingModel.getBookingStartTime();
        int endTime = bookingModel.getBookingEndTime();

        if(startTime < 0000 || startTime > 2359 || endTime < 0000 || endTime > 2359) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Horário inválido");
        }

        if(startTime > endTime) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Horário de início não pode ser maior que o horário de fim");
        }

        /*
         * POSSÍVEIS CONFLITOS NO HORÁRIO DA RESERVA
         * Listar reservas por data V
         * Verificar status da reserva V
         * Verificar horário de início e fim V
        */
        
        List<BookingModel> bookingList = this.bookingRepository.findByBookingDate((bookingModel.getBookingDate()));

        for(BookingModel bookingItem : bookingList) {
            if(bookingItem.getBookingStatus() != "Cancelada") {
                if(bookingItem.getBookingStartTime() == bookingModel.getBookingStartTime() || bookingItem.getBookingEndTime() == bookingModel.getBookingEndTime()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Horário indisponível");
                }  
            }
        }

        var bookingCreated = this.bookingRepository.save(bookingModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingCreated);
    }

    @GetMapping("/")
    public List<BookingModel> listAll(HttpServletRequest request) {
        var bookings = this.bookingRepository.findAll();
        return bookings;
    }    

    @GetMapping("/{id}")
    public ResponseEntity<Object> listOne(@PathVariable UUID id) {
        var booking = bookingRepository.findById(id);
        
        if (booking == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reserva não encontrada");
        }

        return ResponseEntity.status(HttpStatus.OK).body(booking);
    }    

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@RequestBody ResourceModel toBeUpdatedBooking, HttpServletRequest request, @PathVariable UUID id) {
        var booking = this.bookingRepository.findById(id).orElse(null);

        System.out.println(request);

        if(booking == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reserva não encontrada");
        }
        
        Utils.copyNonNullProperties(toBeUpdatedBooking, booking);
        var bookingUpdated = this.bookingRepository.save(booking);

        return ResponseEntity.ok().body(bookingUpdated);
    } 

    @DeleteMapping("/")
    public ResponseEntity<Void> deleteAll() {
        bookingRepository.deleteAll();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteOne(@PathVariable UUID id) {
        var booking = bookingRepository.findById(id);
    
        if (booking == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reserva não encontrada");
        }
    
        bookingRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    
}