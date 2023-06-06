package chat_client;

import chat_enty.User;
//import usingSpring.SpringConfig;
import chat_storage.DataBaseConnector;
import mdlaf.MaterialLookAndFeel;
import mdlaf.themes.MaterialLiteTheme;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.postgresql.Driver;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.Arrays;

import static chat_server.ChatServer.addUser;


public class RegistrationWindow {
    private JFrame frame;
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;

    public RegistrationWindow() {
        try {
            // Установка Material UI Look and Feel
            UIManager.setLookAndFeel(new MaterialLookAndFeel(new MaterialLiteTheme()));
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        //ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
        frame = new JFrame("PASHAGRAM");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(10, 10, 10, 10);

        // Заголовок
        JLabel titleLabel = new JLabel("                   Registration");
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 24));
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        frame.add(titleLabel, constraints);

        // Поле ввода имени
        JLabel nameLabel = new JLabel("Name:");
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        frame.add(nameLabel, constraints);

        nameField = new JTextField();
        nameField.setColumns(25); // Устанавливаем размер поля ввода
        constraints.gridx = 1;
        constraints.gridy = 1;
        frame.add(nameField, constraints);

        // Поле ввода почты
        JLabel emailLabel = new JLabel("Email:");
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        frame.add(emailLabel, constraints);

        emailField = new JTextField();
        emailField.setColumns(25); // Устанавливаем размер поля ввода
        constraints.gridx = 1;
        constraints.gridy = 2;
        frame.add(emailField, constraints);

        // Поле ввода пароля
        JLabel passwordLabel = new JLabel("Password:");
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        frame.add(passwordLabel, constraints);

        passwordField = new JPasswordField();
        passwordField.setColumns(25); // Устанавливаем размер поля ввода
        constraints.gridx = 1;
        constraints.gridy = 3;
        frame.add(passwordField, constraints);

        // Кнопка "Зарегистрироваться"
        JButton registerButton = new JButton("SIGN UP");
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = nameField.getText();
                String email = emailField.getText();
                String pass = Arrays.toString(passwordField.getPassword());
                String password = pass.replaceAll("[\\[\\],\\s]", "");
                JSONObject obj = null;
                int JsonORDb = 2;//1-json, 2-db;
                User mainUser = new User(username, password);
                switch (JsonORDb){
                    case 1:
                        // Здесь можно добавить логику обработки регистрации
//
//                if (username == "" || username == null) {
//                    JOptionPane.showMessageDialog(null, "Заполните поле имя!");
//                    return;
//                }
//                if (password == "" || password == null) {
//                    JOptionPane.showMessageDialog(null, "Заполните поле пароль!");
//                    return;
//                }
//                if (email == "" || email == null) {
//                    JOptionPane.showMessageDialog(null, "Заполните поле почта!");
//                    return;
//                }

                        // Выводим информацию для проверки
                        System.out.println("Имя: " + username);
                        System.out.println("Почта: " + email);
                        System.out.println("Пароль: " + password);

                        //if ((username != null) && (password == null) && (email != null)) {
                        JSONParser parser = new JSONParser();
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
                                        JOptionPane.showMessageDialog(null, "There is already user with such a name!");
                                        //ClientView clientView1 = new ClientView(username);
                                        break;
                                    }
                                }
                            }
                            // If user does not exist, create new user object and add to array
                            if (!userExists) {
                                JSONObject newUser = new JSONObject();
                                newUser.put("username", username);
                                newUser.put("password", password);
                                userArray.add(newUser);
                                // Write updated array to file
                                try (FileWriter file = new FileWriter("users.json")) {
                                    file.write(obj.toJSONString());
                                    file.flush();
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                                JOptionPane.showMessageDialog(null, "You signed up successfully!");
                                MessengerApp messengerApp2 = new MessengerApp(username);
                                //ClientView clientView2 = new ClientView(username);
                                addUser(username);
                                User user2 = new User(username, password);
//                            UserService userService = context.getBean(UserService.class);
//                            userService.createUser(user2);
                                user2.addUserToFile();
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
                            JOptionPane.showMessageDialog(null, "You signed up successfully!");
                            User user = new User(username, password);
//                        UserService userService = context.getBean(UserService.class);
//                        userService.createUser(user);
                            //user.addUserToDatabase();
                            addUser(username);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        } catch (ParseException | SQLException ex) {
                            ex.printStackTrace();
                        }
//                }else if(email == null){
//                    System.out.println("ne zashlo");
//                }
                    case 2:

                        DataBaseConnector connector = new DataBaseConnector("jdbc:postgresql://localhost:5432/postgres", "postgres", "4864");
                        try{
                            Class.forName("org.postgresql.Driver");
                        }catch(ClassNotFoundException exception){
                            exception.printStackTrace();
                        }

                        try{
                            Driver driver = new org.postgresql.Driver();
                            DriverManager.registerDriver(driver);
                        }catch (SQLException ex){
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
                                JOptionPane.showMessageDialog(null, "You registered already, log in!");
                            }else{
                                connector.addUser(mainUser);
                                JOptionPane.showMessageDialog(null, "You registered successfully!");
                                MessengerApp app = new MessengerApp(username);
                            }
                        } catch (SQLException ex) {
                            System.out.println("Sql adding user error");
                            ex.printStackTrace();
                        }
                }

            }


        });

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        frame.add(registerButton, constraints);

        // Текст "Уже зарегистрированы?"
        JLabel signInLabel = new JLabel("                                                         Have account?");
        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        frame.add(signInLabel, constraints);

        // Ссылка "Войти"
        JLabel loginLinkLabel = new JLabel("LOG IN");
        loginLinkLabel.setForeground(Color.BLUE);
        loginLinkLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLinkLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                // Здесь можно добавить логику перехода на страницу входа
                LogWindow logWindow1 = new LogWindow();
                System.out.println("Переход на страницу входа...");
            }
        });
        constraints.gridx = 2;
        constraints.gridy = 5;
        constraints.gridwidth = 1;
        //constraints.anchor = GridBagConstraints.CENTER;
        frame.add(loginLinkLabel, constraints);

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new RegistrationWindow();
            }
        });
    }
}
