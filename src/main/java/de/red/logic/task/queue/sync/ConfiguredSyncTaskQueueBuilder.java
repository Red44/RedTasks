package de.red.logic.task.queue.sync;

import de.red.logic.task.basic.Task;
import de.red.logic.task.error.StartTaskMustBeSet;
import de.red.logic.task.queue.TaskQueue;
import de.red.logic.task.queue.sync.ConfiguredSyncTaskQueue.TaskLinker;

public class ConfiguredSyncTaskQueueBuilder implements SyncTaskQueueBuilder {

  private TaskLinker linker = null;

  private TaskLinker pointer;

  @Override
  public SyncTaskQueueBuilder setStartTask(Task task) {
    this.linker = new TaskLinker(task);
    this.pointer = linker;
    return this;
  }

  @Override
  public SyncTaskQueueBuilder addCompletionTask(Task task) {
    checkTaskLinkerInstance();
    pointer.appendCompleteTask(task);
    return this;
  }

  @Override
  public SyncTaskQueueBuilder addFailTask(Task task) {
    checkTaskLinkerInstance();
    pointer.appendFailTask(task);
    return this;
  }

  @Override
  public SyncTaskQueueBuilder addSuccessTask(Task task) {
    checkTaskLinkerInstance();
    pointer.appendSuccessTask(task);
    return this;
  }

  @Override
  public SyncTaskQueueBuilder addCompletionTasks(Task successTask, Task failTask) {
    checkTaskLinkerInstance();
    pointer.appendSuccessTask(successTask);
    pointer.appendFailTask(failTask);
    return this;
  }

  @Override
  public SyncTaskQueueBuilder addCompletionTaskAndGoto(Task task) throws StartTaskMustBeSet {
    addCompletionTask(task);
    gotoCompletion();
    return this;
  }

  @Override
  public SyncTaskQueueBuilder addFailTaskAndGoto(Task task) throws StartTaskMustBeSet {
    addFailTask(task);
    gotoFail();
    return this;
  }

  @Override
  public SyncTaskQueueBuilder addSuccessTaskAndGoto(Task task) throws StartTaskMustBeSet {
    addSuccessTask(task);
    gotoSuccess();
    return this;
  }

  @Override
  public SyncTaskQueueBuilder gotoCompletion() {
    this.pointer = pointer.gotoCompletion();
    return this;
  }

  @Override
  public SyncTaskQueueBuilder gotoSuccess() {
    this.pointer = pointer.gotoSuccess();
    return this;
  }

  @Override
  public SyncTaskQueueBuilder gotoFail() {
    this.pointer = pointer.gotoFail();
    return this;
  }

  @Override
  public SyncTaskQueueBuilder goBack() {
    pointer.goBack();
    return this;
  }

  @Override
  public TaskQueue build() {
    checkTaskLinkerInstance();
    return new ConfiguredSyncTaskQueue(linker);
  }

  private void checkTaskLinkerInstance() throws StartTaskMustBeSet {
    if (linker == null) {
      throw new StartTaskMustBeSet();
    }
  }
}
