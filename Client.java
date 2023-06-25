import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        String serverIP = args[0];
        String filePath = args[1];

        try {
            File file = new File(filePath);
            FileInputStream fileInputStream = new FileInputStream(file);

            Socket clientSocket = new Socket(serverIP, 44419);
            System.out.println("Connected to server at " + serverIP + ":" + 44419);

            // Sending the file to the server
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                clientSocket.getOutputStream().write(buffer, 0, bytesRead);
            }

            System.out.println("File sent successfully.");

            fileInputStream.close();
            clientSocket.close();

            System.out.println("Client terminated.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
