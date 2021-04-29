package de.red.logic.task.queue;

import de.red.logic.task.basic.Task;
import de.red.logic.task.basic.TaskResult;

public interface TaskQueue extends Task {

  public TaskResult startQueue(Object input);

  public TaskResult startQueue();


}
