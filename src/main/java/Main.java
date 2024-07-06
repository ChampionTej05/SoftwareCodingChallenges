import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    static final int NO_THREADS = 10;
    private static final String SUCCESS_RESPONSE = "HTTP/1.1 200 OK\r\n\r\n";
    private static final String NOT_FOUND_RESPONSE = "HTTP/1.1 404 Not Found\r\n\r\n";
    private static final String HTTP_201_CREATED_RESPONSE = "HTTP/1.1 201 Created\r\n\r\n";
    private static final ExecutorService executorService = Executors.newFixedThreadPool(NO_THREADS);
    static int PORT = 4221;
    private static boolean running = true;
    private static String directoryPath = null;

    private static final List<String> ALLOWED_ENCODING_ALGORITHMS = List.of("gzip");

    public static void main(String[] args) {
        Map<String, String> arguments = parseArguments(args);

        if (arguments.containsKey("directory")) {
            directoryPath = arguments.get("directory");
            System.out.println("Directory path: " + directoryPath);
            // Use directoryPath as needed in your application
            // Rest of your server code

        } else {
            System.out.println("Failed to parse command line properties. Directory Path is not provided ");

        }
        startServer();


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
                    executorService.submit(new ClientSocketHandler(
                            clientSocket, clientSocket.getInputStream(), clientSocket.getOutputStream()));
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
        Map<String, String> headers ;
        String requestLine;
        InputStream inputStream ;
        OutputStream outputStream;
        PrintWriter outResponse;

        BufferedReader inRequest;


        public ClientSocketHandler(Socket clientSocket, InputStream inputStream, OutputStream outputStream) {
            this.clientSocket = clientSocket;
            this.inputStream = inputStream;
            this.outputStream = outputStream;
            this.outResponse = new PrintWriter(this.outputStream, true);
            this.inRequest = new BufferedReader(new InputStreamReader(inputStream));
        }

        @Override
        public void run() {
            try  {
                System.out.println("Client Connection is accepted");
                handleClientRequest();
            } catch (IOException e) {
                System.err.println("Exception in accepting the client connection " + e.getMessage());
            } finally {
                closeClientSocket();
            }
        }

        private void handleClientRequest( ) throws IOException {
            processRequestInformation();
//            readRequestBody();

            String[] requestParts = requestLine.split(" ");
            System.out.println("Parts of the request are: " + Arrays.toString(requestParts));
//            assuming it is always gonna have more than one part ?
            String requestURLPath = requestParts[1];
            String method = requestParts[0];
            String[] subPaths = requestURLPath.split("/");
            System.out.println("Sub paths: " + Arrays.toString(subPaths));

            if (subPaths.length == 0) {
                outResponse.print(SUCCESS_RESPONSE);
            } else if (subPaths.length > 1) {
                switch (subPaths[1]) {
                    case "echo" -> handleEchoRequest(subPaths);
                    case "user-agent" -> handleUserAgentRequest();
                    case "files" -> handleFileRequest(subPaths, method);
                    default -> outResponse.print(NOT_FOUND_RESPONSE);
                }
            } else {
                outResponse.print(NOT_FOUND_RESPONSE);
            }
            inputStream.close();
        }

        private void handleEchoRequest( String[] subPaths) {
            if (subPaths.length > 2) {
                String responseString = subPaths[2];
                String acceptEncoding = headers.get("Accept-Encoding");

                outResponse.print("HTTP/1.1 200 OK\r\n");
                outResponse.print("Content-Type: text/plain\r\n");
                outResponse.print("Content-Length: " + responseString.length() + "\r\n");
                System.out.println("Encoding in the string " + acceptEncoding);
                if((acceptEncoding!=null) &&
                        ALLOWED_ENCODING_ALGORITHMS.contains(acceptEncoding)){
                    outResponse.print("Content-Encoding: " + acceptEncoding + "\r\n");
                }
                outResponse.print("\r\n");
                outResponse.print(responseString);

            } else {
                outResponse.print(NOT_FOUND_RESPONSE);
            }
            outResponse.flush();
        }

        private void handleFileRequest( String[] subPaths, String method) throws FileNotFoundException {
            // get the file path if exists in URL path else not found
            if (subPaths.length < 3) {
                outResponse.print(NOT_FOUND_RESPONSE);
                outResponse.flush();
            } else {

                if (directoryPath == null){
                    outResponse.print(NOT_FOUND_RESPONSE);
                    outResponse.flush();
                    return;
                }
                // get the directory path from command line and check if the file exists or not
                String filename = subPaths[2];
                File file = new File(directoryPath, filename);
                switch(method){
                    case "GET" -> handleGetFileRequest(file);
                    case "POST" -> handlePostFileRequest(file);
                    default -> outResponse.print(NOT_FOUND_RESPONSE);
                }


            }
        }

//TODO: Debug the implementation of this class
        private void handlePostFileRequest(File file){
            try(FileOutputStream fs = new FileOutputStream(file)) {
                StringBuffer bodyBuffer = new StringBuffer();
                while(inRequest.ready()){
                    bodyBuffer.append((char) inRequest.read());
                }
                String requestBody = bodyBuffer.toString();
                System.out.println("Body read: "+requestBody);

                fs.write(requestBody.getBytes());
                fs.flush();
                outResponse.print(HTTP_201_CREATED_RESPONSE);
                outResponse.flush();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void handleGetFileRequest(File file){
            if (!file.exists() || !file.isFile()) {
                System.out.println("Requested file does not exist at the server " + file.getAbsolutePath());
                outResponse.print(NOT_FOUND_RESPONSE);
                outResponse.flush();

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

        private void handleUserAgentRequest() {

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

        private void processRequestInformation() throws IOException{
//            this carries information of "POST /files/number HTTP/1.1"

            requestLine = inRequest.readLine();

            if (requestLine == null) {
                System.out.println("Null request line now");
                throw new IOException("Request Line is empty ");
            }

            headers = readHeaders();
        }

        private Map<String, String> readHeaders() throws IOException {
            Map<String, String> headers = new HashMap<>();
            String headerLine;
            while ((headerLine = inRequest.readLine()) != null && !headerLine.isEmpty()) {
                System.out.println("headerline : "+ headerLine);
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
