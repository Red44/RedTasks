package de.red.logic;

import de.red.logic.task.basic.Task;
import de.red.logic.task.SyncTaskQueueAccess;
import de.red.logic.task.basic.TaskResult;

public class QueueLogicBuilder {


  public abstract static class QueueLogicSync implements Task<Object,Object>, SyncTaskQueueAccess {
    protected QueueLogicSync(){}



    @Override
    public TaskResult<Object> operate(Object input) {
      return null;
    }

  }
}
