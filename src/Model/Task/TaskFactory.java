package Model.Task;

import java.util.List;

public abstract class TaskFactory {

    public static Task<List<Integer>, String> getTask(TaskType type){
        return switch (type) {
            case SORT_TASK -> new SortTask();
            case SUM_TASK -> new SumTask();
            case POWER_TASK -> new PowerTask();
        };
    }


}
