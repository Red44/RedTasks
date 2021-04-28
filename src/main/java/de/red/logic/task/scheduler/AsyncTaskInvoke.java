package de.red.logic.task.scheduler;

import com.sun.javafx.tk.Toolkit.Task;
import de.red.logic.task.async.AsyncTask;

public interface AsyncTaskInvoke {

  public void executeTask(AsyncTask task, Object input);

  public void executeTask(AsyncTask task);

}
