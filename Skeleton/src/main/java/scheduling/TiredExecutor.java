package scheduling;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class TiredExecutor {

    private final TiredThread[] workers;
    private final PriorityBlockingQueue<TiredThread> idleMinHeap = new PriorityBlockingQueue<>();
    private final AtomicInteger inFlight = new AtomicInteger(0);

    public TiredExecutor(int numThreads) {
      if (numThreads <= 0) {
            throw new IllegalArgumentException("Number of threads must be positive.");
        }
        
        workers = new TiredThread[numThreads];
        
        for (int i = 0; i < numThreads; i++) {
            double fatigueFactor = ThreadLocalRandom.current().nextDouble(0.5, 1.5); 
            TiredThread worker = new TiredThread(i, fatigueFactor); 
            workers[i] = worker;
            worker.start();
            idleMinHeap.put(worker);
        }
    }

    public void submit(Runnable task) {
        if (task == null) {
            throw new IllegalArgumentException("task == null");
        }
        
        while(true){
            try {
                TiredThread worker = idleMinHeap.take();
                inFlight.incrementAndGet();

                try {
                    

                    Runnable wrapped = () -> {
                        try {
                            task.run();
                        } finally {
                            int val = inFlight.decrementAndGet();
                            if(val == 0){
                                synchronized (this) {
                                    this.notifyAll();
                                }
                                
                            }
                            idleMinHeap.put(worker);
                           
                          
                        }
                    };
                    
                    worker.newTask(wrapped);  
                    return; 

                } catch (IllegalStateException e) {
                    int val = inFlight.decrementAndGet();
                    if(val == 0){
                        synchronized (this) {
                            this.notifyAll();
                        }
                    
                    }
                    idleMinHeap.put(worker);
                           
                }

            } catch (InterruptedException e) {
                return;
            }
       
        }
       
    }

    public void submitAll(Iterable<Runnable> tasks) {
        if (tasks == null) {
            throw new IllegalArgumentException("tasks == null");
        }
        for (Runnable task : tasks) {
            submit(task);
        }
         synchronized (this) {
            while (inFlight.get() > 0) {
                try {
                    this.wait();

                } catch (InterruptedException e) {
                }
            }
        }

    }

    public void shutdown() throws InterruptedException {

        for (TiredThread worker : workers) {
            worker.shutdown();
        }
         
        for (TiredThread worker : workers) {
            worker.join();
        }
            
    }

    public synchronized String getWorkerReport() {
        StringBuilder output = new StringBuilder();
        long avg = 0;
        for (TiredThread w : workers) {
            avg += w.getFatigue();
        }
        avg = avg / workers.length;
        for (TiredThread w : workers) {
            output.append("Worker Id: ")
              .append(w.getWorkerId())
              .append(": Time used= ")
              .append(w.getTimeUsed())
              .append(" Timeidle= ")
              .append(w.getTimeIdle())
              .append(" Fatigue= ")
              .append(w.getFatigue())
              .append(" Fatigue deviation = " )
              .append(w.getFatigue() - avg)
              .append("\n");
        }

        return output.toString();
        
    }
}
