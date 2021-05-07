package de.red.test;

import de.red.logic.task.RedTasks;
import de.red.logic.task.async.AsyncTaskResult;
import de.red.logic.task.async.AsyncWrapper;
import de.red.logic.task.basic.Task;
import de.red.logic.task.basic.TaskResult;
import de.red.logic.task.preconfigured.wrappertask.InputVoidWrapperTask;
import java.util.List;
import java.util.Random;

public class FullTest {

  public static void main(String[] args) {
    SimpleAssertSet sync = new SimpleAssertSet("SyncTask Queue Test ");
    for (int i = 0; i < 5; i++) {
      int ran1 = new Random().nextInt(10000);
      int ran2 = new Random().nextInt(10000);
      int ran3 = new Random().nextInt(10000);
      sync.assertEquals(RedTasks.newSyncTaskQueueBuilder().setStartTask(new Task() {
        @Override
        public TaskResult operate(Object input) {
          return resultSuccess(ran1);
        }
      }).addCompletionTask(new Task() {
        @Override
        public TaskResult operate(Object input) {
          return resultFailed(((Integer) input) + ran2);
        }
      }).gotoSuccess().addFailTask(new Task() {
        @Override
        public TaskResult operate(Object input) {
          return resultSuccess(((Integer) input) + ran3);
        }
      }).gotoFail().addSuccessTask(new Task() {
        @Override
        public TaskResult operate(Object input) {
          return resultSuccess(input);
        }
      }).build().startQueue().getOutput(), ran1 + ran2 + ran3);
    }
    System.out.println("\n");
    SimpleAssertSet async = new SimpleAssertSet("AsyncTask Queue Test ");
    for (int i = 0; i < 5; i++) {
      async.assertEquals(RedTasks.newAsyncTaskQueueBuilder().addAsyncTask(AsyncWrapper.wrap(
          new Task() {
            @Override
            public TaskResult operate(Object input) {
              return resultSuccess("true");
            }
          }, "test", 1)
      ).buildSync(1, "test").startQueue().getOutput(), "true");
    }

    for (int i = 0; i < 5; i++) {
      RedTasks.newAsyncTaskQueueBuilder().addAsyncTask(AsyncWrapper.wrap(
          new Task() {
            @Override
            public TaskResult operate(Object input) {
              return resultSuccess("true");
            }
          }, "test", 1)
      ).addAsyncSuccessAnyTask(AsyncWrapper.wrap(new Task() {
        @Override
        public TaskResult operate(Object input) {
          List<AsyncTaskResult> result = (List<AsyncTaskResult>) input;
          async.assertEquals(result.get(0).getOutput(), "true");
          return resultSuccess(null);
        }
      }, "complete", 1), "test")
          .buildAsync().startQueue();

    }

  }
}
