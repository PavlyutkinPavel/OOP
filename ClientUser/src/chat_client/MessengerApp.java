package chat_client;

import chat_enty.ChatHistory;
import chat_network.TCPConnection;
import chat_network.TCPConnectionListener;
import chat_storage.DataBaseConnector;
import org.json.simple.parser.JSONParser;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static chat_enty.ChatHistory.appendMessage;
import static chat_server.ChatServer.*;

public class MessengerApp implements ActionListener, TCPConnectionListener {

    private static final String IP_ADDR = "127.0.0.1";
    private static final int PORT = 1234;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    public TCPConnection connection;
    private JFrame frame;
    private JLabel usernameLabel = new JLabel("User123");;
    private JButton avatarPanel = new JButton("INFO");
    private JTextField searchField;
    private JComboBox<String> chatComboBox;
    private final JButton createChatButton = new JButton("Create pinned");
    private final JButton deleteChatButton = new JButton("Delete pinned");

    private final JButton joinChatButton = new JButton("Join Chat");
    private JTextArea chatArea = new JTextArea("");
    private final JTextField messageField = new JTextField();
    private final JButton sendButton = new JButton("Send");

    private List<String> allChatNames;
    private List<String> filteredChatNames;

    public Set<String> allGroups = getGroups();

    public MessengerApp(String nickname) throws SQLException {

        this.usernameLabel.setText(nickname);
        try {
            connection = new TCPConnection(this, IP_ADDR, PORT);
        } catch (IOException e) {
            System.out.println("Check server part");
            printMsg("Connection exception: " + e);
        }
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        JSONParser parser = new JSONParser();

        DataBaseConnector connector = new DataBaseConnector("jdbc:postgresql://localhost:5432/postgres", "postgres", "4864");
        connector.connect();
        connector.getUser(nickname);

//        try {
//            JSONObject obj = (JSONObject) parser.parse(new FileReader("users.json"));
//            JSONArray userArray = (JSONArray) obj.get("users");
//            ArrayList<String> usernames = new ArrayList<>();
//            for (Object o : userArray) {
//                JSONObject user = (JSONObject) o;
//                String username = (String) user.get("username");
//                usernames.add(username);
//                //fieldNickname.setText(username);
//
//            }
//            // теперь мы можем использовать переменную usernames для доступа к именам всех пользователей в файле
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (org.json.simple.parser.ParseException e) {
//            throw new RuntimeException(e);
//        }





        frame = new JFrame("PASHAGRAM");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Top panel for user info
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        userPanel.add(usernameLabel);
//        ImageIcon backgroundImage = new ImageIcon(ClassLoader.getSystemResource("icons/i.png"));
////        Image i5 = backgroundImage.getImage().getScaledInstance(50,50, Image.SCALE_DEFAULT);
//        avatarPanel = new JButton();
//        avatarPanel.setIcon(backgroundImage);
        avatarPanel.setPreferredSize(new Dimension(70, 50));
        avatarPanel.addActionListener(this);
        //avatarPanel.setBackground(UIManager.getColor("Button.background"));
        userPanel.add(avatarPanel);
        topPanel.add(userPanel, BorderLayout.WEST);

        // Search field with autocomplete
        allChatNames = new ArrayList<>();
        allChatNames.add("Main Chat");
        filteredChatNames = new ArrayList<>();
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        topPanel.add(searchField, BorderLayout.CENTER);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateFilteredChatNames();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateFilteredChatNames();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateFilteredChatNames();
            }
        });

        // Chat dropdown list
        chatComboBox = new JComboBox<>();
        //chatComboBox.add("Main Chat",
        updateChatList();
        chatComboBox.setPreferredSize(new Dimension(200, 30));
        topPanel.add(chatComboBox, BorderLayout.EAST);
        // Создать модель ComboBox для списка чатов
        DefaultComboBoxModel<String> chatComboBoxModel = new DefaultComboBoxModel<>();
        chatComboBox.setModel(chatComboBoxModel);

        topPanel.setBackground(UIManager.getColor("ToolBar.background"));
        frame.add(topPanel, BorderLayout.NORTH);

        // Left panel for chat list
        JPanel leftPanel = new JPanel();
        BoxLayout leftPanelLayout = new BoxLayout(leftPanel, BoxLayout.Y_AXIS);
        createChatButton.addActionListener(this);
        deleteChatButton.addActionListener(this);
        joinChatButton.addActionListener(this);
        leftPanel.setLayout(leftPanelLayout);
        leftPanel.add(createChatButton);
        leftPanel.add(deleteChatButton);
        //leftPanel.add(joinChatButton);
        leftPanel.setBackground(UIManager.getColor("ToolBar.background"));
        frame.add(leftPanel, BorderLayout.WEST);

        // Center panel for chat area
        JPanel centerPanel = new JPanel(new BorderLayout());
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        //chatArea.setText("Main chat:\n");
        centerPanel.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        centerPanel.setBackground(UIManager.getColor("TextArea.background"));
        frame.add(centerPanel, BorderLayout.CENTER);
        ChatHistory chatHistory = new ChatHistory();
        ArrayList<String> chat = (ArrayList<String>) chatHistory.readMessages();
        for(String message: chat){
            chatArea.append(message + "\n");
        }

        // Bottom panel for message input
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        sendButton.addActionListener(this);
        messageField.addActionListener(this);
        bottomPanel.setBackground(UIManager.getColor("ToolBar.background"));
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.getRootPane().setBorder(new EmptyBorder(10, 10, 10, 10));
        frame.getContentPane().setBackground(UIManager.getColor("Panel.background"));

        Color focusColor = UIManager.getColor("Button.background").brighter();

        loadChatList();

        searchField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                searchField.setBorder(BorderFactory.createLineBorder(focusColor));
            }

            @Override
            public void focusLost(FocusEvent e) {
                searchField.setBorder(BorderFactory.createLineBorder(UIManager.getColor("TextField.foreground")));
            }
        });

        messageField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                messageField.setBorder(BorderFactory.createLineBorder(focusColor));
            }

            @Override
            public void focusLost(FocusEvent e) {
                messageField.setBorder(BorderFactory.createLineBorder(UIManager.getColor("TextField.foreground")));
            }
        });

        frame.setVisible(true);
    }

    private void updateFilteredChatNames() {
        updateChatList();
    }

    private void loadChatList() {
        // Загрузить список чатов из файла или другого источника данных
        // Вместо этого кода используйте свою реализацию загрузки списка чатов
        // Обновить список чатов
        for (String groupName : allGroups) {
            chatComboBox.addItem(groupName); // Add each group to the combo box
        }

        updateChatList();
    }

    private void updateChatList() {
        // Очистить выпадающий список
        chatComboBox.removeAllItems();

        // Получить текст из поля поиска
        String searchText = searchField.getText().toLowerCase();

        // Фильтровать и добавлять чаты в выпадающий список
        for (String chatName : allGroups) {
            if (chatName.toLowerCase().contains(searchText)) {
                chatComboBox.addItem(chatName);
            }
        }
    }

