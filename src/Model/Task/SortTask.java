package Model.Task;

import java.util.*;

public class SortTask<T> extends Task<Collection<T>,Listener<T>>{

    private final List<T> list;
    private final Comparator<? super T> comparator;

    public SortTask(Collection<T> list, Comparator<? super T> comparator) {
        super(list);
        this.list = convertToList(list);
        this.comparator = comparator;
    }


    public List<T> convertToList(Collection<T> rawList){
        return new ArrayList<>(rawList);
    }

    @Override
    public void run() {
        Collections.sort(this.list, comparator);
        System.out.println(this.list);
    }
}
