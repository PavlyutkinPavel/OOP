package groupId;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.List;
import java.util.Map;

public class JsonSerializer {
    private static final ObjectMapper objectMapper = new ObjectMapper();

//    public static <T> void serialize(List<T> obj, String path) {
//        try {
//            String json = objectMapper.writeValueAsString(obj);
//            File file = new File(path);
//            FileWriter fileWriter = new FileWriter(file, true);
//            if (file.length() > 0) {
//                fileWriter.write(", ");
//            } else {
//                fileWriter.write("{\""+path+"\": [");
//            }
//            fileWriter.write(json);
//            fileWriter.close(); // Перенесено сюда
//            if (file.length() > 0) {
//                fileWriter = new FileWriter(file, true); // Открываем снова для дозаписи
//                fileWriter.write("]}");
//                fileWriter.close();
//            } else {
//                fileWriter = new FileWriter(file, true); // Открываем снова для дозаписи
//                fileWriter.write("]}");
//                fileWriter.close();
//            }
//        } catch (IOException e) {
//            throw new RuntimeException("Error serializing object to JSON: " + obj, e);
//        }
//    }

    public static <T> void serialize(List<T> obj, String path) {
        try {
            String json = objectMapper.writeValueAsString(obj);
            File file = new File(path);
            StringBuilder stringBuilder = new StringBuilder();

            if (file.exists()) {
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                bufferedReader.close();
            }

            String content = stringBuilder.toString().trim();

            FileWriter fileWriter = new FileWriter(file);
            if (!content.isEmpty()) {
                int index = content.lastIndexOf("]");
                if (index != -1) {
                    // Check if the new object already exists in the array
                    String existingArray = content.substring(content.indexOf("[") + 1, index).trim();
                    if (!existingArray.contains(json.substring(1, json.length() - 1))) {
                        content = content.substring(0, index) + ", " + json.substring(1, json.length() - 1) + "]";
                    }
                }
            } else {
                content = "{\""+path+"\": [" + json.substring(1, json.length() - 1) + "]}";
            }
            if (!content.endsWith("}")) {
                content += "}";
            }
            fileWriter.write(content);
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException("Error serializing object to JSON: " + obj, e);
        }
    }

    public static List<List<String>> deserialize(String path) {
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();

            String json = stringBuilder.toString();
            Map<String, List<List<String>>> data = objectMapper.readValue(new StringReader(json), new TypeReference<Map<String, List<List<String>>>>() {});

            // Получить значение из первого ключа
            String firstKey = data.keySet().iterator().next();
            return data.get(firstKey);
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing JSON: " + path, e);
        }
    }



}
