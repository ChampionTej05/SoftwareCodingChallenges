import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    static int PORT = 4221;
    private static boolean running = true;

    public static void main(String[] args) {
        // You can use print statements as follows for debugging, they'll be visible when running tests.
        System.out.println("Logs from your program will appear here!");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started listening on port " + PORT);

            while (running) {
                try (
                        Socket clientSocket = serverSocket.accept();
                        PrintWriter outResponse = new PrintWriter(clientSocket.getOutputStream(), true)
                ) {
                    System.out.println("Client Connection is accepted ");
                    outResponse.print("HTTP/1.1 200 OK\r\n\r\n");
                    outResponse.flush();
                } catch (IOException e) {
                    System.err.println("Exception in accepting the client connection " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    public static void stopServer() {
        running = false;
        try (Socket socket = new Socket("localhost", PORT)) {
            // no-op
            System.out.println("closed connection from server");
        } catch (IOException e) {
            // no-op
        }
    }
}
