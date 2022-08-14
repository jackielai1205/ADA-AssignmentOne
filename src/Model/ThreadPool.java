package Model;

import java.util.*;

public class ThreadPool{

    private String name;
    private final Queue<Runnable> queue;
    private final List<TaskThread> threadList;
    private int size;
    private boolean isBlocked;

    public ThreadPool(int initialSize, String name) {
        this.name = name;
        this.size = initialSize;
        this.threadList = new ArrayList<>();
        for(int x = 0; x < size; x++){
            threadList.add(new TaskThread(x, new TempTask(this)));
        }
        for(TaskThread thread: threadList){
            thread.start();
        }
        this.queue = new ArrayDeque<>();
        isBlocked = false;
    }

    public int getSize(){
        return this.size;
    }

    public int getAvailable(){
        if(!isBlocked){
            for(TaskThread thread : threadList){
                if(thread.getTempTask().getAvailable()){
                    return thread.getThreadId();
                }
            }
        }
        return -1;
    }

    public void resize(int newSize){

        if(newSize == this.size){
            return;
        }
        isBlocked = true;
        for(TaskThread thread : threadList){
            while(!thread.getTempTask().getAvailable()){

            }
        }
        if(newSize < this.size){
            threadList.subList(newSize, this.size).clear();
        }else if(newSize > this.size){
            for(int x = this.size; x < newSize; x++){
                threadList.add(new TaskThread(x, new TempTask(null)));
            }
        }
        this.size =  newSize;
        isBlocked = false;
    }

    public void destroyPool(){

    }

    public boolean perform(Runnable task){
        int available = this.getAvailable();
        System.out.println("Assigned thread " + available + " from " + name);
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
            if(!isBlocked){
                synchronized (this){
                    this.task = task;
                    this.notify();
                }
            }
        }
    }
}
