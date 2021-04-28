package de.red.logic.task.adapter;

import de.red.logic.task.async.AsyncTask;
import de.red.logic.task.basic.AsyncTaskResult;
import de.red.logic.task.basic.TaskResult;
import de.red.logic.task.scheduler.RequireAsyncTaskInvoke;
import de.red.logic.task.scheduler.RequireTaskInfoAccess;
import de.red.logic.task.scheduler.TaskInfoAccess;

public abstract class PipeConsumer implements RequireTaskInfoAccess, RequireAsyncTaskInvoke {

  public abstract void yield(AsyncTaskResult taskResult);

}
