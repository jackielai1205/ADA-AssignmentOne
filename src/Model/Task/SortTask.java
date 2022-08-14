package Model.Task;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class SortTask extends Task<List<Integer>, String>{

    private final List<Integer> list;

    public SortTask(List<Integer> list, BufferedReader br, PrintWriter pw) {
        super(list, br, pw);
        this.list = convertToList(list);
    }

    public SortTask(){
        super(new ArrayList<>());
        this.list = new ArrayList<>();
    }

    public List<Integer> convertToList(List<Integer> rawList){
        return new ArrayList<>(rawList);
    }

    public void addElement(Integer element){
        this.list.add(element);
    }

    @Override
    public void run() {
        while(true){
            try{
                pw.println("Please enter a number or enter \"quit\" to stop");
                String message = br.readLine();
                if(message == null || message.equals("quit")){
                    break;
                }
                int number = Integer.parseInt(message);
                this.addElement(number);
                this.notifyAll("Added number: " + number);
                this.notifyAll("List as below");
                this.notifyAll(this.list.toString());
            }catch (NumberFormatException | IOException e){
                pw.println("Invalid Input. Please try again");
            }
        }

        this.notifyAll("Received all elements. Sorting elements...");

        this.list.sort(new Comparator<>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        });
        this.notifyAll("Sort completed.");
        pw.println(this.list);
    }
}
