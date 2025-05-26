package asset.spy.rfid.emu.dto.context;

import lombok.Builder;

@Builder
public record TimeoutSettingContext(int minTimeoutMin, int maxTimeoutMin) {
}
