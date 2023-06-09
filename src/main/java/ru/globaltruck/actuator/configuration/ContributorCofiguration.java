package ru.globaltruck.actuator.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import ru.globaltruck.actuator.contributor.TrackingWebMvcTagsContributor;

public class ContributorCofiguration {

    @Bean
    public TrackingWebMvcTagsContributor trackingWebMvcTagsContributor(ObjectMapper objectMapper) {
        return new TrackingWebMvcTagsContributor(objectMapper);
    }

}
