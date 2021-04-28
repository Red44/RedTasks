package de.red.logic.task.basic;

public interface TaskResult<O>{

  boolean succeded();

  default boolean failed(){
    return !succeded();
  }

  O getOutput();

}
