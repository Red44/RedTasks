package de.red.logic.task.error;

public class StartTaskMustBeSet extends RuntimeException{

  public StartTaskMustBeSet() {
    super("The start task must be defined first to persist building the queue");
  }
}
