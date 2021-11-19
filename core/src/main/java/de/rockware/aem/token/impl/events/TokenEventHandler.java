package de.rockware.aem.token.impl.events;

import com.day.cq.wcm.api.PageEvent;
import com.day.cq.wcm.api.PageModification;

import de.rockware.aem.token.api.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Track Modification Events that might be relevant for Tokens.
 */
@Component(
        service = {
                EventHandler.class,
                JobConsumer.class
        },
        immediate = true, configurationPolicy = ConfigurationPolicy.OPTIONAL,
        property = {
                "event.topics=" + PageEvent.EVENT_TOPIC,
                JobConsumer.PROPERTY_TOPICS + "=" + TokenEventHandler.JOB_TOPICS
        }
)
@Slf4j
public class TokenEventHandler implements EventHandler, JobConsumer {
    /**
     * Modification Job Topics.
     */
    public static final String JOB_TOPICS = "token/aem/modification";

    private static final String PAGE_EVENT = "pageEvent";

    @Reference
    private JobManager jobManager;

    @Reference
    private TokenService tokenService;

    @Override
    public void handleEvent(Event event) {
        log.trace("Checking event.");
        PageEvent pageEvent = PageEvent.fromEvent(event);
        if(pageEvent != null) {
            Map<String, Object> properties = new HashMap<>();
            log.trace("Event is a page event.");
            properties.put(PAGE_EVENT, pageEvent);
            log.trace("Adding new job.");
            jobManager.addJob(JOB_TOPICS, properties);
        } else {
            log.trace("Not a page event.");
        }
    }

    @Override
    public JobResult process(Job job) {
        if (job.getProperty(PAGE_EVENT) != null) {
            PageEvent pageEvent = (PageEvent) job.getProperty(PAGE_EVENT);
            Iterator<PageModification> modificationsIterator;
            modificationsIterator = pageEvent.getModifications();
            while (modificationsIterator.hasNext()) {
                PageModification modification = modificationsIterator.next();
                log.debug("Handling modification {} on path {}.", modification.getType(), modification.getPath());
                tokenService.flushMap(modification.getPath());
            }
        } else {
            log.trace("Invalid event type. Cannot help.");
        }
        return JobResult.OK;
    }
}
