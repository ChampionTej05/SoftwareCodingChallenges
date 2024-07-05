//
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.Socket;
//import java.net.URL;
//
//public class MainTest {
//
//    private static Thread serverThread;
//
//    @BeforeAll
//    public static void startServer() {
//        serverThread = new Thread(() -> Main.main(new String[]{}));
//        serverThread.start();
//        waitForServerToBeReady();
//    }
//
////    @AfterAll
//    public static void stopServer() {
//        Main.stopServer();
//        try {
//            serverThread.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Test
//    public void testServerResponse() throws Exception {
//        URL url = new URL("http://localhost:4221");
//        HttpURLConnection con = (HttpURLConnection) url.openConnection();
//        con.setRequestMethod("GET");
//
//        int responseCode = con.getResponseCode();
//        assertEquals(200, responseCode);
//
//        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//        String inputLine;
//        StringBuilder content = new StringBuilder();
//        while ((inputLine = in.readLine()) != null) {
//            content.append(inputLine);
//        }
//        in.close();
//
//        // Check if the content is as expected (in this case it will be empty, but you can adjust as needed)
//        assertEquals("", content.toString());
//        stopServer();
//    }
//
//    private static void waitForServerToBeReady() {
//        int retries = 10;
//        int delay = 500; // milliseconds
//        while (retries-- > 0) {
//            try (Socket socket = new Socket("localhost", 4221)) {
//                return; // Server is ready
//            } catch (Exception e) {
//                try {
//                    Thread.sleep(delay);
//                } catch (InterruptedException interruptedException) {
//                    interruptedException.printStackTrace();
//                }
//            }
//        }
//        throw new RuntimeException("Server did not start in time");
//    }
//}

import org.junit.jupiter.api.Test;

public class MainTest {
    @Test
    void test(){
        System.out.println("No tests written yet ");
    }
}
