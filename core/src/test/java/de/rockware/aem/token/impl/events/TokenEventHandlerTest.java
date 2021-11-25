package de.rockware.aem.token.impl.events;

import com.day.cq.wcm.api.PageEvent;
import de.rockware.aem.token.api.service.TokenService;
import de.rockware.aem.token.impl.service.DefaultTokenService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.event.jobs.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class TokenEventHandlerTest {

    private final AemContext ctx = new AemContext();

    @Mock
    JobManager jobManager;

    @BeforeEach
    void setUp() {
        ctx.load().json("/de/rockware/aem/token/api/resource/ExperiencePage.json", "/content");
        ctx.currentResource("/content/experiencepage");
        DefaultTokenService dtService = new DefaultTokenService();
        ctx.registerService(TokenService.class, dtService);
        ctx.registerService(JobManager.class, jobManager);
        TokenEventHandler eventHandler = new TokenEventHandler();
        ctx.registerService(TokenEventHandler.class, eventHandler);
    }

    @Test
    void handleEvent() {
        Map<String, String> properties = new HashMap<>();
        Event event = new Event("myTopic", properties);
        properties.put("event.application", "abcde");
        properties.put("modifications", null);
        TokenEventHandler handler = ctx.getService(TokenEventHandler.class);
        handler.handleEvent(event);
    }

    @Test
    void process() {
        Map<String, String> properties = new HashMap<>();
        Event event = new Event("myTopic", properties);
        properties.put("event.application", "abcde");
        properties.put("modifications", null);
        TokenEventHandler handler = ctx.getService(TokenEventHandler.class);
        Job job = mock(Job.class);
        when(job.getProperty("pageEvent")).thenReturn(PageEvent.fromEvent(event));
        handler.process(job);
    }
}