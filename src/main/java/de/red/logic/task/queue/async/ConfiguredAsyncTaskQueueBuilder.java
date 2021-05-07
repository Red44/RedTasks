package de.red.logic.task.queue.async;

import de.red.logic.task.adapter.PipeConsumer;
import de.red.logic.task.async.AsyncTask;
import de.red.logic.task.async.AsyncTaskResult;
import de.red.logic.task.queue.TaskQueue;
import de.red.logic.task.scheduler.AsyncTaskInvoke;
import de.red.logic.task.scheduler.RequireExecutor;
import de.red.logic.task.scheduler.TaskInfoAccess;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.function.Predicate;

public class ConfiguredAsyncTaskQueueBuilder implements
    AsyncTaskQueueBuilder, RequireExecutor {

  private final ConfiguredAsyncTaskQueue queueStructure = new ConfiguredAsyncTaskQueue();

  @Override
  public ConfiguredAsyncTaskQueueBuilder addAsyncTask(AsyncTask asyncTask) {
    addTaskToQueue(asyncTask);
    queueStructure.instantExecutions.add(asyncTask);
    return this;
  }

  @Override
  public ConfiguredAsyncTaskQueueBuilder addAsyncCompletionTask(
      AsyncTask<List<AsyncTaskResult>, ?> completionTask,
      String targetGroup) {
    new PipeConsumerBuilder(queueStructure)
        .appendTaskValidator(taskResult -> taskResult.getGroupName().equals(targetGroup))
        .appendLogic((taskResult, taskAccess) -> {
          if (taskAccess.completions >= taskAccess.getGroupSize(targetGroup)) {
            taskAccess.executeTask("result", taskAccess.results);
          }
        })
        .appendTask(completionTask, "result")
        .build();
    return this;
  }

  @Override
  public ConfiguredAsyncTaskQueueBuilder addAsyncSuccessAllTask(
      AsyncTask<List<AsyncTaskResult>, ?> completionTask,
      String targetGroup) {
    new PipeConsumerBuilder(queueStructure)
        .appendTaskValidator(taskResult -> taskResult.getGroupName().equals(targetGroup))
        .appendLogic((taskResult, taskAccess) -> {
          if (taskAccess.completions >= taskAccess.getGroupSize(targetGroup)) {
            if (taskAccess.results.stream().allMatch(res -> taskResult.succeded())) {
              taskAccess.executeTask("result", taskAccess.results);
            }
          }
        })
        .appendTask(completionTask, "result")
        .build();

    return this;
  }

  @Override
  public ConfiguredAsyncTaskQueueBuilder addAsyncSuccessAnyTask(
      AsyncTask<List<AsyncTaskResult>, ?> completionTask,
      String targetGroup) {
    new PipeConsumerBuilder(queueStructure)
        .appendTaskValidator(taskResult -> taskResult.getGroupName().equals(targetGroup))
        .appendLogic((taskResult, taskAccess) -> {
          if (taskAccess.completions >= taskAccess.getGroupSize(targetGroup)) {
            if (taskAccess.results.stream().anyMatch(res -> taskResult.succeded())) {
              taskAccess.executeTask("result", taskAccess.results);
            }
          }
        })
        .appendTask(completionTask, "result")
        .build();
    return this;
  }

  @Override
  public ConfiguredAsyncTaskQueueBuilder addAsyncFailAnyTask(
      AsyncTask<List<AsyncTaskResult>, ?> completionTask,
      String targetGroup) {
    new PipeConsumerBuilder(queueStructure)
        .appendTaskValidator(taskResult -> taskResult.getGroupName().equals(targetGroup))
        .appendLogic((taskResult, taskAccess) -> {
          if (taskAccess.completions >= taskAccess.getGroupSize(targetGroup)) {
            if (taskAccess.results.stream().anyMatch(res -> taskResult.failed())) {
              taskAccess.executeTask("result", taskAccess.results);
            }
          }
        })
        .appendTask(completionTask, "result")
        .build();
    return this;
  }

  @Override
  public ConfiguredAsyncTaskQueueBuilder addAsyncFailAllTask(
      AsyncTask<List<AsyncTaskResult>, ?> completionTask,
      String targetGroup) {
    new PipeConsumerBuilder(queueStructure)
        .appendTaskValidator(taskResult -> taskResult.getGroupName().equals(targetGroup))
        .appendLogic((taskResult, taskAccess) -> {
          if (taskAccess.completions >= taskAccess.getGroupSize(targetGroup)) {
            if (taskAccess.results.stream().allMatch(res -> taskResult.failed())) {
              taskAccess.executeTask("result", taskAccess.results);
            }
          }
        })
        .appendTask(completionTask, "result")
        .build();
    return this;
  }

  @Override
  public ConfiguredAsyncTaskQueueBuilder addAsyncAllCompletionTasks(
      AsyncTask<List<AsyncTaskResult>, ?> success,
      AsyncTask<List<AsyncTaskResult>, ?> fail, String targetGroup) {
    new PipeConsumerBuilder(queueStructure)
        .appendTaskValidator(taskResult -> taskResult.getGroupName().equals(targetGroup))
        .appendLogic((taskResult, taskAccess) -> {
          if (taskAccess.completions >= taskAccess.getGroupSize(targetGroup)) {
            if (taskAccess.results.stream().allMatch(res -> taskResult.succeded())) {
              taskAccess.executeTask("success", taskAccess.results);
            } else {
              taskAccess.executeTask("fail", taskAccess.results);
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
    new PipeConsumerBuilder(queueStructure)
        .appendTaskValidator(taskResult -> taskResult.getGroupName().equals(targetGroup)
            && taskResult.getTaskId() == targetID)
        .appendLogic((taskResult, taskAccess) -> taskAccess.executeTask("completion", taskAccess.results))
        .appendTask(completionTask, "completion")
        .build();

    return this;
  }

  @Override
  public ConfiguredAsyncTaskQueueBuilder addAsyncSuccessTask(AsyncTask completionTask,
      String targetGroup, int targetID) {
    new PipeConsumerBuilder(queueStructure)
        .appendTaskValidator(taskResult -> taskResult.getGroupName().equals(targetGroup)
            && taskResult.getTaskId() == targetID)
        .appendLogic((taskResult, taskAccess) -> {
          if (taskResult.succeded()) {
            taskAccess.executeTask("completion", taskAccess.results);
          }
        })
        .appendTask(completionTask, "completion")
        .build();
    return this;
  }

  @Override
  public ConfiguredAsyncTaskQueueBuilder addAsyncFailTask(AsyncTask completionTask,
      String targetGroup, int targetID) {
    new PipeConsumerBuilder(queueStructure)
        .appendTaskValidator(taskResult -> taskResult.getGroupName().equals(targetGroup)
            && taskResult.getTaskId() == targetID)
        .appendLogic((taskResult, taskAccess) -> {
          if (taskResult.failed()) {
            taskAccess.executeTask("completion", taskAccess.results.get(0));
          }
        })
        .appendTask(completionTask, "completion")
        .build();
    return this;
  }

  @Override
  public AsyncTaskQueueBuilder overrideDefaultExecutor(ExecutorService executorService) {
    queueStructure.executor = executorService;
    return this;
  }

  @Override
  public ConfiguredAsyncTaskQueueBuilder addAsyncAnyCompletionTasks(
      AsyncTask<List<AsyncTaskResult>, ?> success,
      AsyncTask<List<AsyncTaskResult>, ?> fail, String targetGroup) {
    new PipeConsumerBuilder(queueStructure)
        .appendTaskValidator(taskResult -> taskResult.getGroupName().equals(targetGroup))
        .appendLogic((taskResult, taskAccess) -> {
          if (taskAccess.completions >= taskAccess.getGroupSize(targetGroup)) {
            if (taskAccess.results.stream().anyMatch(res -> res.succeded())) {
              taskAccess.executeTask("success", taskAccess.results);
            } else {
              taskAccess.executeTask("fail", taskAccess.results);
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
      AsyncTask<List<AsyncTaskResult>, ?> successTaskAny,
      AsyncTask<List<AsyncTaskResult>, ?> failTaskAny,
      AsyncTask<List<AsyncTaskResult>, ?> failTaskAll,
      AsyncTask<List<AsyncTaskResult>, ?> sucessTaskAll,
      String targetGroup) {
    new PipeConsumerBuilder(queueStructure)
        .appendTaskValidator(taskResult -> taskResult.getGroupName().equals(targetGroup))
        .appendLogic((taskResult, taskAccess) -> {
          if (taskAccess.completions >= taskAccess.getGroupSize(targetGroup)) {
            if (taskAccess.results.stream().anyMatch(res -> res.succeded())) {
              taskAccess.executeTask("success_any", taskAccess.results);
            } else {
              taskAccess.executeTask("fail_any", taskAccess.results);
            }

            if (taskAccess.results.stream().allMatch(res -> res.succeded())) {
              taskAccess.executeTask("success_all", taskAccess.results);
            } else {
              taskAccess.executeTask("fail_all", taskAccess.results);
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
  @SuppressWarnings(value = "Deadlock freedom")
  public ConfiguredAsyncTaskQueue buildSync(int ID, String group) {
    buildAsync();
    queueStructure.finalGroup = Optional.of(group);
    queueStructure.finalID = Optional.of(ID);

    return queueStructure;
  }

  @Override
  public TaskQueue buildAsync() {
    queueStructure.allTasks.forEach(task -> {
      queueStructure.groupSize.putIfAbsent(task.getGroupName(), 0);
      queueStructure.groupSize.compute(task.getGroupName(), (name, integer) -> ++integer);
    });
    return queueStructure;
  }

  private void addTaskToQueue(AsyncTask task) {
    queueStructure.allTasks.add(task);
  }

  @Override
  public ExecutorService requireExecutor() {
    return null;
  }

  protected static class PipeConsumerBuilder extends PipeConsumer {

    protected ConfiguredAsyncTaskQueue prototype;
    protected Optional<String> group = Optional.empty();
    protected Optional<Integer> targetID = Optional.empty();
    protected HashMap<String, AsyncTask> tasks = new HashMap<>();

    protected Optional<AsyncTaskResultRunnable> triggerLogic = Optional.empty();
    protected Optional<Predicate<AsyncTaskResult>> validator = Optional.empty();

    protected int completions = 0;
    protected int fails = 0;
    protected int success = 0;

    protected List<AsyncTaskResult> results = new ArrayList<>();

    public PipeConsumerBuilder(ConfiguredAsyncTaskQueue prototype) {
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
      this.validator = Optional.of(predicate);
      return this;
    }

    public PipeConsumerBuilder appendTask(AsyncTask task, String nick) {
      tasks.put(nick, task);
      prototype.allTasks.add(task);
      return this;
    }

    public PipeConsumerBuilder appendLogic(AsyncTaskResultRunnable onValidTask) {
      this.triggerLogic = Optional.of(onValidTask);
      return this;
    }


    public void build() {
      prototype.asyncTaskPipe.addAdapter(this);
    }

    @Override
    public void yield(AsyncTaskResult taskResult) {
      validator.ifPresent(validator -> {
        if (validator.test(taskResult)) {
          results.add(taskResult);
          completions++;
          if (taskResult.succeded()) {
            success++;
          } else {
            fails++;
          }
          triggerLogic.ifPresent(trigger -> trigger.run(taskResult, this));
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

  protected interface AsyncTaskResultRunnable {

    public void run(AsyncTaskResult taskResult, PipeConsumerBuilder taskAccess);
  }

  protected enum TaskNick{
    COMPLETION,SUCCESS,FAIL,FAIL_ANY,SUCCESS_ANY,FAIL_MANY,SUCCESS_MANY;
  }

}
