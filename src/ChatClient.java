import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {

    public static final String hostName = "localhost";
    public static final int port = 8080;
    public boolean stopRequest;

    public ChatClient() {
        stopRequest = false;
    }

    public static void main(String[] args){
        ChatClient client = new ChatClient();
        client.startClient();
    }

    private void startClient() {
        Socket socket;
        try {
            socket = new Socket(hostName, port);
            SendMessageHandler sendMessageHandler = new SendMessageHandler(socket);
            ReceiveMessageHandler receiveMessageHandler = new ReceiveMessageHandler(socket);
            Thread sendMessageThread = new Thread(sendMessageHandler);
            Thread receiveMessageThread = new Thread(receiveMessageHandler);
            sendMessageThread.start();
            receiveMessageThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public class SendMessageHandler implements Runnable{

        public Socket socket;

        public SendMessageHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
                Scanner scanner = new Scanner(System.in);
                while(true){
                    String message = scanner.nextLine();
                    if(message.equalsIgnoreCase("quit")){
                        socket.close();
                    }else{
                        pw.println(message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class ReceiveMessageHandler implements Runnable{

        public Socket socket;

        public ReceiveMessageHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while(!stopRequest){
                    String message = br.readLine();
                    if(message == null){
                        socket.close();
                        break;
                    }
                    System.out.println(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
