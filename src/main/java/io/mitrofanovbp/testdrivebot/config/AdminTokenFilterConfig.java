package io.mitrofanovbp.testdrivebot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registers AdminTokenFilter for /api/admin/** path.
 */
@Configuration
public class AdminTokenFilterConfig {

    @Bean
    public FilterRegistrationBean<AdminTokenFilter> adminTokenFilterRegistration(AppProperties props, ObjectMapper mapper) {
        FilterRegistrationBean<AdminTokenFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(new AdminTokenFilter(props.getAdminToken(), mapper));
        reg.addUrlPatterns("/api/admin/*");
        reg.setOrder(1);
        return reg;
    }
}
