package Model.Task;

public abstract class TaskFactory {

    public static Task getTask(TaskType type){
        return switch (type) {
            case SORT_TASK -> new SortTask();
            case SUM_TASK -> new SumTask();
        };
    }


}
