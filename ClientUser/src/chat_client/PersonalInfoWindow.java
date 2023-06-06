package chat_client;

import chat_enty.User;
import chat_storage.DataBaseConnector;
import com.formdev.flatlaf.FlatLightLaf;
import mdlaf.MaterialLookAndFeel;
import mdlaf.themes.MaterialLiteTheme;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class PersonalInfoWindow {
    private JFrame frame;
    private String password = "******";

    public PersonalInfoWindow(String username) throws SQLException {
        try {
            // Установка Material UI Look and Feel
            UIManager.setLookAndFeel(new MaterialLookAndFeel(new MaterialLiteTheme()));
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
//        JSONParser parser = new JSONParser();
//        try {
//            JSONObject obj = (JSONObject) parser.parse(new FileReader("users.json"));
//            JSONArray userArray = (JSONArray) obj.get("users");
//            ArrayList<String> usernames = new ArrayList<>();
//            for (Object o : userArray) {
//                JSONObject user = (JSONObject) o;
//                username = (String) user.get("username");
//                usernames.add(username);
//            }
//            // теперь мы можем использовать переменную usernames для доступа к именам всех пользователей в файле
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (org.json.simple.parser.ParseException e) {
//            throw new RuntimeException(e);
//        }

        DataBaseConnector connector = new DataBaseConnector("jdbc:postgresql://localhost:5432/postgres", "postgres", "4864");
        connector.connect();
        User user = connector.getUser(username);
        frame = new JFrame("Personal Info");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(10, 10, 10, 10);

        // Заголовок
        JLabel titleLabel = new JLabel("PASHAGRAM USER");
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 24));
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        frame.add(titleLabel, constraints);

        // Имя пользователя
        JLabel usernameLabel = new JLabel("Имя пользователя:");
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        frame.add(usernameLabel, constraints);

        JLabel usernameValueLabel = new JLabel(username);
        constraints.gridx = 1;
        constraints.gridy = 1;
        frame.add(usernameValueLabel, constraints);

        // Пароль пользователя
        JLabel passwordLabel = new JLabel("Пароль:");
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        frame.add(passwordLabel, constraints);

        JLabel passwordValueLabel = new JLabel(user.getPassword());
        constraints.gridx = 1;
        constraints.gridy = 2;
        frame.add(passwordValueLabel, constraints);

        frame.setVisible(true);



    }
}

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                new PersonalInfoWindow(String username);
//            }
//        });
//    }
//}
