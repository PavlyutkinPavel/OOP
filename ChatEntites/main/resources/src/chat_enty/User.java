package chat_enty;

import chat_storage.DataBaseConnector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Repository;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import static chat_network.TCPService.isTCPPortOpen;
import static chat_server.ChatServer.addUser;
import static chat_server.ChatServer.unregister;

@Entity
@Repository
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int userId;
    public String username;
    public String password;



    public User(){

    }
    public User(String username, String password){
        this.userId = 1;
        this.username = username;
        this.password = password;
        boolean conReady = isTCPPortOpen("127.0.0.1", 1234);
//        try {
//            tcpConnection = new TCPConnection((TCPConnectionListener) this, "127.0.0.1", 1234);
//        } catch (IOException e) {
//            System.out.println("Connection exception: " + e);
//        }
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void addUserToDatabase() {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String username = "username";
        String password = "password";

        try {
            DataBaseConnector connector = new DataBaseConnector(url, username, password);
            connector.connect();
            connector.addUser(this);
            connector.disconnect();
            System.out.println("User added to the database!");
        } catch (SQLException e) {
            System.out.println("Failed to add user to the database: "+ e);
            e.printStackTrace();
        }
    }
    public void addUserToFile(){
        addUser(this.username);
        JSONParser parser = new JSONParser();
        try {
            JSONObject obj = (JSONObject) parser.parse(new FileReader("users.json"));
            JSONArray userArray = (JSONArray) obj.get("users");
            ArrayList<String> usernames = new ArrayList<>();
            for (Object o : userArray) {
                JSONObject user = (JSONObject) o;
                String username = (String) user.get("username");
                usernames.add(username);
                this.username = username;

            }
            // теперь мы можем использовать переменную usernames для доступа к именам всех пользователей в файле
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException e) {
            throw new RuntimeException(e);
        }
    }

    //functions for admin
    private void deleteUser(String usernameToDelete){
        unregister(this.username);
        JSONParser parser = new JSONParser();
        try {
            JSONObject obj = (JSONObject) parser.parse(new FileReader("users.json"));
            JSONArray userArray = (JSONArray) obj.get("users");
            Iterator<JSONObject> iterator = userArray.iterator();
            while (iterator.hasNext()) {
                JSONObject user = iterator.next();
                String username = (String) user.get("username");
                if (username.equals(usernameToDelete)) {
                    iterator.remove();
                    break;
                }
            }
            obj.put("users", userArray);
            FileWriter fileWriter = new FileWriter("users.json");
            fileWriter.write(obj.toJSONString());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException e) {
            throw new RuntimeException(e);
        }

    }


    private void editUser(String old_username, String new_username){
        JSONParser parser = new JSONParser();
        try {
            JSONObject obj = (JSONObject) parser.parse(new FileReader("users.json"));
            JSONArray userArray = (JSONArray) obj.get("users");
            for (Object o : userArray) {
                JSONObject user = (JSONObject) o;
                String username = (String) user.get("username");
                if (username.equals(old_username)) {
                    user.put("username", new_username);
                }
            }
            // сохраняем изменения в файл
            try (FileWriter file = new FileWriter("users.json")) {
                file.write(obj.toJSONString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException e) {
            throw new RuntimeException(e);
        }

    }
    private void searchUser(String usernameSearch){
        JSONParser parser = new JSONParser();
        try {
            JSONObject obj = (JSONObject) parser.parse(new FileReader("users.json"));
            JSONArray userArray = (JSONArray) obj.get("users");
            for (Object o : userArray) {
                JSONObject user = (JSONObject) o;
                String username = (String) user.get("username");
                if (username.equals(usernameSearch)) { // здесь нужно указать имя искомого пользователя
                    // пользователь найден
                    String email = (String) user.get("email");
                    // ... здесь можно обрабатывать найденного пользователя
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException e) {
            throw new RuntimeException(e);
        }

    }
    public String toString(){
        return "username: "+this.username+" , password: "+this.password;
    }

}
