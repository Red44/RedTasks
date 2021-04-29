package de.red.logic.task.async;

import de.red.logic.task.basic.TaskResult;

public interface AsyncTaskResult<O> extends TaskResult<O> {

  public String getGroupName();

  public int getTaskId();

}
