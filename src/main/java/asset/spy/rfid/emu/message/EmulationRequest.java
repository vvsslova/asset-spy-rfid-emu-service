package asset.spy.rfid.emu.message;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmulationRequest {
    @NotNull(message = "Vendor name cannot be blank")
    private String vendorName;

    @NotNull(message = "Article cannot be blank")
    private Long article;

    @Min(value = 0, message = "Product count must be grater than 0")
    private Integer count;

    @Min(value = 0, message = "Minimum timeout must be greater than 0")
    private Integer minTimeoutMin;

    @Min(value = 0, message = "Maximum timeout must be greater than 0")
    private Integer maxTimeoutMin;

    @Min(value = 0, message = "Defective frequency must be between 0 and 1")
    @Max(value = 1, message = "Defective frequency mus be between 0 and 1")
    private Double defectiveFrequency;

    @Min(value = 0, message = "Lost frequency mus be between 0 and 1")
    @Max(value = 1, message = "Lost frequency mus be between 0 and 1")
    private Double lostFrequency;

    @Min(value = 0, message = "Quick sale frequency mus be between 0 and 1")
    @Max(value = 1, message = "Quick sale frequency mus be between 0 and 1")
    private Double quickSaleFrequency;
}
