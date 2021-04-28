package de.red.logic.task.scheduler;

import de.red.logic.task.adapter.AsyncTaskPipe;
import de.red.logic.task.adapter.PipeConsumer;
import de.red.logic.task.async.AsyncTask;
import de.red.logic.task.basic.AsyncTaskResult;
import de.red.logic.task.basic.InputVoidTask;
import de.red.logic.task.basic.TaskResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AsyncTaskQueue implements InputVoidTask<Object>,RequireExecutor,AsyncTaskInvoke,TaskInfoAccess{

  protected AsyncTaskPipe asyncTaskPipe = new AsyncTaskPipe();

  protected List<AsyncTask<?, ?>> allTasks = new ArrayList<>();

  protected HashMap<String, Integer> groupSize = new HashMap<>();

  protected List<AsyncTask> instantExecutions = new ArrayList<>();

  protected int finalID;
  protected String finalGroup;

  protected AsyncTaskQueue() {}

  @Override
  public TaskResult<Object> operate(Void input) {
    instantExecutions.forEach(tasks -> executeTask(tasks, input));
    try {
      return requireExecutor().submit(() -> {
        Thread current = Thread.currentThread();
        final AtomicReference<AsyncTaskResult<Object>> result = new AtomicReference<>();
        asyncTaskPipe.addAdapter(new PipeConsumer() {
          @Override
          public void yield(AsyncTaskResult taskResult) {
            if (taskResult.getTaskId() == finalID && taskResult.getGroupName()
                .equals(finalGroup)) {
              current.resume();
              result.set(taskResult);
            }
          }

          @Override
          public TaskInfoAccess requireTaskInfoAccess() {
            return null;
          }

          @Override
          public AsyncTaskInvoke requestAsyncTaskInvoke() {
            return null;
          }

        });
        current.suspend();
        return result.get();
      }).get();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
    return new TaskResult<Object>() {
      @Override
      public boolean succeded() {
        return false;
      }

      @Override
      public Object getOutput() {
        return null;
      }
    };
  }

  @Override
  public void executeTask(AsyncTask task, Object input) {
    requireExecutor().submit(()->
        asyncTaskPipe.pipe(
            (AsyncTaskResult) task.operate(input)
        )
    );
  }
  @Override
  public void executeTask(AsyncTask task) {
   executeTask(task,null);
  }

  @Override
  public HashMap<String, Integer> getGroupSizes() {
    return groupSize;
  }
}
