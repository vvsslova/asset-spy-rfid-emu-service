package asset.spy.rfid.emu.dto.http.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductStatusMessage {
    private String itemId;
    private Long article;
    private String productStatus;
    private OffsetDateTime timestamp;
}
