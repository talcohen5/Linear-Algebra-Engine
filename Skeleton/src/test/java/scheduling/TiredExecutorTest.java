package scheduling;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


public class TiredExecutorTest {

    @Test
    void testAllTasksExecuted() {
        TiredExecutor exec = new TiredExecutor(3);
        AtomicInteger counter = new AtomicInteger(0);

        for (int i = 0; i < 100; i++) {
            exec.submit(counter::incrementAndGet);
        }

        try {
            exec.shutdown();
        } catch (InterruptedException e) {
            fail();
        }

        assertEquals(100, counter.get());
    }

    @Test
    void testNoDeadlockOnShutdown() {
        TiredExecutor exec = new TiredExecutor(2);
        exec.submit(() -> {});
        assertDoesNotThrow(() -> exec.shutdown());
    }

    @Test
    void testWorkerReportNotEmpty() {
        TiredExecutor exec = new TiredExecutor(1);
        exec.submit(() -> {});
        try { exec.shutdown(); } catch (InterruptedException ignored) {}
        assertFalse(exec.getWorkerReport().isEmpty());
    }

   @Test
    void testSubmitAllBlocksUntilFinished() {
        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            TiredExecutor exec = new TiredExecutor(4);
            AtomicInteger counter = new AtomicInteger(0);
            List<Runnable> tasks = new ArrayList<>();

            for (int i = 0; i < 50; i++) {
                tasks.add(() -> {
                    try {
                        Thread.sleep(10); 
                    } catch (InterruptedException e) {}
                    counter.incrementAndGet();
                });
            }

            try {
                exec.submitAll(tasks);
            } catch (IllegalArgumentException e) {
                fail("submitAll threw IllegalArgumentException unexpectedly!");}
           
            assertEquals(50, counter.get(), "submitAll returned before all tasks were finished!");
            
            exec.shutdown();
        });
    }

    @Test
    void testInvalidArguments() {
    // test that constructor throws exception for invalid numThreads
        assertThrows(IllegalArgumentException.class, () -> new TiredExecutor(0));
        assertThrows(IllegalArgumentException.class, () -> new TiredExecutor(-5));
    // test that submit and submitAll throw exception for null tasks
        TiredExecutor exec = new TiredExecutor(1);
        assertThrows(IllegalArgumentException.class, () -> exec.submit(null));
        assertThrows(IllegalArgumentException.class, () -> exec.submitAll(null));
    }

    @Test
    void testStressCheck() {
         assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
             int threads = 10;
             int tasks = 1000;
             TiredExecutor exec = new TiredExecutor(threads);
             AtomicInteger counter = new AtomicInteger(0);
             
             for(int i=0; i<tasks; i++) {
                 exec.submit(counter::incrementAndGet);
             }
             
             try {
                 exec.shutdown();
             } catch (InterruptedException e) {
                 fail();
             }
             
             assertEquals(tasks, counter.get(), "Stress test failed - some tasks were lost");
         });
    }

    @Test
    void testShutdownWaitsForTasks() {
        TiredExecutor exec = new TiredExecutor(2);
        AtomicInteger counter = new AtomicInteger(0);

        for (int i = 0; i < 20; i++) {
            exec.submit(() -> {
                try {
                    Thread.sleep(50); 
                } catch (InterruptedException e) {}
                counter.incrementAndGet();
            });
        }

        try {
            exec.shutdown();
        } catch (InterruptedException e) {
            fail();
        }

        assertEquals(20, counter.get(), "Not all tasks completed before shutdown");
    }

        
    
    @Test
    public void testSubmitAllBlocksUntilCompletion() {
        TiredExecutor exec= new TiredExecutor(4);
        int numTasks = 100;
        AtomicInteger counter = new AtomicInteger(0);
        
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < numTasks; i++) {
            tasks.add(() -> {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {}
                counter.incrementAndGet();
            });
        }
        exec.submitAll(tasks);
        assertEquals(numTasks, counter.get());
    }

  
 

}