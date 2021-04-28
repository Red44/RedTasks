import de.red.logic.task.AsyncTaskQueueBuilder;
import de.red.logic.task.async.AsyncTask;
import de.red.logic.task.async.preconfigured.AsyncInputVoidTask;
import de.red.logic.task.basic.AsyncTaskResult;
import de.red.logic.task.basic.TaskResult;
import de.red.logic.task.scheduler.AsyncTaskQueue;
import de.red.logic.task.scheduler.ConfiguredAsyncTaskQueueBuilder;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.checkerframework.checker.units.qual.A;

public class Test {

  public static void main(String[] args) {
    ExecutorService executor = Executors.newFixedThreadPool(255);
    AsyncTaskQueueBuilder builder = new ConfiguredAsyncTaskQueueBuilder() {
      @Override
      public ExecutorService requireExecutor() {
        return executor;
      }
    };
    System.out.println(builder.addAsyncTask(new NumberInstanc())
        .addAsyncCompletionTask(new NummberAdder(), "number").build(1, "adder").operate(null)
        .getOutput());


  }
}
class NumberInstanc implements AsyncInputVoidTask<Integer>{
  @Override
  public TaskResult<Integer> operate(Void input) {
    System.out.println("number get");
    return resultSuccess(15);
  }

  @Override
  public int getID() {

    return 1;
  }

  @Override
  public String getGroupName() {
    return "number";
  }
}
class NummberAdder implements AsyncTask<List<AsyncTaskResult<Integer>>,Integer>{


  @Override
  public String getGroupName() {
    return "adder";
  }

  @Override
  public int getID() {
    return 1;
  }

  @Override
  public TaskResult<Integer> operate(List<AsyncTaskResult<Integer>> input) {
    System.out.println("number add");
    AtomicInteger integer = new AtomicInteger();
    input.forEach(result -> integer.addAndGet(result.getOutput()));
    return resultSuccess(integer.incrementAndGet());
  }
}


