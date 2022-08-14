package Model.Task;

import Model.TaskObserver;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public abstract class Task<E,F> implements Runnable{

    public int id;
    public E content;
    public List<TaskObserver<F>> taskObserverList;
    public BufferedReader br;
    public PrintWriter pw;

    public Task(E content) {
        this.content = content;
        this.br = null;
        this.pw = null;
        id = UniqueIdentifier.getUniqueIdentifier().getId();
        this.taskObserverList = new ArrayList<>();
    }

    public Task(E param, BufferedReader br, PrintWriter pw){
        this.br = br;
        this.pw = pw;
        this.content = param;
        id = UniqueIdentifier.getUniqueIdentifier().getId();
        this.taskObserverList = new ArrayList<>();
    }

    public int getId(){
        return this.id;
    }

    public void addListener(TaskObserver<F> o){
         taskObserverList.add(o);
    }

    public void configuration(BufferedReader br, PrintWriter pw){
        this.pw = pw;
        this.br = br;
    }
    public void removeListener(TaskObserver<F> o){
        taskObserverList.remove(o);
    }

    protected void notifyAll(F progress){
        for(TaskObserver<F> observer: this.taskObserverList){
            observer.inform(progress);
        }
    }

    private static class UniqueIdentifier {
        private static UniqueIdentifier uniqueIdentifier;
        private volatile int id;

        public UniqueIdentifier() {
            this.id = 0;
        }

        public int getId(){
            synchronized (this){
                return this.id++;
            }
        }

        public static UniqueIdentifier getUniqueIdentifier() {
            if(uniqueIdentifier == null){
                uniqueIdentifier = new UniqueIdentifier();
            }
            return uniqueIdentifier;
        }
    }
}
