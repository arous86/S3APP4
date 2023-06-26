import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        String serverIP = args[0];
        String filePath = args[1];

        BufferedReader reader = null;

        try {
            FileReader fileReader = new FileReader(filePath);
            reader = new BufferedReader(fileReader);

            String line;
            StringBuilder content = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

            System.out.println(content.toString());
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing the file: " + e.getMessage());
            }
        }
    }
}
