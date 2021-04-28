package de.red.logic.task.async;

import de.red.logic.task.basic.AsyncTaskResult;
import de.red.logic.task.basic.Task;
import de.red.logic.task.basic.TaskResult;

public interface AsyncTask<I, O> extends Task<I, O>, ProcessorGroup ,PID{

  default TaskResult<O> result(O output, boolean succeeded) {
    final AsyncTask instance = this;

    return new AsyncTaskResult<O>() {
      @Override
      public String getGroupName() {
        return instance.getGroupName();
      }

      @Override
      public int getTaskId() {
        return instance.getID();
      }

      @Override
      public boolean succeded() {
        return succeeded;
      }

      @Override
      public O getOutput() {
        return output;
      }
    };
  }

}
