
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientApplication {

    public final int port;
    public final String hostName;
    public final Scanner scanner;
    public TransitionHandler handler;

    public ClientApplication(int port, String hostName) {
        this.port = port;
        this.hostName = hostName;
        scanner = new Scanner(System.in);
        handler = null;
    }

    public static void main(String[] args){
        ClientApplication client = new ClientApplication(8080, "localhost");
        client.startClient();
    }

    private void startClient() {
        Socket socket = null;
            try {
                socket = new Socket(hostName, port);
                this.handler = new TransitionHandler(socket);
                System.out.println(socket.getInetAddress());
                Thread thread = new Thread(this.handler);
                thread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
