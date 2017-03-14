/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;


public enum CoreEventType {
     JOB_CREATED("A new job has been created"),
     JOB_UPDATED("Job has been updated"),
     JOb_REMOVED("Job has been removed"),
     JOB_STATUS_CHANGED("Job change the status"),
     JOB_RESULT("New results for the job"),
     JOB_NEW_LOG("A new log entry for the job"),

     SSH_CONNECTION_ERROR("The ssh client is not connected");

     private final String name;

     CoreEventType(String name) {
          this.name = name;
     }

     public String toString() {
          return name;
     }

}
