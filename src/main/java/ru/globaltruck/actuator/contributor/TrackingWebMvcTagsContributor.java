package ru.globaltruck.actuator.contributor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.metrics.web.servlet.WebMvcTags;
import org.springframework.boot.actuate.metrics.web.servlet.WebMvcTagsContributor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class TrackingWebMvcTagsContributor implements WebMvcTagsContributor {

    @Value("${management.metrics.tag.default-value}")
    String tagDefaultValue;

    private final ObjectMapper objectMapper;

    @Override
    public Iterable<Tag> getTags(HttpServletRequest request, HttpServletResponse response, Object handler,
                                             Throwable exception) {
        log.debug("Adding tags started");
        List<Tag> tagList = new ArrayList<>();
        String remoteAddr = null;
        String referer = null;
        String userName = null;
        String interfaceId = null;
        String sourceSystem = null;
        try {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }

            referer = request.getHeader("Referer");

            interfaceId = request.getHeader("GT2-Interface-Id");

            sourceSystem = request.getHeader("GT2-Source-System");

            String token = request.getHeader("authorization");
            String payload = token == null ? "" : new String(
                    Base64.getUrlDecoder().decode(
                            token.replace("Bearer ", "")
                                    .split("\\.")[1]
                                    .getBytes(StandardCharsets.UTF_8)
                    )
            );
            JsonNode claims;
            claims = objectMapper.readTree(payload);
            userName = claims.get("preferred_username") == null
                    ? tagDefaultValue
                    : claims.get("preferred_username").asText();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        tagList.add(Tag.of("remoteAddr", remoteAddr == null || "".equals(remoteAddr)
                ? tagDefaultValue
                : remoteAddr));
        tagList.add(Tag.of("referer", referer == null || "".equals(referer)
                ? tagDefaultValue
                : referer));
        tagList.add(Tag.of("userName", userName == null || "".equals(userName)
                ? tagDefaultValue
                : userName));
        tagList.add(Tag.of("interfaceId", interfaceId == null || "".equals(interfaceId)
                ? tagDefaultValue
                : interfaceId));
        tagList.add(Tag.of("sourceSystem", sourceSystem == null || "".equals(sourceSystem)
                ? tagDefaultValue
                : sourceSystem));
        log.debug("Added tags: {}", tagList);
        log.debug("Adding tags is complete");

        return Tags.of(tagList);
    }

    @Override
    public Iterable<Tag> getLongRequestTags(HttpServletRequest request, Object handler) {
        return Tags.of(WebMvcTags.method(request), WebMvcTags.uri(request, null));
    }
}
