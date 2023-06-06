package chat_client;

import chat_storage.DataBaseConnector;
import org.postgresql.Driver;
import chat_network.TCPConnection;
import chat_network.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

import mdlaf.MaterialLookAndFeel;
import mdlaf.themes.MaterialLiteTheme;



//import mybibl.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import chat_enty.User;

import static chat_server.ChatServer.addUser;

public class LogWindow extends JFrame implements ActionListener, TCPConnectionListener {

    private static final String IP_ADDR = "127.0.0.1";
    private static final int PORT = 1234;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LogWindow();//в потоке edtt
            }
        });
    }

    private TCPConnection connection;
    private final JTextField fieldHI = new JTextField("             WELCOME BACK TO PASHAGRAM");
    //private final JButton logInButton;

    private JFrame frame;
    public JTextField nameField;
    public JPasswordField passwordField;

    public LogWindow() {
//        setTitle("PASHAGRAM");
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setSize(WIDTH, HEIGHT);
//        setLocationRelativeTo(null);
//
//        fieldHI.setEditable(false);
//        add(fieldHI, BorderLayout.NORTH);
//
//        logInButton = new JButton("ВОЙТИ");
//
//
//        JPanel panel = new JPanel();
//        panel.setLayout(new BorderLayout(10, 10));
//        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
//        panel.add(logInButton, BorderLayout.CENTER);
//
//
//        add(panel);
//        setVisible(true);
        //ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);

        try {
            connection = new TCPConnection(this, IP_ADDR, PORT);
        } catch (IOException e) {
            System.out.println("Connection exception: " + e);
        }
        try {
            // Установка Material UI Look and Feel
            UIManager.setLookAndFeel(new MaterialLookAndFeel(new MaterialLiteTheme()));
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        frame = new JFrame("PASHAGRAM");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(10, 10, 10, 10);

        // Заголовок
        JLabel titleLabel = new JLabel("Welcome back to PASHAGRAM!");
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 24));
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        frame.add(titleLabel, constraints);

        // Поле ввода имени
        JLabel nameLabel = new JLabel("Name:");
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        frame.add(nameLabel, constraints);

        nameField = new JTextField();
        nameField.setColumns(25);
        constraints.gridx = 1;
        constraints.gridy = 1;
        frame.add(nameField, constraints);

        // Поле ввода пароля
        JLabel passwordLabel = new JLabel("Password:");
        constraints.gridx = 0;
        constraints.gridy = 2;
        frame.add(passwordLabel, constraints);

        passwordField = new JPasswordField();
        passwordField.setColumns(25);
        constraints.gridx = 1;
        constraints.gridy = 2;
        frame.add(passwordField, constraints);
        // Кнопка "Войти"
        JButton loginButton = new JButton("LOG IN");
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        frame.add(loginButton, constraints);

        frame.setVisible(true);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                String username = JOptionPane.showInputDialog("Enter username:");
//                String password = JOptionPane.showInputDialog("Enter password:");
                String username = nameField.getText();
                String pass = Arrays.toString(passwordField.getPassword());
                String password = pass.replaceAll("[\\[\\],\\s]", "");


                JSONParser parser = new JSONParser();
                JSONObject obj = null;
                int JsonORDb = 2;//1-json, 2-db;
                User mainUser = new User(username, password);
                switch (JsonORDb) {
                    case 1:
                        try {
                            obj = (JSONObject) parser.parse(new FileReader("users.json"));
                            JSONArray userArray = (JSONArray) obj.get("users");

                            // Check if user already exists in the file
                            boolean userExists = false;
                            for (Object o : userArray) {
                                JSONObject user = (JSONObject) o;
                                if (user.get("username").equals(username)) {
                                    userExists = true;
                                    // Check if password is correct
                                    if (user.get("password").equals(password)) {
                                        JOptionPane.showMessageDialog(null, "You logged in successfully!");
                                        MessengerApp messengerApp1 = new MessengerApp(username);
                                        //ClientView clientView1 = new ClientView(username);
                                        addUser(username);
                                        User user1 = new User(username, password);
                                        user1.addUserToFile();
                                        //                                UserRepository userRepository = new UserJpaRepository();
                                        //                                userRepository.save(user1);
                                        //                                UserService userService = context.getBean(UserService.class);//new UserService(userRepository);//
                                        //                                userService.createUser(user1);
                                        //user1.addUserToDatabase();
                                        setVisible(false);
                                        //fieldHI.setText("You registered successfully!");
                                        break;
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Wrong password!");
                                        //fieldHI.setText("Wrong password!");
                                        break;
                                    }
                                }
                            }
                            // If user does not exist, create new user object and add to array
                            if (!userExists) {
                                JOptionPane.showMessageDialog(null, "Wrong name or password!");
                                setVisible(false);
                            }
                        } catch (FileNotFoundException ex) {
                            // File does not exist yet, create new array with first user object
                            JSONArray userArray = new JSONArray();
                            JSONObject newUser = new JSONObject();
                            newUser.put("username", username);
                            newUser.put("password", password);
                            userArray.add(newUser);
                            JSONObject root = new JSONObject();
                            root.put("users", userArray);
                            // Write JSON object to file
                            try (FileWriter file = new FileWriter("users.json")) {
                                file.write(root.toJSONString());
                                file.flush();
                            } catch (IOException ex2) {
                                ex2.printStackTrace();
                            }
                            JOptionPane.showMessageDialog(null, "User created!");
                            User user = new User(username, password);
                            addUser(username);
                            //                    UserService userService = context.getBean(UserService.class);
                            //                    userService.createUser(user);
                            //user.addUserToDatabase();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        } catch (SQLException ex) {
                            System.out.println("Sql exception log part");
                        }
                    case 2:
                        DataBaseConnector connector = new DataBaseConnector("jdbc:postgresql://localhost:5432/postgres", "postgres", "4864");
                        try {
                            Class.forName("org.postgresql.Driver");
                        } catch (ClassNotFoundException exception) {
                            exception.printStackTrace();
                        }

                        try {
                            Driver driver = new org.postgresql.Driver();
                            DriverManager.registerDriver(driver);
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, "Unable to load driver class!");
                            ex.printStackTrace();
                        }
                        try {
                            connector.connect();
                        } catch (SQLException ex) {
                            System.out.println("Sql connection error");
                        }
                        try {
                            if(connector.userExists(username, password)){
                                JOptionPane.showMessageDialog(null, "You logged in successfully!");
                                MessengerApp app = new MessengerApp(username);
                            }else{
                                JOptionPane.showMessageDialog(null, "There is no such a user!");
                            }
                        } catch (SQLException ex) {
                            System.out.println("Sql adding user error");
                            ex.printStackTrace();
                        }
                }
            }
        });




        //                //work with mybibl
