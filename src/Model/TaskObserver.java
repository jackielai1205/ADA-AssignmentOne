package Model;

public interface TaskObserver<F> {

    void inform(F progress);
}
