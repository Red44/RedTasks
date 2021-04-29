package de.red.logic.task.queue.async;

import de.red.logic.task.adapter.AsyncTaskPipe;
import de.red.logic.task.adapter.PipeConsumer;
import de.red.logic.task.async.AsyncTask;
import de.red.logic.task.async.AsyncTaskResult;
import de.red.logic.task.basic.Task;
import de.red.logic.task.basic.TaskResult;
import de.red.logic.task.queue.TaskQueue;
import de.red.logic.task.scheduler.AsyncTaskInvoke;
import de.red.logic.task.scheduler.TaskInfoAccess;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class ConfiguredAsyncTaskQueue implements Task,
    AsyncTaskInvoke, TaskInfoAccess , TaskQueue {

  protected ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

  protected AsyncTaskPipe asyncTaskPipe = new AsyncTaskPipe();

  protected List<AsyncTask<?, ?>> allTasks = new ArrayList<>();

  protected HashMap<String, Integer> groupSize = new HashMap<>();

  protected List<AsyncTask> instantExecutions = new ArrayList<>();

  protected Optional<Integer> finalID = Optional.empty();
  protected Optional<String> finalGroup = Optional.empty();

  protected ConfiguredAsyncTaskQueue() {}

  @Override
  public TaskResult<Object> operate(Object input) {
    instantExecutions.forEach(tasks -> executeTask(tasks, input));
    if(finalGroup.isPresent() && finalID.isPresent()) {
      try {
        return executor.submit(() -> {
          Thread current = Thread.currentThread();
          final AtomicReference<AsyncTaskResult<Object>> result = new AtomicReference<>();
          asyncTaskPipe.addAdapter(new PipeConsumer() {
            @Override
            public void yield(AsyncTaskResult taskResult) {
              if (taskResult.getTaskId() == finalID.get() && taskResult.getGroupName()
                  .equals(finalGroup.get())) {
                result.set(taskResult);
                current.resume();
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

    }else {
      return new TaskResult<Object>() {
        @Override
        public boolean succeded() {
          return true;
        }

        @Override
        public Object getOutput() {
          return null;
        }
      };
    }
  }

  @Override
  public void executeTask(AsyncTask task, Object input) {
    executor.submit(()->
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

  @Override
  public TaskResult startQueue(Object input) {
    return operate(input);
  }

  @Override
  public TaskResult startQueue() {
    return operate(null);
  }

}
