package de.red.logic.task.queue.sync;

import de.red.logic.task.basic.Task;
import de.red.logic.task.basic.TaskResult;
import de.red.logic.task.queue.TaskQueue;
import java.util.Optional;

public class ConfiguredSyncTaskQueue implements TaskQueue {

  protected TaskLinker taskLinker;

  protected ConfiguredSyncTaskQueue(TaskLinker linker) {
    taskLinker = linker;
  }

  @Override
  public TaskResult operate(Object input) {
    return traceDown(taskLinker, input);
  }

  public TaskResult startQueue(Object input) {
    return operate(input);
  }

  public TaskResult startQueue() {
    return operate(null);
  }

  public TaskResult traceDown(TaskLinker link, Object input) {
    TaskResult result = link.getBaseTask().operate(input);
    if (result.succeded()) {
      if (link.gotoSuccess() == link) {
        return result;
      } else {
        return traceDown(link.gotoSuccess(), result.getOutput());
      }

    } else {
      if (link.gotoFail() == link) {
        return result;
      } else {
        return traceDown(link.gotoFail(), result.getOutput());
      }

    }
  }


  public static class TaskLinker {

    private final Task baseTask;

    private Optional<TaskLinker> success = Optional.empty();
    private Optional<TaskLinker> fail = Optional.empty();
    private Optional<TaskLinker> before = Optional.empty();

    public TaskLinker(Task task) {
      this.baseTask = task;
    }

    public TaskLinker appendCompleteTask(Task task) {
      Optional<TaskLinker> link = Optional.of(new TaskLinker(task));
      this.success = link;
      this.fail = link;
      link.get().before = Optional.of(this);
      return this;
    }

    public TaskLinker appendSuccessTask(Task task) {
      this.success = Optional.of(new TaskLinker(task));
      this.success.get().before = Optional.of(this);
      return this;
    }

    public TaskLinker appendFailTask(Task task) {
      this.fail = Optional.of(new TaskLinker(task));
      this.fail.get().before = Optional.of(this);
      return this;
    }

    public TaskLinker gotoCompletion() {
      return gotoSuccess();
    }

    public TaskLinker gotoSuccess() {
      return this.success.orElse(this);
    }

    public TaskLinker gotoFail() {
      return this.fail.orElse(this);
    }

    public TaskLinker goBack() {
      return before.orElse(this);
    }

    public Task getBaseTask() {
      return baseTask;
    }
  }

}
