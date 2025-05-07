package asset.spy.rfid.emu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableRetry
@EnableAsync
public class RfidEmuServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RfidEmuServiceApplication.class, args);
    }
}
