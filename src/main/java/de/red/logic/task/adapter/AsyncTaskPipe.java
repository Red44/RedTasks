package de.red.logic.task.adapter;

import de.red.logic.task.async.AsyncTaskResult;
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
