import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

class AziendaNotificationServer implements Runnable {
    private final BlockingQueue<String> notificationQueue;

    public AziendaNotificationServer(BlockingQueue<String> notificationQueue) {
        this.notificationQueue = notificationQueue;
    }

    @Override
    public void run() {
        // Questo server rimane sempre in ascolto sulla porta 12347
        try (ServerSocket serverSocket = new ServerSocket(12347)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                // Aspetta una notifica dalla coda e la invia al client azienda
                String notification = notificationQueue.take();
                out.println(notification);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}