//    private void deleteChatList() {
//        // Очистить выпадающий список
//        //chatComboBox.removeAllItems();
//
//        // Получить текст из поля поиска
//        String searchText = searchField.getText().toLowerCase();
//
//        // Фильтровать и добавлять чаты в выпадающий список
//        for (String chatName : allChatNames) {
//            if (chatName.toLowerCase().contains(searchText)) {
//                chatComboBox.removeItem(chatName);
//            }
//        }
//    }



    public static void main(String[] args) throws SQLException {
        SwingUtilities.invokeLater((Runnable) new MessengerApp("User123"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sendButton || e.getSource() == messageField) {
            String msg = messageField.getText();
            if (msg.equals("")) {
                return;
            }
            messageField.setText(null);
            connection.sendString(usernameLabel.getText() + ": " + msg);
        } else if (e.getSource() == createChatButton) {
            // Обработка события создания нового чата
            String newChatName = JOptionPane.showInputDialog(frame, "Enter pinned message:");
            if (newChatName != null && !newChatName.isEmpty()) {
                if(!allGroups.contains(newChatName)){
                    allChatNames.add(newChatName);
                    updateChatList();
                    //работа с сервером
                    createGroup(newChatName, connection);
                }else{
                    JOptionPane.showMessageDialog(null, "There is such a message already!");
                }
            }
        }else if (e.getSource() == deleteChatButton) {
            // Обработка события удаления существующего чата
            String oldChatName = JOptionPane.showInputDialog(frame, "Enter pinned message:");
            if (oldChatName != null && !oldChatName.isEmpty()) {
                if (allGroups.contains(oldChatName)) {
                    allChatNames.remove(oldChatName);
                    chatComboBox.removeItem(oldChatName);
                    updateChatList();
                    //работа с сервером
                    removeGroup(oldChatName, connection);
                } else {
                    JOptionPane.showMessageDialog(null, "There is  no such message!");
                }
            }
//        }else if (e.getSource() == joinChatButton) {
//            // Обработка события вступления в существующий чат
//            String chatName = JOptionPane.showInputDialog(frame, "Enter chat name:");
//            if (chatName != null && !chatName.isEmpty()) {
//                if(allGroups.contains(chatName)){
//                    //добавить в список чатов combobox мб
//                    updateChatList();
//                    //работа с сервером
//                    joinGroup(chatName, usernameLabel.getText(), connection);
//                }else{
//                    JOptionPane.showMessageDialog(null, "There is  no such chat!");
//                }
//            }
//        }
        }else if (e.getSource() == avatarPanel) {
            // Обработка открытия окна личной инфы
            try {
                PersonalInfoWindow personalInfoWindow = new PersonalInfoWindow(usernameLabel.getText());
            } catch (SQLException ex) {
                System.out.println("Sql exception");
            }
        }
    }



    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        System.out.println("\nConnection is working... ");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        printMsg(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        System.out.println("Connection is not working ");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printMsg("Connection exception: " + e);
    }

    private synchronized void printMsg(String msg) {//used in different threads
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {//works in thread of window
                if (!msg.contains("User")&&!msg.contains("Connection")&&!msg.contains("null")) {
                    chatArea.append(msg + "\n");
                    chatArea.setCaretPosition(chatArea.getDocument().getLength());
                    appendMessage(usernameLabel.getText(), msg);
                }
            }
        });
    }

}
