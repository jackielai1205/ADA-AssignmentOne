import Model.TaskObserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

class TransitionHandler implements Runnable{

    private final Socket socket;
    private final BufferedReader br;
    private final PrintWriter pw;
    private final SendHandler sendHandler;
    private final ReceiveHandler receiveHandler;
    private static boolean stopRequest;

    TransitionHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.pw = new PrintWriter(socket.getOutputStream(), true);
        this.sendHandler = new SendHandler(pw);
        this.receiveHandler = new ReceiveHandler(br);
        stopRequest = false;
    }

    @Override
    public void run() {
        Thread sendThread = new Thread(sendHandler);
        Thread receiveThread = new Thread(receiveHandler);
        sendThread.start();
        receiveThread.start();
        while(receiveThread.getState() != Thread.State.TERMINATED){}
        receiveThread.stop();
        sendThread.stop();
    }

    class SendHandler implements Runnable{

        private PrintWriter pw;
        private Scanner scanner;

        public SendHandler(PrintWriter pw) {
            this.pw = pw;
            this.scanner = new Scanner(System.in);
        }

        @Override
        public void run() {
            while(!stopRequest){
                String message = this.scanner.nextLine();
                pw.println(message);
            }
        }
    }

    class ReceiveHandler implements Runnable, TaskObserver<Object>{

        private BufferedReader br;
        private boolean stopRequest;

        public ReceiveHandler(BufferedReader br) {
            this.br = br;
            stopRequest = false;
        }

        @Override
        public void run() {
            try {
                while(!stopRequest){
                    String message = br.readLine();
                    if(message == null){
                        break;
                    }
                    System.out.println(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void inform(Object progress) {
            System.out.println(progress);
            stopRequest = true;
        }
    }
}