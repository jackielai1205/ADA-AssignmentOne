import Model.Task.SortTask;
import Model.Task.Task;
import Model.Task.ThreadPool;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Application {

    private ThreadPool threadPool;

    public Application(ThreadPool threadPool) {
        this.threadPool = threadPool;
    }

    public static void main(String[]  args){
        Application application = new Application(new ThreadPool(1));
        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(3);
        arrayList.add(1);
        arrayList.add(5);
        arrayList.add(4);
        arrayList.add(2);
        SortTask<Integer> task = new SortTask<>(arrayList, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        });
        application.threadPool.perform(task);
    }
}
