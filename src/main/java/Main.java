import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Objects;

public class Main {
    static int PORT = 4221;
    private static boolean running = true;
    static String successResponse = "HTTP/1.1 200 OK\r\n\r\n";
    static String notFoundResponse = "HTTP/1.1 404 Not Found\r\n\r\n";

    public static void main(String[] args) {
        // You can use print statements as follows for debugging, they'll be visible when running tests.
        System.out.println("Logs from your program will appear here!");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started listening on port " + PORT);

            while (running) {
                try (
                        Socket clientSocket = serverSocket.accept();
                        BufferedReader inRequest = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        PrintWriter outResponse = new PrintWriter(clientSocket.getOutputStream(), true)
                ) {
                    System.out.println("Client Connection is accepted ");
                    String requestLine = inRequest.readLine();

                    String []requestParts = requestLine.split(" "); //[GET, /index.html, HTTP/1.1]
                    System.out.println("parts of the requests are : " + Arrays.toString(requestParts));

                    String requestURLPath = requestParts[1];
                    String []subPaths = requestURLPath.split("/");
                    System.out.println("Sub paths: " + Arrays.toString(subPaths));
                    if (subPaths.length >1 && Objects.equals(subPaths[1], "echo")){
                        String responseString = subPaths[2];
                        outResponse.print("HTTP/1.1 200 OK\r\n");
                        outResponse.print("Content-Type: text/plain\r\n");
                        outResponse.print("Content-Length: " + responseString.length() + "\r\n");
                        outResponse.print("\r\n");
                        outResponse.print(responseString);
                        outResponse.flush();
                    }

                } catch (IOException e) {
                    System.err.println("Exception in accepting the client connection " + e.getMessage());
                }
                running = false;
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
