package de.red.logic.task.adapter;

import de.red.logic.task.async.AsyncTaskResult;
import de.red.logic.task.queue.async.RequireAsyncTaskInvoke;
import de.red.logic.task.scheduler.RequireTaskInfoAccess;

public abstract class PipeConsumer implements RequireTaskInfoAccess, RequireAsyncTaskInvoke {

  public abstract void yield(AsyncTaskResult taskResult);

}
