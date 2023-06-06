package groupId;

import com.google.gson.Gson;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class MySerializer {
    public MySerializer() {
    }

    public synchronized static <T> void Write(List<T> obj, String path) throws IOException {
        Gson gson = new Gson();
        FileWriter fileWriter = new FileWriter(path);
        fileWriter.write(gson.toJson(obj));
        fileWriter.close();
    }

    public synchronized static <T> T Read(String path, Class<T> cl) throws IOException {
        Gson gson = new Gson();
        FileReader fileReader = new FileReader(path);
        Scanner sc = new Scanner(fileReader);
        T out = gson.fromJson(sc.nextLine(), cl);
        fileReader.close();
        sc.close();
        return out;
    }
}
