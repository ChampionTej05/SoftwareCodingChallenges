import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    static int PORT = 4221;
    static final int NO_THREADS = 10;
    private static boolean running = true;
    static String successResponse = "HTTP/1.1 200 OK\r\n\r\n";
    static String notFoundResponse = "HTTP/1.1 404 Not Found\r\n\r\n";
    private static final ExecutorService executorService = Executors.newFixedThreadPool(NO_THREADS);
    public static void main(String[] args) {
        // You can use print statements as follows for debugging, they'll be visible when running tests.
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started listening on port " + PORT);

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    executorService.submit(new ClientSocketHandler(clientSocket));
                } catch (IOException e) {
                    System.err.println("Exception in accepting the client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } finally {
            executorService.shutdown();
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

    public static class ClientSocketHandler implements Runnable {
        private final Socket clientSocket;


        ClientSocketHandler(Socket clientSocket){
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {

                try (
                        BufferedReader inRequest = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        PrintWriter outResponse = new PrintWriter(clientSocket.getOutputStream(), true)
                ) {
                    System.out.println("Client Connection is accepted ");
                    String requestLine = inRequest.readLine();
                    if (requestLine == null){
                        outResponse.flush();

                    }

                    String []requestParts = Objects.requireNonNull(requestLine).split(" "); //[GET, /index.html, HTTP/1.1]
                    System.out.println("parts of the requests are : " + Arrays.toString(requestParts));

                    String requestURLPath = requestParts[1];
                    String []subPaths = requestURLPath.split("/");
                    System.out.println("Sub paths: " + Arrays.toString(subPaths));
                    if (subPaths.length == 0 ){
                        outResponse.print(successResponse);
                    }else if (subPaths.length >1 && Objects.equals(subPaths[1], "echo")){
                        String responseString = subPaths[2];
                        outResponse.print("HTTP/1.1 200 OK\r\n");
                        outResponse.print("Content-Type: text/plain\r\n");
                        outResponse.print("Content-Length: " + responseString.length() + "\r\n");
                        outResponse.print("\r\n");
                        outResponse.print(responseString);
                    } else if (subPaths.length >1 && Objects.equals(subPaths[1], "user-agent")){
                        HashMap < String, String> headers = new HashMap<>();
                        String headerLine;
                        while ((headerLine = inRequest.readLine()) != null && !headerLine.isEmpty()) {
                            String[] headerParts = headerLine.split(": ", 2);
                            if (headerParts.length == 2) {
                                headers.put(headerParts[0], headerParts[1]);
                            }
                        }
                        headers.forEach((key, value) -> System.out.println(key + ": " + value));
                        String userAgent = headers.get("User-Agent");
                        System.out.println("Sending response " + userAgent);
                        outResponse.print("HTTP/1.1 200 OK\r\n");
                        outResponse.print("Content-Type: text/plain\r\n");
                        outResponse.print("Content-Length: " + userAgent.length() + "\r\n");
                        outResponse.print("\r\n");
                        outResponse.print(userAgent);

                    }
                    else{
                        outResponse.print(notFoundResponse);
                    }
                    outResponse.flush();

                } catch (IOException e) {
                    System.err.println("Exception in accepting the client connection " + e.getMessage());
                } finally {
                    try{
                        clientSocket.close();
                    }catch( IOException e){
                        e.printStackTrace();
                    }
                }

        }
    }
}
