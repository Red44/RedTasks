package de.red.logic.task.queue.sync;

import de.red.logic.task.basic.Task;
import de.red.logic.task.error.StartTaskMustBeSet;
import de.red.logic.task.queue.TaskQueue;
import de.red.logic.task.queue.sync.ConfiguredSyncTaskQueue.TaskLinker;

public class ConfiguredSyncTaskQueueBuilder implements SyncTaskQueueBuilder {

  private TaskLinker linker = null;

  @Override
  public SyncTaskQueueBuilder setStartTask(Task task) {
    this.linker = new TaskLinker(task);
    return this;
  }

  @Override
  public SyncTaskQueueBuilder addCompletionTask(Task task) {
    checkTaskLinkerInstance();
    linker.appendCompleteTask(task);
    return this;
  }

  @Override
  public SyncTaskQueueBuilder addFailTask(Task task) {
    checkTaskLinkerInstance();
    linker.appendFailTask(task);
    return this;
  }

  @Override
  public SyncTaskQueueBuilder addSuccessTask(Task task) {
    checkTaskLinkerInstance();
    linker.appendSuccessTask(task);
    return this;
  }

  @Override
  public SyncTaskQueueBuilder addCompletionTasks(Task successTask, Task failTask) {
    checkTaskLinkerInstance();
    linker.appendSuccessTask(successTask);
    linker.appendFailTask(failTask);
    return this;
  }

  @Override
  public SyncTaskQueueBuilder gotoCompletion() {
    linker.gotoCompletion();
    return this;
  }

  @Override
  public SyncTaskQueueBuilder gotoSuccess() {
    linker.gotoSuccess();
    return this;
  }

  @Override
  public SyncTaskQueueBuilder gotoFail() {
    linker.gotoFail();
    return this;
  }

  @Override
  public SyncTaskQueueBuilder goBack() {
    linker.goBack();
    return this;
  }

  @Override
  public TaskQueue build() {
    checkTaskLinkerInstance();
    return new ConfiguredSyncTaskQueue(linker);
  }

  private void checkTaskLinkerInstance() throws StartTaskMustBeSet{
    if(linker == null)
      throw new StartTaskMustBeSet();
  }
}
