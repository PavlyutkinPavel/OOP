package chat_enty;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatHistory {

    private static final String FILE_NAME = "chat_history.json";

    public static List<String> readMessages() {
        List<String> messages = new ArrayList<>();
        JSONParser parser = new JSONParser();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                JSONObject jsonObject = (JSONObject) parser.parse(line);
                String message = (String) jsonObject.get("message");
                messages.add(message);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return messages;
    }

    private static String getUsernameFromLine(String line) {
        int usernameStartIndex = line.indexOf("\"username\":") + 12;
        int usernameEndIndex = line.indexOf("\"", usernameStartIndex);
        return line.substring(usernameStartIndex, usernameEndIndex);
    }

    private static String getMessageFromLine(String line) {
        int messageStartIndex = line.indexOf("\"message\":") + 11;
        int messageEndIndex = line.lastIndexOf("\"");
        return line.substring(messageStartIndex, messageEndIndex);
    }

    public static void appendMessage(String username, String message) {



        JSONObject entryObject = new JSONObject();
        entryObject.put("username", username);
        entryObject.put("message", message);

        try (FileWriter writer = new FileWriter(FILE_NAME, true)) {
            writer.write(entryObject.toJSONString() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public static List<String> readMessages() {
//        List<String> messages = new ArrayList<>();
//        try (BufferedReader reader = new BufferedReader(new FileReader("chat_history.json"))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                String[] parts = line.split(": ", 2);
//                if (parts.length == 2) {
//                    String username = parts[0];
//                    String message = parts[1];
//                    messages.add(String.format("%s: %s", username, message));
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return messages;
//    }


}
