package de.red.logic.task.scheduler;

import de.red.logic.task.basic.AsyncTaskResult;
import de.red.logic.task.scheduler.ConfiguredAsyncTaskQueueBuilder.PipeConsumerBuilder;

public interface AsyncTaskResultRunnable {

  public void run(AsyncTaskResult taskResult, PipeConsumerBuilder builder);

}
