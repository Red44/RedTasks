package de.red.logic.task;

import de.red.logic.task.async.AsyncTask;
import de.red.logic.task.async.preconfigured.AsyncInputVoidTask;
import de.red.logic.task.basic.AsyncTaskResult;
import de.red.logic.task.scheduler.AsyncTaskQueue;
import java.util.List;

/**
 * accepts any form of async tasks
 **/
public interface AsyncTaskQueueBuilder<Builder> {

  public AsyncTaskQueueBuilder addAsyncTask(AsyncTask asyncTask);

  public AsyncTaskQueueBuilder addAsyncCompletionTask(AsyncTask<List<AsyncTaskResult>, ?> completionTask,
      String targetGroup);

  public AsyncTaskQueueBuilder addAsyncSuccessAllTask(AsyncTask<List<AsyncTaskResult>, ?> completionTask,
      String targetGroup);

  public AsyncTaskQueueBuilder addAsyncSuccessAnyTask(AsyncTask<List<AsyncTaskResult>, ?> completionTask,
      String targetGroup);

  public AsyncTaskQueueBuilder addAsyncFailAnyTask(AsyncTask<List<AsyncTaskResult>, ?> completionTask,
      String targetGroup);

  public AsyncTaskQueueBuilder addAsyncFailAllTask(AsyncTask<List<AsyncTaskResult>, ?> completionTask,
      String targetGroup);


  public AsyncTaskQueueBuilder addAsyncAllCompletionTasks(AsyncTask<List<AsyncTaskResult>, ?> success,
      AsyncTask<List<AsyncTaskResult>,?> fail, String targetGroup);

  public AsyncTaskQueueBuilder addAsyncAnyCompletionTasks(AsyncTask<List<AsyncTaskResult>, ?> success,
      AsyncTask<List<AsyncTaskResult>,?> fail, String targetGroup);

  public AsyncTaskQueueBuilder addAsyncCompletionTask(
      AsyncTask<List<AsyncTaskResult>, ?> successTaskAny,
      AsyncTask<List<AsyncTaskResult>, ?> failTaskAny,
      AsyncTask<List<AsyncTaskResult>, ?> failTaskAll,
      AsyncTask<List<AsyncTaskResult>, ?> sucessTaskAll,
      String targetGroup);

  public AsyncTaskQueueBuilder addAsyncCompletionTask(AsyncTask<List<AsyncTaskResult>, ?> completionTask,
      String targetGroup, int targetID);

  public AsyncTaskQueueBuilder addAsyncSuccessTask(AsyncTask completionTask,
      String targetGroup, int targetID);

  public AsyncTaskQueueBuilder addAsyncFailTask(AsyncTask completionTask,
      String targetGroup, int targetID);

  public AsyncTaskQueue build(int ID, String group);
}
