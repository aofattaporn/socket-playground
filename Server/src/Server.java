import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    public static void main(String[] args) {
        final int PORT = 12345;
        int clientNo = 0;

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started. Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();

                // Increment and display client number
                System.out.println("====================================================");
                clientNo++;
                System.out.println("Client No. " + clientNo + " connected by IP: " + clientSocket.getInetAddress().getHostAddress());
                System.out.println("====================================================\n");

                // Create a new thread to handle the client
                ClientHandler clientHandler = new ClientHandler(clientSocket, clientNo);
                clientHandler.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler extends Thread {
    private final Socket clientSocket;
    private final int clientNo;

    public ClientHandler(Socket clientSocket, int clientNo) {
        this.clientSocket = clientSocket;
        this.clientNo = clientNo;
    }

    @Override
    public void run() {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {

                System.out.print("Client No. " + clientNo + " sent: " + line + " -> Server sent: ");

                if (line.equals("1")) {
                    System.out.println("List all files");
                    List<String> fileNames = getFileNamesFromDirectory();
                    for (String fileName : fileNames) {
                        writer.write(fileName + "\n");
                    }
                    writer.write("files count: " + fileNames.size() + "\n");
                    writer.write("END_LINES\n");
                    writer.flush();
                } else {
                    System.out.println("Please try again");
                    writer.write("Please try again\n");
                    writer.write("END_LINES\n");
                    writer.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                System.out.println("Client No. " + clientNo + " closed.");
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<String> getFileNamesFromDirectory() {
        List<String> fileNames = new ArrayList<>();
        File directory = new File("./assets");
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    fileNames.add(file.getName());
                }
            }
        }
        return fileNames;
    }
}
