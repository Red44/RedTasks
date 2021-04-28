package de.red.logic.task.basic;

public interface AsyncTaskResult<O> extends TaskResult<O>{

  public String getGroupName();

  public int getTaskId();

}
