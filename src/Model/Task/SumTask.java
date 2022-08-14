package Model.Task;

import Model.TaskObserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SumTask extends Task<List<Integer>, String>{

    private List<Integer> numbers;

    public SumTask(List<Integer> list, BufferedReader br, PrintWriter pw) {
        super(list, br, pw);
        this.numbers = list;
    }

    public SumTask(){
        super(new ArrayList<>());
    }

    @Override
    public void run() {
        String message = "";
        int sum = 0;
        while(!message.equals("quit")){
            pw.println("Please enter a number or click \"quit\" to leave");
            try {
                message = br.readLine();
                if(message.equals("quit")){
                    break;
                }
                int number = Integer.parseInt(message);
                this.numbers.add(number);
                this.notifyAll("Added number: " + number);
                sum += number;
                this.notifyAll("Current result: " + sum);
            } catch (IOException | NumberFormatException e) {
                pw.println("Invalid Input. Please try again...");
            }
        }
        pw.println("The task is finished and the result is " + sum);
    }
}
