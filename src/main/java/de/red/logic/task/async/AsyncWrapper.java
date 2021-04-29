package de.red.logic.task.async;

import de.red.logic.task.async.AsyncTask;
import de.red.logic.task.basic.Task;
import de.red.logic.task.basic.TaskResult;

public final class AsyncWrapper {

  private AsyncWrapper() {
  }

  public static AsyncTask wrap(Task task, String group, int ID) {
    return new AsyncTask() {
      @Override
      public TaskResult operate(Object input) {
        TaskResult res = task.operate(input);
        return result(res.getOutput(), res.succeded());
      }

      @Override
      public int getID() {
        return ID;
      }

      @Override
      public String getGroupName() {
        return group;
      }
    };
  }

  public static AsyncTask wrap(Task task, int ID) {
    return new AsyncTask() {
      @Override
      public TaskResult operate(Object input) {
        TaskResult res = task.operate(input);
        return result(res.getOutput(), res.succeded());
      }

      @Override
      public int getID() {
        return ID;
      }
    };
  }

  public static AsyncTask wrap(Task task, String group) {
    return new AsyncTask() {
      @Override
      public TaskResult operate(Object input) {
        TaskResult res = task.operate(input);
        return result(res.getOutput(), res.succeded());
      }

      @Override
      public String getGroupName() {
        return group;
      }
    };
  }

  public static AsyncTask wrap(Task task) {
    return new AsyncTask() {
      @Override
      public TaskResult operate(Object input) {
        TaskResult res = task.operate(input);
        return result(res.getOutput(), res.succeded());
      }
    };
  }

  public static AsyncTask overrideIdentifiers(AsyncTask task, String group, int id) {
    return wrap(task, group, id);
  }

  public static AsyncTask overrideIdentifiers(AsyncTask task, String group) {
    return wrap(task, group, task.getID());
  }

  public static AsyncTask overrideIdentifiers(AsyncTask task, int ID) {
    return wrap(task, task.getGroupName(), ID);
  }

}
