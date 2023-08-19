import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        final String SERVER_IP = "127.0.0.1";
        final int SERVER_PORT = 12345;

        try (
                Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in))
        ) {
            boolean flagWriter = true;

            while (true) {
                if (flagWriter) {
                    System.out.println("====================================================");
                    System.out.print("Enter 1 to get all files (or type '0' to quit): ");

                    String userInput = userInputReader.readLine();

                    if (userInput.equalsIgnoreCase("0")) {
                        break;
                    }

                    writer.write(userInput + "\n");
                    writer.flush();

                    flagWriter = false;
                }

                String message = reader.readLine();
                if (message != null) {
                    if (message.equals("END_LINES")) {
                        System.out.println("====================================================\n");
                        flagWriter = true; // End of multi-line message
                    } else {
                        System.out.println("Server sent: " + message);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
