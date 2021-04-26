package de.red.logic.task.async;

import de.red.logic.TaskGroup;
import de.red.logic.task.Task;

public interface AsyncTask<I,O> extends Task<I,O> {

  public TaskGroup getTaskGroup();

}
