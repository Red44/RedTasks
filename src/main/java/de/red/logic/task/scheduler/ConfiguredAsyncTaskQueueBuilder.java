package de.red.logic.task.scheduler;

import de.red.logic.task.AsyncTaskQueueBuilder;
import de.red.logic.task.adapter.PipeConsumer;
import de.red.logic.task.async.AsyncTask;
import de.red.logic.task.async.preconfigured.AsyncInputVoidTask;
import de.red.logic.task.basic.AsyncTaskResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.function.Predicate;

public abstract class ConfiguredAsyncTaskQueueBuilder implements
    AsyncTaskQueueBuilder<ConfiguredAsyncTaskQueueBuilder>, RequireExecutor {

  private final ConfiguredAsyncTaskQueueBuilder instance = this;

  private AsyncTaskQueue prototype = new AsyncTaskQueue() {
    @Override
    public ExecutorService requireExecutor() {
      return instance.requireExecutor();
    }
  };

  @Override
  public ConfiguredAsyncTaskQueueBuilder addAsyncTask(AsyncTask asyncTask) {
    addTaskToQueue(asyncTask);
    prototype.instantExecutions.add(asyncTask);
    return this;
  }

  @Override
  public ConfiguredAsyncTaskQueueBuilder addAsyncCompletionTask(AsyncTask<List<AsyncTaskResult>,?> completionTask,
      String targetGroup) {
    new PipeConsumerBuilder(prototype)
        .appendTaskValidator(taskResult -> taskResult.getGroupName().equals(targetGroup))
        .appendLogic((taskResult, builder) -> {
          if (builder.completions >= builder.getGroupSize(targetGroup)) {
            builder.executeTask("result",builder.results);
          }
        })
        .appendTask(completionTask, "result")
        .build();
    return this;
  }

  @Override
  public ConfiguredAsyncTaskQueueBuilder addAsyncSuccessAllTask(AsyncTask<List<AsyncTaskResult>,?> completionTask,
      String targetGroup) {
    new PipeConsumerBuilder(prototype)
        .appendTaskValidator(taskResult -> taskResult.getGroupName().equals(targetGroup))
        .appendLogic((taskResult, builder) -> {
          if (builder.completions >= builder.getGroupSize(targetGroup)) {
            if (builder.results.stream().allMatch(res -> taskResult.succeded())) {
              builder.executeTask("result",builder.results);
            }
          }
        })
        .appendTask(completionTask, "result")
        .build();

    return this;
  }

  @Override
  public ConfiguredAsyncTaskQueueBuilder addAsyncSuccessAnyTask(AsyncTask<List<AsyncTaskResult>,?> completionTask,
      String targetGroup) {
    new PipeConsumerBuilder(prototype)
        .appendTaskValidator(taskResult -> taskResult.getGroupName().equals(targetGroup))
        .appendLogic((taskResult, builder) -> {
          if (builder.completions >= builder.getGroupSize(targetGroup)) {
            if (builder.results.stream().anyMatch(res -> taskResult.succeded())) {
              builder.executeTask("result",builder.results);
            }
          }
        })
        .appendTask(completionTask, "result")
        .build();
    return this;
  }

  @Override
  public ConfiguredAsyncTaskQueueBuilder addAsyncFailAnyTask(AsyncTask<List<AsyncTaskResult>,?> completionTask,
      String targetGroup) {
    new PipeConsumerBuilder(prototype)
        .appendTaskValidator(taskResult -> taskResult.getGroupName().equals(targetGroup))
        .appendLogic((taskResult, builder) -> {
          if (builder.completions >= builder.getGroupSize(targetGroup)) {
            if (builder.results.stream().anyMatch(res -> taskResult.failed())) {
              builder.executeTask("result",builder.results);
            }
          }
        })
        .appendTask(completionTask, "result")
        .build();
    return this;
  }

  @Override
  public ConfiguredAsyncTaskQueueBuilder addAsyncFailAllTask(AsyncTask<List<AsyncTaskResult>,?> completionTask,
      String targetGroup) {
    new PipeConsumerBuilder(prototype)
        .appendTaskValidator(taskResult -> taskResult.getGroupName().equals(targetGroup))
        .appendLogic((taskResult, builder) -> {
          if (builder.completions >= builder.getGroupSize(targetGroup)) {
            if (builder.results.stream().allMatch(res -> taskResult.failed())) {
              builder.executeTask("result",builder.results);
            }
          }
        })
        .appendTask(completionTask, "result")
        .build();
    return this;
  }

  @Override
  public ConfiguredAsyncTaskQueueBuilder addAsyncAllCompletionTasks(AsyncTask<List<AsyncTaskResult>,?> success,
      AsyncTask<List<AsyncTaskResult>,?> fail, String targetGroup) {
    new PipeConsumerBuilder(prototype)
        .appendTaskValidator(taskResult -> taskResult.getGroupName().equals(targetGroup))
        .appendLogic((taskResult, builder) -> {
          if (builder.completions >= builder.getGroupSize(targetGroup)) {
            if (builder.results.stream().allMatch(res -> taskResult.succeded())) {
              builder.executeTask("success",builder.results);
            } else {
              builder.executeTask("fail",builder.results);
            }
          }
        })
        .appendTask(success, "success")
        .appendTask(fail, "fail")
        .build();
    return this;
  }


  @Override
  public ConfiguredAsyncTaskQueueBuilder addAsyncCompletionTask(AsyncTask completionTask,
      String targetGroup, int targetID) {
    new PipeConsumerBuilder(prototype)
        .appendTaskValidator(taskResult -> taskResult.getGroupName().equals(targetGroup)
            && taskResult.getTaskId() == targetID)
        .appendLogic((taskResult, builder) -> builder.executeTask("completion",builder.results))
        .appendTask(completionTask, "completion")
        .build();

    return this;
  }

  @Override
  public ConfiguredAsyncTaskQueueBuilder addAsyncSuccessTask(AsyncTask completionTask,
      String targetGroup, int targetID) {
    new PipeConsumerBuilder(prototype)
        .appendTaskValidator(taskResult -> taskResult.getGroupName().equals(targetGroup)
            && taskResult.getTaskId() == targetID)
        .appendLogic((taskResult, builder) -> {
          if (taskResult.succeded()) {
            builder.executeTask("completion",builder.results);
          }
        })
        .appendTask(completionTask, "completion")
        .build();
    return this;
  }

  @Override
  public ConfiguredAsyncTaskQueueBuilder addAsyncFailTask(AsyncTask completionTask,
      String targetGroup, int targetID) {
    new PipeConsumerBuilder(prototype)
        .appendTaskValidator(taskResult -> taskResult.getGroupName().equals(targetGroup)
            && taskResult.getTaskId() == targetID)
        .appendLogic((taskResult, builder) -> {
          if (taskResult.failed()) {
            builder.executeTask("completion",builder.results.get(0));
          }
        })
        .appendTask(completionTask, "completion")
        .build();
    return this;
  }

  @Override
  public ConfiguredAsyncTaskQueueBuilder addAsyncAnyCompletionTasks(AsyncTask<List<AsyncTaskResult>,?> success,
      AsyncTask<List<AsyncTaskResult>,?> fail, String targetGroup) {
    new PipeConsumerBuilder(prototype)
        .appendTaskValidator(taskResult -> taskResult.getGroupName().equals(targetGroup))
        .appendLogic((taskResult, builder) -> {
          if (builder.completions >= builder.getGroupSize(targetGroup)) {
            if (builder.results.stream().anyMatch(res -> res.succeded())) {
              builder.executeTask("success",builder.results);
            } else {
              builder.executeTask("fail",builder.results);
            }
          }
        })
        .appendTask(success, "success")
        .appendTask(fail, "fail")
        .build();
    return this;
  }

  @Override
  public ConfiguredAsyncTaskQueueBuilder addAsyncCompletionTask(
      AsyncTask<List<AsyncTaskResult>,?> successTaskAny,
      AsyncTask<List<AsyncTaskResult>,?> failTaskAny,
      AsyncTask<List<AsyncTaskResult>,?> failTaskAll,
      AsyncTask<List<AsyncTaskResult>,?> sucessTaskAll,
      String targetGroup) {
    new PipeConsumerBuilder(prototype)
        .appendTaskValidator(taskResult -> taskResult.getGroupName().equals(targetGroup))
        .appendLogic((taskResult, builder) -> {
          if (builder.completions >= builder.getGroupSize(targetGroup)) {
            if (builder.results.stream().anyMatch(res -> res.succeded())) {
              builder.executeTask("success_any",builder.results);
            } else {
              builder.executeTask("fail_any",builder.results);
            }

            if (builder.results.stream().allMatch(res -> res.succeded())) {
              builder.executeTask("success_all",builder.results);
            } else {
              builder.executeTask("fail_all",builder.results);
            }
          }

        })
        .appendTask(successTaskAny, "success_any")
        .appendTask(failTaskAny, "fail_any")
        .appendTask(sucessTaskAll, "success_all")
        .appendTask(failTaskAll, "fail_all")
        .build();
    return this;
  }

  @Override
  /**
   * completion task must be set
   */
  public AsyncTaskQueue build(int ID, String group) {
    prototype.allTasks.forEach(task -> {
      prototype.groupSize.putIfAbsent(task.getGroupName(), 0);
      prototype.groupSize.compute(task.getGroupName(), (name, integer) -> integer++);
    });
    prototype.finalGroup = group;
    prototype.finalID = ID;

    return prototype;
  }

  private void addTaskToQueue(AsyncTask task) {
    prototype.allTasks.add(task);
  }

  protected static class PipeConsumerBuilder extends PipeConsumer {

    protected AsyncTaskQueue prototype;
    protected Optional<String> group = Optional.empty();
    protected Optional<Integer> targetID = Optional.empty();
    protected HashMap<String, AsyncTask> tasks = new HashMap<>();

    protected Optional<AsyncTaskResultRunnable> trigger = Optional.empty();
    protected Optional<Predicate<AsyncTaskResult>> predicate = Optional.empty();

    protected int completions = 0;
    protected int fails = 0;
    protected int success = 0;

    protected List<AsyncTaskResult> results = new ArrayList<>();

    public PipeConsumerBuilder(AsyncTaskQueue prototype) {
      this.prototype = prototype;
    }

    @Override
    public AsyncTaskInvoke requestAsyncTaskInvoke() {
      return prototype;
    }

    @Override
    public TaskInfoAccess requireTaskInfoAccess() {
      return prototype;
    }

    public PipeConsumerBuilder appendGroup(String group) {
      this.group = Optional.of(group);
      return this;
    }

    public PipeConsumerBuilder appendID(int ID) {
      this.targetID = Optional.of(ID);
      return this;
    }

    public PipeConsumerBuilder appendTaskValidator(Predicate<AsyncTaskResult> predicate) {
      this.predicate = Optional.of(predicate);
      return this;
    }

    public PipeConsumerBuilder appendTask(AsyncTask task, String nick) {
      tasks.put(nick, task);
      prototype.allTasks.add(task);
      return this;
    }

    public PipeConsumerBuilder appendLogic(AsyncTaskResultRunnable onValidTask) {
      this.trigger = Optional.of(onValidTask);
      return this;
    }


    public void build() {
      prototype.asyncTaskPipe.addAdapter(this);
    }

    @Override
    public void yield(AsyncTaskResult taskResult) {
      predicate.ifPresent(validator -> {
        if (validator.test(taskResult)) {
          results.add(taskResult);
          completions++;
          if (taskResult.succeded()) {
            success++;
          } else {
            fails++;
          }
          trigger.ifPresent(trigger -> trigger.run(taskResult, this));
        }
      });
    }

    public void executeTask(String nick, Object input) {
      requestAsyncTaskInvoke().executeTask(tasks.get(nick), input);
    }

    public void executeTask(String nick) {
      executeTask(nick, null);
    }

    public int getGroupSize(String group) {
      return requireTaskInfoAccess().getGroupSizes().get(group);
    }

  }

}
