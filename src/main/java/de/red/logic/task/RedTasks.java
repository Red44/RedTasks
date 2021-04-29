package de.red.logic.task;

import de.red.logic.task.queue.async.AsyncTaskQueueBuilder;
import de.red.logic.task.queue.async.ConfiguredAsyncTaskQueueBuilder;
import de.red.logic.task.queue.sync.ConfiguredSyncTaskQueueBuilder;
import de.red.logic.task.queue.sync.SyncTaskQueueBuilder;

public final class RedTasks {

  private RedTasks(){

  }

  public static AsyncTaskQueueBuilder newAsyncTaskQueueBuilder(){
    return new ConfiguredAsyncTaskQueueBuilder();
  }

  public static SyncTaskQueueBuilder newSyncTaskQueueBuilder(){
    return new ConfiguredSyncTaskQueueBuilder();
  }

}
