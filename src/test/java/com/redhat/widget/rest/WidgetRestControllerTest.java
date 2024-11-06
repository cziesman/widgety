package com.redhat.widget.rest;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class WidgetRestControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(WidgetRestControllerTest.class);

    private static final String DONTCARE = "dontcare";

    private static final String DONTCARE2 = "dont care";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WidgetService widgetService;

    @Disabled
    @Test
    public void testFindWidgetByName() throws IOException, InterruptedException {

        Widget widget = create(DONTCARE2, DONTCARE);
        Widget widget2 = findByName(DONTCARE2);

        assertThat(widget2).isNotNull();
        assertThat(widget2).isEqualTo(widget);
    }

    private Widget create(String name, String description) throws IOException, InterruptedException {

        String encName = URLEncoder.encode(name, StandardCharsets.UTF_8.name());
        String encDescription = URLEncoder.encode(description, StandardCharsets.UTF_8.name());

        String command = "curl -X 'PUT' " +
                "  'http://127.0.0.1:8080/api/widget' " +
                "  -H 'accept: */*' " +
                "  -H 'Content-Type: application/json' " +
                "  -d '{ " +
                "  \"id\": null, " +
                "  \"name\": \"" +
                encName +
                "\", " +
                "  \"description\": \"" +
                encDescription +
                "\" " +
                "}'";
        List<String> result = Shell.execute(command);

        Widget retval = null;
        if (!result.isEmpty()) {
            try {
                retval = objectMapper.readValue(result.get(0), Widget.class);
                LOG.info("{}", retval);
            } catch (UnrecognizedPropertyException ex) {
                try {
                    ErrorResponse errorResponse = objectMapper.readValue(result.get(0), ErrorResponse.class);
                    LOG.info("{}", errorResponse);
                } catch (Throwable t) {
                    LOG.error(t.getMessage(), t);
                }
            }
        } else {
            LOG.info("Could not create widget with name {}", name);
        }

        return retval;
    }
    private Widget findByName(String name) throws IOException, InterruptedException {

        String encName = URLEncoder.encode(name, StandardCharsets.UTF_8.name());

        String command = "curl -X 'GET' " +
                "  'http://127.0.0.1:8080/api/widget/name/" +
                encName +
                "' " +
                "  -H 'accept: application/json'";
        List<String> result = Shell.execute(command);

        Widget retval = null;
        if (!result.isEmpty()) {
            try {
                retval = objectMapper.readValue(result.get(0), Widget.class);
                LOG.info("{}", retval);
            } catch (UnrecognizedPropertyException ex) {
                try {
                    ErrorResponse errorResponse = objectMapper.readValue(result.get(0), ErrorResponse.class);
                    LOG.info("{}", errorResponse);
                } catch (Throwable t) {
                    LOG.error(t.getMessage(), t);
                }
            }
        } else {
            LOG.info("No widget found with name {}", name);
        }

        return retval;
    }

}
