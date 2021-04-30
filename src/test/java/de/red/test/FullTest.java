package de.red.test;

import de.red.logic.task.RedTasks;
import de.red.logic.task.basic.Task;
import de.red.logic.task.basic.TaskResult;

public class FullTest {

  public static void main(String[] args) {
    System.out.println(RedTasks.newSyncTaskQueueBuilder().setStartTask(new Task() {
      @Override
      public TaskResult operate(Object input) {
        return resultSuccess(15);
      }
    }).addCompletionTask(new Task() {
      @Override
      public TaskResult operate(Object input) {
        return resultFailed(((Integer) input) + 5);
      }
    }).gotoSuccess().addFailTask(new Task() {
      @Override
      public TaskResult operate(Object input) {
        return resultSuccess(((Integer) input) + 100);
      }
    }).gotoFail().addSuccessTask(new Task() {
      @Override
      public TaskResult operate(Object input) {
        return resultSuccess(input);
      }
    }).build().startQueue().getOutput());
  }

}
