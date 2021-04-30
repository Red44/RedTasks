package de.red.test;

public final class SimpleAssertSet {

  private int task = 0;
  private String taskqueueName;


  public SimpleAssertSet(String taskqueueName) {
    this.taskqueueName = taskqueueName;
  }

  public void assertTrue(boolean bool) {
    task++;
    if (bool) {
      successMessage();
    } else {
      failMessage();
    }
  }
  public void assertEquals(Object obj,Object obj1){
    assertTrue(obj.equals(obj1));
  }
  public void assertFalse(boolean bool) {
    assertTrue(!bool);
  }
  public void assertNull(Object obj){
    assertTrue(obj == null);
  }

  private void failMessage() {
    System.out.println(taskqueueName+" - Task : " + task + "        ❌");
  }

  private void successMessage() {
    System.out.println(taskqueueName+ " - Task : " + task + "       ✔️");
  }


}
