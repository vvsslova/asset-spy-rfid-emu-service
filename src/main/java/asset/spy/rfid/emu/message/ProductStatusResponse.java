package asset.spy.rfid.emu.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductStatusResponse {
    private String itemId;
    private Integer article;
    private String productStatus;
    private Long timestamp;
}
