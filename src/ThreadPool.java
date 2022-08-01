import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ThreadPool {

    private Queue<Runnable> queue;
    private List<Thread> threadList;
    private int size;

    public ThreadPool(int initialSize) {
        this.size = initialSize;
        this.threadList = new ArrayList<>();
    }

    public int getSize(){
        return this.size;
    }

    public int getAvailable(){
        return 0;
    }

    public void resize(int newSize){
        if(newSize <= this.size){
            return;
        }
        for(int x = this.size; x <= newSize; x++){
            threadList.add(new Thread());
        }
    }

    public void destroyPool(){

    }

    public boolean perform(Runnable task){
        return false;
    }

    //+ThreadPool(initialSize:int)
    //+getSize():int
    //+getAvailable():int
    //+resize(newSize:int):void
    //+destroyPool():void
    //+perform(task:Runnable):boolean
}
