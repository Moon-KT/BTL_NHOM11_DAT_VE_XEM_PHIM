package org.example.final_btl_datve.controller;

import org.example.final_btl_datve.dto.RoomDto;
import org.example.final_btl_datve.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> reads(){
        return ResponseEntity.ok().body(roomService.reads());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> read(@PathVariable Long id) throws Exception{
        return ResponseEntity.ok().body(roomService.read(id));
    }


    @GetMapping("/room/{id}/seats/{seatRow}/{seatNumber}")
    public ResponseEntity<?> readSeat(
            @PathVariable Long id,
            @PathVariable String seatRow, // Đổi int thành String
            @PathVariable int seatNumber) throws Exception {
        return ResponseEntity.ok().body(roomService.readSeat(id, seatRow, seatNumber));
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody RoomDto screeningRoomDto) throws Exception{
        return ResponseEntity.ok().body(roomService.update(id, screeningRoomDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) throws Exception{
        roomService.delete(id);
        return ResponseEntity.ok("Xóa phòng chiếu thành công");
    }
}

