package de.red.logic.task.queue.sync;

import de.red.logic.task.basic.Task;
import de.red.logic.task.error.StartTaskMustBeSet;
import de.red.logic.task.queue.TaskQueue;

public interface SyncTaskQueueBuilder {

/*
 calling this method multiple times will delete the changes made before this was invoked
 */
  public SyncTaskQueueBuilder setStartTask(Task task);

  public SyncTaskQueueBuilder addCompletionTask(Task task) throws StartTaskMustBeSet;

  public SyncTaskQueueBuilder addFailTask(Task task) throws StartTaskMustBeSet;

  public SyncTaskQueueBuilder addSuccessTask(Task task) throws StartTaskMustBeSet;

  public SyncTaskQueueBuilder addCompletionTasks(Task successTask, Task failTask)
      throws StartTaskMustBeSet;

  public SyncTaskQueueBuilder gotoCompletion() throws StartTaskMustBeSet;

  public SyncTaskQueueBuilder gotoSuccess() throws StartTaskMustBeSet;

  public SyncTaskQueueBuilder gotoFail() throws StartTaskMustBeSet;

  public SyncTaskQueueBuilder goBack() throws StartTaskMustBeSet;

  public TaskQueue build() throws StartTaskMustBeSet;

}