//                List<List<String>> logInfo = new ArrayList<>();
//                List<String> entry1 = Arrays.asList(username, password);
//                logInfo.add(entry1);
//
//                User userD = new User(username, password);
//
//                List<List<String>> data = JsonSerializer.deserialize("usersLib.json");
//                System.out.println(data);
//
//                boolean userExists = false;
//                for (int i = 0; i < data.size(); i++) {
//                    if(username.equals(data.get(i).get(0))){
//                        userExists = true;
//                        if(password.equals(data.get(i).get(1))){
//                            JOptionPane.showMessageDialog(null, "You logged in successfully!");
//                            ClientView clientView1 = new ClientView(username);//username
//                            addUser(username);
//                            User user1 = new User(username, password);
//                            user1.addUserToFile();
//                            setVisible(false);
//                            //fieldHI.setText("You registered successfully!");
//                            break;
//                        }else{
//                            JOptionPane.showMessageDialog(null, "Wrong password!");
//                        }
//                    }if(!userExists){
//                        JsonSerializer.serialize(logInfo, "usersLib.json");
//                        JOptionPane.showMessageDialog(null, "User created!");
//                        ClientView clientView2 = new ClientView(username);
//                        addUser(username);
//                        User user2 = new User(username, password);
//                        user2.addUserToFile();
//                        setVisible(false);
//                    }
//                }
//            }

//            logInButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                // Действия при нажатии кнопки LOG IN
//                String username = JOptionPane.showInputDialog("Enter username:");
//                String password = JOptionPane.showInputDialog("Enter password:");
//                JSONObject obj = new JSONObject();
//                obj.put("username", username);
//                obj.put("password", password);
//                try (FileWriter file = new FileWriter( "users.json", true)) {
//                    file.write(obj.toJSONString() + "\n");
//                    file.flush();
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }
//
////                try {
////                    String url = "jdbc:postgresql://localhost:3036/mydatabase";
////                    String dbUsername = "myusername";
////                    String dbPassword = "mypassword";
////                    try{
////                        Class.forName("org.postgresql.Driver");
////                    }catch(ClassNotFoundException exception){
////                        exception.printStackTrace();
////                    }
////
//////                    try{
//////                        Driver driver = new com.mysql.jdbc.Driver();
//////                        DriverManager.registerDriver(driver);
//////                    }catch (SQLException ex){
//////                        fieldHI.setText("Unable to load driver class!");
//////                        ex.printStackTrace();
//////                    }
////
////
////                    Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
////
////                    String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
////
////                    PreparedStatement statement = connection.prepareStatement(sql);
////                    statement.setString(1, username);
////                    statement.setString(2, password);
////
////                    int rowsInserted = statement.executeUpdate();
////
////
////                    statement.close();
////                    connection.close();
////
////                    fieldHI.setText("You registered successfully!");
////
////                    ClientView clientView = new ClientView();
////
////                } catch (SQLException ex) {
////                    ex.printStackTrace();
////                    fieldHI.setText("Error registering user.");
////
////                }
//            }
//
//        });
}
    @Override
    public synchronized void actionPerformed(ActionEvent e) {

    }

    private synchronized void printMsg(String msg) {//used in different threads
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {//works in thread of window
                fieldHI.setText(msg);
                fieldHI.setCaretPosition(fieldHI.getDocument().getLength());
            }
        });
    }

    public void onConnectionReady(TCPConnection tcpConnection) {
        System.out.println("Connection is working... ");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        System.out.println(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        System.out.println("Connection is not working ");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("Connection exception: " + e);
    }
}