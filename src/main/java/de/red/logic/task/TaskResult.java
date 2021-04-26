package de.red.logic.task;

public interface TaskResult<O>{

  boolean succeded();

  O getOutput();

}
