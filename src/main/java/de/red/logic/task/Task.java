package de.red.logic.task;

public interface Task<I, O> {

  public TaskResult<O> operate(I input);

  default TaskResult<O> resultFailed() {
    return result(null, false);
  }

  default TaskResult<O> resultSuccess(O output) {
    return result(output, true);
  }
  default TaskResult<O> result(O output, boolean succeeded) {
    return new TaskResult<O>() {
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
