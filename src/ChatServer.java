import Model.ThreadPool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {

    public static final int port = 8080;
    public List<Socket> socketList = new ArrayList<>();
    public final ThreadPool threadPool;

    public ChatServer() {
        this.threadPool = new ThreadPool(2, "");
    }

    public static void main(String[] args){
        ChatServer server = new ChatServer();
        server.startServer();
    }

    public void startServer(){
        boolean stopRequest = false;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Port already in use");
            System.exit(-1);
        }
        Socket socket = null;
        while(!stopRequest){
            try {
                socket = serverSocket.accept();
                SocketRunnable task = new SocketRunnable(socket);
                this.threadPool.perform(task);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendPodCastMessage(Socket socket, String message){
        for(Socket currentSocket:socketList){
            if(!currentSocket.equals(socket)){
                try {
                    PrintWriter socketWriter = new PrintWriter(currentSocket.getOutputStream(), true);
                    socketWriter.println(socket.getInetAddress() + ": " + message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class SocketRunnable implements Runnable{

        private final Socket socket;

        public SocketRunnable(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            PrintWriter pw= null;
            BufferedReader br = null;
            try {
                System.out.println("Connection made with " + socket.getInetAddress());
                pw = new PrintWriter(socket.getOutputStream(), true);
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                socketList.add(socket);
                while(true){
                    String message = br.readLine();
                    if(message != null){
                        System.out.println(message);
                        sendPodCastMessage(socket, message);
                    }else{
                        br.close();
                        pw.close();
                        socket.close();
                    }
                }
            } catch (IOException e) {
                String clientName = socket!=null ? String.valueOf(socket.getInetAddress()) :"Error";
                System.out.println(clientName + " had left...");
            } finally{
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(pw != null) {
                    pw.close();
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                socketList.remove(socket);
            }
        }
    }
}
