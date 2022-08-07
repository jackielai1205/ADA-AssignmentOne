package Model.Task;

public abstract class Task<E,F> implements Runnable{

    public int id;
    public E content;
    public TaskObserver<F> taskObserver;

    public Task(E param){
        this.content = param;
        id = UniqueIdentifier.getId();
        this.taskObserver = null;
    }

    public int getId(){
        return this.id;
    }

    public void addListener(TaskObserver<F> o){
        this.taskObserver = o;
    }
    public void removeListener(TaskObserver<F> o){
    }

    protected void notifyAll(F progress){

    }

    private static class UniqueIdentifier {
        private static int id = 0;

        public static int getId(){
            return id++;
        }
    }
}
