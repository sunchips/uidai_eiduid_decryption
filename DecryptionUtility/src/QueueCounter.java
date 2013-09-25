import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class QueueCounter extends Thread {
  private static final Logger LOG = LogManager.getLogger(QueueCounter.class);

  boolean started = true;

  public void stopThread() {
    started = false;
  }

  public void run() {
    while (started) {
      try {
        if (Main.count != null && Main.tpe != null && Main.workQueue != null && Main.inputDirectory != null)
          LOG.info("Current Queue: " + Main.count.get() + " Current Pool: " + Main.tpe.getPoolSize() + " Total Size: " + Main.workQueue.size() + ". Current Directory: " + Main.inputDirectory);
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        e.printStackTrace();
        LOG.error(ExceptionUtils.getStackTrace(e));
      }
    }
  }

}
