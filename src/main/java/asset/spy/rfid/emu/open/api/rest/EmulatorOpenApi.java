package asset.spy.rfid.emu.open.api.rest;

import asset.spy.rfid.emu.dto.http.EmulationRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Emulator", description = "Emulating the movement of products")
@ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "OK - emulating was successful", content = {
                @Content(mediaType = "application/json", schema =
                @Schema(implementation = String.class))
        }),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = {
                @Content(mediaType = "application/json", schema =
                @Schema(implementation = String.class))
        })
})
public interface EmulatorOpenApi {

    @Operation(summary = "Emulating the movement of products")
    ResponseEntity<String> startEmulator(@Valid @RequestBody EmulationRequestDto request);
}
