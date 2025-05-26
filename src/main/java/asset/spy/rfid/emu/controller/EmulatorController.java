package asset.spy.rfid.emu.controller;

import asset.spy.rfid.emu.dto.http.kafka.EmulationRequestDto;
import asset.spy.rfid.emu.service.EmulationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/rfid")
@RequiredArgsConstructor
public class EmulatorController {

    private final EmulationService emulationService;

    @PostMapping("/emulate")
    public ResponseEntity<String> startEmulator(@Valid @RequestBody EmulationRequestDto request) {
        emulationService.processEmulationRequest(request);
        return ResponseEntity.accepted().body("OK");
    }
}
