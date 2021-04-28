package de.red.logic.task.async;

public interface ProcessorGroup {

  default String getGroupName(){
    return this.getClass().getSimpleName();
  }

}
