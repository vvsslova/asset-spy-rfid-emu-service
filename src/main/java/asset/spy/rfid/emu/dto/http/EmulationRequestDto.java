package asset.spy.rfid.emu.dto.http;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "DTO for emulating the movement of products")
public class EmulationRequestDto {
    @NotNull(message = "Vendor name cannot be blank")
    @Schema(description = "Name of vendor", example = "Some name")
    private String vendorName;

    @NotNull(message = "Article cannot be blank")
    @Schema(description = "Article of product", example = "10000000")
    private Long article;

    @Min(value = 0, message = "Product count must be grater than 0")
    @Schema(description = "Count of product items", example = "3")
    private Integer count;

    @Min(value = 0, message = "Minimum timeout must be greater than 0")
    @Schema(description = "Minimum timeout", example = "1")
    private Integer minTimeoutMin;

    @Min(value = 0, message = "Maximum timeout must be greater than 0")
    @Schema(description = "Maximum timeout", example = "1")
    private Integer maxTimeoutMin;

    @Min(value = 0, message = "Defective frequency must be between 0 and 1")
    @Max(value = 1, message = "Defective frequency mus be between 0 and 1")
    @Schema(description = "Defective frequency", example = "0.5")
    private Double defectiveFrequency;

    @Min(value = 0, message = "Lost frequency mus be between 0 and 1")
    @Max(value = 1, message = "Lost frequency mus be between 0 and 1")
    @Schema(description = "Lost frequency", example = "0.5")
    private Double lostFrequency;

    @Min(value = 0, message = "Quick sale frequency mus be between 0 and 1")
    @Max(value = 1, message = "Quick sale frequency mus be between 0 and 1")
    @Schema(description = "Quick sale frequency", example = "0.5")
    private Double quickSaleFrequency;
}
