
package scheduling;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicLong;

import scheduling.TiredThread;

import static org.junit.jupiter.api.Assertions.*;

public class TiredThreadTest {
    TiredThread worker;
    @BeforeEach
    //before each test : create a new tired thread with random fatigue factor and start it
    public void setUp() {
        double fatigueFactor = 0.5 + Math.random();
        worker = new TiredThread(1, fatigueFactor);
        worker.start();
    }

    @AfterEach
    //after each test: shutdown the thread to avoid "zombie" threads in the system
    public void tearDown() {
        
        if (worker != null && worker.isAlive()) {
            worker.shutdown();
            try {
                // give it half a second to gracefully shutdown before moving to the next test
                Thread.sleep(500); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    @Test
    public void testInitialState() {
        assertEquals(0, worker.getTimeUsed(), "Time used should start at 0");
        assertFalse(worker.isBusy(), "Worker should not be busy initially");
        assertEquals(1, worker.getWorkerId());
    }
   // test that after assigning a task, the time used and fatigue are updated correctly
   @Test
    public void testTaskExecutionUpdatesFatigue() throws InterruptedException {
        // Assign a simple task that sleeps for 20 milliseconds
        worker.newTask(() -> {
            try {
                Thread.sleep(20); 
            } catch (InterruptedException e) {
                // ignore
            }
        });
        Thread.sleep(100);
        assertTrue(worker.getTimeUsed() > 0, "Time used should update after task");
        assertTrue(worker.getFatigue() > 0, "Fatigue should verify work done");
        assertFalse(worker.isBusy(), "Worker should be free after task finishes");
    }
    // test that a task is rejected when the worker is busy
    @Test
    public void testRejectTaskWhenBusy() throws InterruptedException {
        worker.newTask(() -> {
            try {
                Thread.sleep(200); 
            } catch (InterruptedException e) {
                // ignore
            }
        });

        assertThrows(IllegalStateException.class, () -> {
            worker.newTask(() -> {});
        }, "Should throw exception because worker is busy with the first task");
        Thread.sleep(300);
    }
  
    // test that shutdown properly stops the thread
    @Test
    public void testShutdown() throws InterruptedException {
        assertTrue(worker.isAlive());   
        worker.shutdown();
        Thread.sleep(1000);
        
        assertFalse(worker.isAlive(), "Thread should be dead after shutdown");
    }
    // test that compareTo reflects fatigue levels correctly
    @Test
    public void testCompareTo() throws InterruptedException {
        TiredThread worker1 = new TiredThread(1, 1.0); 
        TiredThread worker2 = new TiredThread(2, 2.0); 
        
        worker1.start();
        worker2.start();

        Runnable task = () -> {
            try { Thread.sleep(50); } catch (Exception e) {}
        };

        worker1.newTask(task);
        worker2.newTask(task);

        Thread.sleep(200);
        assertTrue(worker2.getFatigue() > worker1.getFatigue(), 
                "Worker 2 should be more tired");
        

        assertTrue(worker2.compareTo(worker1) > 0);

        worker1.shutdown();
        worker2.shutdown();
        Thread.sleep(100);
    }
         
}