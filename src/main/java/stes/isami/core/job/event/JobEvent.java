package stes.isami.core.job.event;

import java.util.UUID;

/**
 * Created by cosmin on 22/04/2017.
 */
public class JobEvent {

     private UUID id;

     public  JobEvent(UUID id) {
          this.id = id;
     }

     public UUID getId() {
          return id;
     }
}
