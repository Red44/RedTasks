package de.red.logic.task;

import de.red.logic.task.basic.Task;

public interface SyncTaskQueueAccess {

  public void addCompletionTask(Task task);

  public void addFailTask(Task task);

  public void addSuccessTask(Task task);

  public void addCompletionTasks(Task successTask, Task failTask);

}
