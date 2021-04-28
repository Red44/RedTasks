package de.red.logic.task.async;

import java.util.concurrent.atomic.AtomicLong;

public interface PID {

  AtomicLong pidCount = new AtomicLong();

  default int getID(){
    return this.hashCode();
  }

}
