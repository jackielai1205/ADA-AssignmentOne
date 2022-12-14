import Model.Task.*;
import Model.TaskObserver;
import Model.ThreadPool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerApplication {

    private final ThreadPool entryThread;
    private final ThreadPool taskThread;
    private final List<Socket> socketList;
    private final int port;


    public ServerApplication(int port, int threadSize) {
        this.entryThread = new ThreadPool(threadSize, "Entry thread pool");
        this.taskThread = new ThreadPool(threadSize, "Task thread pool");
        this.socketList = new ArrayList<>();
        this.port = port;
    }

    public static void main(String[]  args){
        ServerApplication serverApplication = new ServerApplication(8080, 3);
        serverApplication.startServer();
    }

    public void startServer(){
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while(true){
                Socket incomeSocket = serverSocket.accept();
                System.out.println(incomeSocket.getInetAddress() + " connected");
                socketList.add(incomeSocket);
                TaskHandler taskHandler = new TaskHandler(incomeSocket, taskThread);
                boolean available = this.entryThread.perform(taskHandler);
                if(!available){
                    PrintWriter pw = configOutputStream(incomeSocket);
                    pw.println("Server busy now. Please wait a moment...");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedReader configInputStream(Socket incomeSocket) throws IOException {
        return new BufferedReader(new InputStreamReader(incomeSocket.getInputStream()));
    }

    public PrintWriter configOutputStream(Socket incomeSocket) throws IOException {
        return new PrintWriter(incomeSocket.getOutputStream(), true);
    }

    class TaskHandler implements Runnable{

        private final Socket socket;
        private final ThreadPool taskThread;

        public TaskHandler(Socket socket, ThreadPool taskThread) {
            this.socket = socket;
            this.taskThread = taskThread;
        }

        public void printMenu(PrintWriter pw){
            pw.println("Awaiting input");
            pw.println("1) Sort list");
            pw.println("2) Sum list");
            pw.println("3) Calculate exponents");
            pw.println("5) Quit");
            pw.println("============");
        }

        public int receiveInput(BufferedReader br, PrintWriter pw) throws NullPointerException{
            int input;
            try{
                String message = br.readLine();
                if(message == null){
                    throw new NullPointerException();
                }
                input = Integer.parseInt(message);
            }catch (NumberFormatException | IOException e){
                pw.println("Invalid Input. Please try again...");
                return receiveInput(br, pw);
            }
            return input;
        }

        @Override
        public void run() {
            try {
                BufferedReader br = configInputStream(socket);
                PrintWriter pw = configOutputStream(socket);
                this.printMenu(pw);
                int clientInput = this.receiveInput(br, pw);
                switch (clientInput) {
                    case 1 -> {
                        Task<List<Integer>, String> sortTask = TaskFactory.getTask(TaskType.SORT_TASK);
                        runTask(sortTask, br, pw, socket);
                    }
                    case 2 -> {
                        Task<List<Integer>, String> sumTask = TaskFactory.getTask(TaskType.SUM_TASK);
                        runTask(sumTask, br, pw, socket);
                    }
                    case 3 ->{
                        Task<List<Integer>, String> powerTask = TaskFactory.getTask(TaskType.POWER_TASK);
                        runTask(powerTask, br, pw, socket);
                    }
                    case 5 -> {
                        pw.println("Good bye");
                        closeSocket(socket);
                    }
                }
            } catch (IOException | NullPointerException e ) {
                e.printStackTrace();
                System.out.println(socket.getInetAddress() + " left...");
                closeSocket(socket);
            }
        }

        public void runTask(Task<List<Integer>, String> task, BufferedReader br, PrintWriter pw, Socket socket){
            task.configuration(br, pw);
            task.addListener(new TaskObserver<String>() {
                @Override
                public void inform(String progress) {
                    try {
                        PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
                        pw.println(progress);
                    } catch (IOException e) {
                        System.out.println("Something went wrong");
                    }
                }
            });
            taskThread.perform(task);
        }
    }



    public void closeSocket(Socket socket){
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
