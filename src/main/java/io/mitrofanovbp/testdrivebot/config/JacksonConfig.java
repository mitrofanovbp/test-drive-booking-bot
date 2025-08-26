package io.mitrofanovbp.testdrivebot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneOffset;

/**
 * Configures Jackson to handle JavaTime properly and write timestamps as ISO-8601 strings in UTC.
 */
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // ObjectMapper does not hold timezone for OffsetDateTime; we keep values with offsets.
        mapper.setTimeZone(java.util.TimeZone.getTimeZone(ZoneOffset.UTC));
        return mapper;
    }
}
