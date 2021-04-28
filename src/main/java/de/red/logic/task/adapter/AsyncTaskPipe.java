package de.red.logic.task.adapter;

import de.red.logic.task.async.AsyncTask;
import de.red.logic.task.basic.AsyncTaskResult;
import de.red.logic.task.basic.Task;
import de.red.logic.task.basic.TaskResult;
import java.util.ArrayList;
import java.util.List;

public class AsyncTaskPipe {

  List<PipeConsumer> consumers = new ArrayList<>();

  public void pipe(AsyncTaskResult taskResult) {
   consumers.forEach(consumer -> consumer.yield(taskResult));
  }

  public void addAdapter(PipeConsumer consumer) {
    this.consumers.add(consumer);
  }

}
