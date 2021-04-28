package de.red.logic.task.adapter;

import de.red.logic.task.async.AsyncTask;
import de.red.logic.task.basic.Task;
import de.red.logic.task.basic.TaskResult;
import de.red.logic.task.scheduler.RequireExecutor;

public abstract class AsyncTaskLinker<I> implements RequireExecutor, Task<I,Void> {

  private AsyncTask startTask;

  public AsyncTaskLinker(AsyncTask task) {
    this.startTask = task;
  }


  @Override
  public TaskResult<Void> operate(I input) {
    requireExecutor().submit(()->startTask.operate(input));
    return null;
  }
}
