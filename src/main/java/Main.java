import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    static final int NO_THREADS = 10;
    private static final String SUCCESS_RESPONSE = "HTTP/1.1 200 OK\r\n\r\n";
    private static final String NOT_FOUND_RESPONSE = "HTTP/1.1 404 Not Found\r\n\r\n";
    private static final ExecutorService executorService = Executors.newFixedThreadPool(NO_THREADS);
    static int PORT = 4221;
    private static boolean running = true;
    private static String directoryPath = null;

    public static void main(String[] args) {
        Map<String, String> arguments = parseArguments(args);

        if (arguments.containsKey("directory")) {
            directoryPath = arguments.get("directory");
            System.out.println("Directory path: " + directoryPath);
            // Use directoryPath as needed in your application
            // Rest of your server code
            startServer();
        } else {
            System.out.println("Failed to parse command line properties. Directory Path is not provided ");
            System.exit(1);
        }


    }

    private static Map<String, String> parseArguments(String[] args) {
        Map<String, String> arguments = new HashMap<>();

        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--")) {
                if (i + 1 < args.length && !args[i + 1].startsWith("--")) {
                    arguments.put(args[i].substring(2), args[i + 1]);
                    i++;
                } else {
                    arguments.put(args[i].substring(2), null);
                }
            }
        }
        return arguments;
    }

    public static void startServer() {
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


        public ClientSocketHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (
                    BufferedReader inRequest = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter outResponse = new PrintWriter(clientSocket.getOutputStream(), true)
            ) {
                System.out.println("Client Connection is accepted");
                handleClientRequest(inRequest, outResponse);
            } catch (IOException e) {
                System.err.println("Exception in accepting the client connection " + e.getMessage());
            } finally {
                closeClientSocket();
            }
        }

        private void handleClientRequest(BufferedReader inRequest, PrintWriter outResponse) throws IOException {
            String requestLine = inRequest.readLine();
            if (requestLine == null) {
                outResponse.flush();
                return;
            }

            String[] requestParts = requestLine.split(" ");
            System.out.println("Parts of the request are: " + Arrays.toString(requestParts));
//            assuming it is always gonna have more than one part ?
            String requestURLPath = requestParts[1];
            String[] subPaths = requestURLPath.split("/");
            System.out.println("Sub paths: " + Arrays.toString(subPaths));

            if (subPaths.length == 0) {
                outResponse.print(SUCCESS_RESPONSE);
            } else if (subPaths.length > 1) {
                switch (subPaths[1]) {
                    case "echo" -> handleEchoRequest(outResponse, subPaths);
                    case "user-agent" -> handleUserAgentRequest(inRequest, outResponse);
                    case "files" -> handleFileRequest(outResponse, subPaths);
                    default -> outResponse.print(NOT_FOUND_RESPONSE);
                }
            } else {
                outResponse.print(NOT_FOUND_RESPONSE);
            }
            outResponse.flush();
        }

        private void handleEchoRequest(PrintWriter outResponse, String[] subPaths) {
            if (subPaths.length > 2) {
                String responseString = subPaths[2];
                outResponse.print("HTTP/1.1 200 OK\r\n");
                outResponse.print("Content-Type: text/plain\r\n");
                outResponse.print("Content-Length: " + responseString.length() + "\r\n");
                outResponse.print("\r\n");
                outResponse.print(responseString);
            } else {
                outResponse.print(NOT_FOUND_RESPONSE);
            }
        }

        private void handleFileRequest(PrintWriter outResponse, String[] subPaths) throws FileNotFoundException {
            // get the file path if exists in URL path else not found
            if (subPaths.length < 3) {
                outResponse.print(NOT_FOUND_RESPONSE);
            } else {
                // get the directory path from command line and check if the file exists or not
                String filename = subPaths[2];
                File file = new File(directoryPath, filename);
                if (!file.exists() || !file.isFile()) {
                    System.out.println("Requested file does not exist at the server " + file.getAbsolutePath());
                    outResponse.print(NOT_FOUND_RESPONSE);

                } else {


                    System.out.println("Sending response: " + file.getAbsolutePath());
                    outResponse.print("HTTP/1.1 200 OK\r\n");
                    outResponse.print("Content-Type: application/octet-stream\r\n");
                    outResponse.print("Content-Length: " + file.length() + "\r\n");
                    outResponse.print("\r\n");
                    outResponse.flush();
                    // Write binary data
                    // Write file data
                    try (OutputStream os = clientSocket.getOutputStream();
                         FileInputStream fis = new FileInputStream(file)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = fis.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        os.flush();
                    } catch (IOException e) {
                        System.err.println("Error sending file data: " + e.getMessage());
                    }


                }

            }
        }

        private void handleUserAgentRequest(BufferedReader inRequest, PrintWriter outResponse) throws IOException {
            Map<String, String> headers = readHeaders(inRequest);
            String userAgent = headers.get("User-Agent");
            if (userAgent != null) {
                System.out.println("Sending response: " + userAgent);
                outResponse.print("HTTP/1.1 200 OK\r\n");
                outResponse.print("Content-Type: text/plain\r\n");
                outResponse.print("Content-Length: " + userAgent.length() + "\r\n");
                outResponse.print("\r\n");
                outResponse.print(userAgent);
            } else {
                outResponse.print(NOT_FOUND_RESPONSE);
            }
        }

        private Map<String, String> readHeaders(BufferedReader inRequest) throws IOException {
            Map<String, String> headers = new HashMap<>();
            String headerLine;
            while ((headerLine = inRequest.readLine()) != null && !headerLine.isEmpty()) {
                String[] headerParts = headerLine.split(": ", 2);
                if (headerParts.length == 2) {
                    headers.put(headerParts[0], headerParts[1]);
                }
            }
            headers.forEach((key, value) -> System.out.println(key + ": " + value));
            return headers;
        }

        private void closeClientSocket() {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
