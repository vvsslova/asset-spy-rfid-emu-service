package asset.spy.rfid.emu.model;

import lombok.Getter;

@Getter
public enum ProductStatus {
    RECEIVED_IN_WAREHOUSE("поступил_на_склад"),
    SORTED("отсортирован"),
    READY_FOR_SHIPPING("готов_к_отгрузке"),
    ON_THE_WAY_TO_STORE("на_пути_в_магазин"),
    ARRIVED_AT_STORE("товар_поступил_в_магазин"),
    SOLD("продан"),
    DEFECTIVE("отбракован"),
    LOST("утерян");

    private final String value;

    ProductStatus(String value) {
        this.value = value;
    }
}
