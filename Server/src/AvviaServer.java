import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AvviaServer implements Runnable {
    private static final int PORT = 12346;
    private static final int NUM_THREADS = 10;

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("ServerCentrale in attesa di connessioni...");

            ExecutorService threadPool = Executors.newFixedThreadPool(NUM_THREADS);

            while (ServerCentrale.statoServer) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connessione accettata.");

                threadPool.submit(new ClientHandler(clientSocket));
            }

            threadPool.shutdown();
            serverSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
