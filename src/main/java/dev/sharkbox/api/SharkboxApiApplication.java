package dev.sharkbox.api;

import java.time.ZoneId;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SharkboxApiApplication {

    private static final Logger logger = LoggerFactory.getLogger("MAIN");

    public static void main(String[] args) {
        var tz = System.getenv().getOrDefault("TZ", "UTC");
        logger.info("TZ=" + tz);
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of(tz)));
        SpringApplication.run(SharkboxApiApplication.class, args);
    }

}
