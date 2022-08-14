package Model.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PowerTask extends Task<List<Integer>, String>{

    public PowerTask() {
        super(new ArrayList<>());
    }

    @Override
    public void run() {
        pw.println("Please enter the base...");
        int base = getInput();
        pw.println("Please enter the power...");
        int power = getInput();
        pw.println("The " + base + " to the power " + power + " is " + (int)Math.pow(base, power));
    }

    public int getInput(){
        String message;
        int input;
        try {
            message = br.readLine();
            input = Integer.parseInt(message);
        } catch (IOException | NumberFormatException e ) {
            pw.println("Invalid Input. Please enter again..");
            return getInput();
        }
        return input;
    }
}
