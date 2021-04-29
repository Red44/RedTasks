import de.red.logic.task.queue.async.AsyncTaskQueueBuilder;
import de.red.logic.task.async.AsyncTaskResult;
import de.red.logic.task.basic.Task;
import de.red.logic.task.basic.TaskResult;
import de.red.logic.task.queue.async.ConfiguredAsyncTaskQueueBuilder;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import static de.red.logic.task.async.AsyncWrapper.*;

public class Test {

  public static void main(String[] args) {
    ExecutorService executor = Executors.newFixedThreadPool(255);
    AsyncTaskQueueBuilder builder = new ConfiguredAsyncTaskQueueBuilder();
    System.out.println(
        builder.addAsyncTask(wrap(new NumberInstanc(),"number",1))
        .addAsyncCompletionTask(wrap(new NummberAdder(),"adder",1), "number").
         buildSync(1, "adder").operate(null)
        .getOutput());


  }
}
class NumberInstanc implements Task<Void,Integer> {
  @Override
  public TaskResult<Integer> operate(Void input) {
    System.out.println("number get");
    return resultSuccess(15);
  }
}
class NummberAdder implements Task<List<AsyncTaskResult<Integer>>,Integer>{

  @Override
  public TaskResult<Integer> operate(List<AsyncTaskResult<Integer>> input) {
    System.out.println("number add");
    AtomicInteger integer = new AtomicInteger();
    input.forEach(result -> integer.addAndGet(result.getOutput()));
    return resultSuccess(integer.incrementAndGet());
  }
}


