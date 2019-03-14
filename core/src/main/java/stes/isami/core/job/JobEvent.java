package stes.isami.core.job;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Job event class. Describes the events for a job
 */
public class JobEvent {

     private final JobEventType eventType;
     private final List<UUID> ids;
     private UUID id;

     public JobEvent(JobEventType eventType) {
          this(UUID.randomUUID(),eventType);
     }

     public  JobEvent(UUID id,JobEventType eventType) {
          this.eventType = eventType;
          this.id = id;
          ids = new ArrayList<>();
          getIds().add(id);
     }

     public JobEvent(List<UUID> ids, JobEventType eventType) {
          this.eventType = eventType;
          this.ids = ids;
     }

     public UUID getId() {
          return id;
     }

     public JobEventType getEventType() {
          return eventType;
     }

     public List<UUID> getIds() {
          return ids;
     }

     public enum JobEventType {
          CREATE,
          START_DELETE,
          END_DELETE,
          DELETE,
          STATE_CHANGED,
          UPDATE
     }
}
