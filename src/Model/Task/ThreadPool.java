package Model.Task;

import java.util.*;

public class ThreadPool{

    private final Queue<Runnable> queue;
    private final List<TaskThread> threadList;
    private int size;

    public ThreadPool(int initialSize) {
        this.size = initialSize;
        this.threadList = new ArrayList<>();
        for(int x = 0; x < size; x++){
            threadList.add(new TaskThread(x, new TempTask(this)));
        }
        for(TaskThread thread: threadList){
            thread.start();
        }
        this.queue = new ArrayDeque<>();
    }

    public int getSize(){
        return this.size;
    }

    public int getAvailable(){
        for(TaskThread thread : threadList){
            if(thread.getTempTask().getAvailable()){
                return thread.getThreadId();
            }
        }
        return -1;
    }

    public void resize(int newSize){
        if(newSize <= this.size){
            return;
        }
        for(int x = this.size; x < newSize; x++){
            threadList.add(new TaskThread(x, new TempTask(this)));
        }
        this.size = newSize;
    }

    public void destroyPool(){

    }

    public boolean perform(Runnable task){
        System.out.println(task);
        int available = this.getAvailable();
        System.out.println(available);
        if(available == -1){
            this.queue.add(task);
            return false;
        }
        TaskThread availableThread = threadList.get(available);
        availableThread.setTask(task);
        return true;
    }

    public class TaskThread extends Thread{

        private final int threadId;
        private final TempTask tempTask;

        public TaskThread(int id, TempTask tempTask) {
            super(tempTask);
            this.tempTask = tempTask;
            this.threadId = id;
        }

        public TempTask getTempTask() {
            return tempTask;
        }

        public int getThreadId() {
            return threadId;
        }

        public void setTask(Runnable task){
            this.tempTask.setTask(task);
        }
    }

    public class TempTask implements Runnable{

        private Runnable task;
        private boolean isRunning;
        private final ThreadPool threadPool;

        public TempTask(ThreadPool threadPool) {
            this.threadPool = threadPool;
            this.task = null;
        }

        @Override
        public void run() {
            while(true){
                while(this.task != null){
                    this.isRunning = true;
                    this.task.run();
                    this.task = null;
                    this.isRunning = false;
                    synchronized (this.threadPool.queue){
                        if(this.threadPool.queue.peek() != null){
                            this.task = this.threadPool.queue.poll();
                        }
                    }
                }
                synchronized (this){
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public boolean isRunning() {
            return isRunning;
        }

        public boolean getAvailable(){
            return task == null;
        }

        public Runnable getTask() {
            return task;
        }

        public void setTask(Runnable task) {
            synchronized (this){
                this.task = task;
                this.notify();
            }
        }
    }
}
