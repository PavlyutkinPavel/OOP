package chat_client;

import chat_enty.ChatHistory;
import chat_network.TCPConnection;
import chat_network.TCPConnectionListener;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static chat_enty.ChatHistory.appendMessage;

public class ClientView extends JFrame implements ActionListener, TCPConnectionListener {

    private static final String IP_ADDR = "127.0.0.1";
    private static final int PORT = 1234;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;


    public TCPConnection connection;

    private final JTextArea log = new JTextArea();//поле ввода
    public JTextField fieldNickname = new JTextField("UserDefault...");
    private final JTextField fieldInput = new JTextField();

    private JTextArea chats = new JTextArea();


    public ClientView(String nickname) {
        this.fieldNickname.setText(nickname);
        try {
            connection = new TCPConnection(this, IP_ADDR, PORT);
        } catch (IOException e) {
            System.out.println("ТЫ КОНЧЕННЫЙ");
            printMsg("Connection exception: " + e);
        }
        JSONParser parser = new JSONParser();
        try {
            JSONObject obj = (JSONObject) parser.parse(new FileReader("users.json"));
            JSONArray userArray = (JSONArray) obj.get("users");
            ArrayList<String> usernames = new ArrayList<>();
            for (Object o : userArray) {
                JSONObject user = (JSONObject) o;
                String username = (String) user.get("username");
                usernames.add(username);
                //fieldNickname.setText(username);

            }
            // теперь мы можем использовать переменную usernames для доступа к именам всех пользователей в файле
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException e) {
            throw new RuntimeException(e);
        }
        ChatHistory chatHistory = new ChatHistory();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setTitle("PASHAGRAM");
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);

        // Создаем компоновщик BorderLayout
        setLayout(new BorderLayout());

        log.setEditable(false);
        log.setLineWrap(true);
        log.setText("Main chat: ");
        ArrayList<String> chat = (ArrayList<String>) chatHistory.readMessages();

//        for (String message : chat) {
//            log.append(message + "\n");
//        }

        add(log, BorderLayout.CENTER);

        fieldInput.setSize(600, 200);
        fieldInput.addActionListener(this);
        add(fieldInput, BorderLayout.SOUTH);

        add(fieldNickname, BorderLayout.NORTH);


        chats.setSize(100, 200);
        chats.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(chats, BorderLayout.WEST);
        chats.setText("Chats and groups:\nMain Chat(active)");

        setVisible(true);

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = fieldInput.getText();
        if (msg.equals("")) {
            return;
        }
        fieldInput.setText(null);
        connection.sendString(fieldNickname.getText() + ": " + msg);
    }


    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMsg("\nConnection is working... ");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        printMsg(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMsg("Connection is not working ");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printMsg("Connection exception: " + e);
    }

    private synchronized void printMsg(String msg) {//used in different threads
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {//works in thread of window
                if (!msg.contains("User")||!msg.contains("Connection")) {
                    log.append(msg + "\n");
                    log.setCaretPosition(log.getDocument().getLength());
                    appendMessage(fieldNickname.getText(), msg);
                }
            }
        });
    }

    //    public static void main(String[] args) {
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                new ClientView();//в потоке edtt
//            }
//        });
//    }
}
/*import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;
import java.net.*;
import java.io.*;

public class ClientView implements ActionListener, Runnable {

    JTextField text;
    JPanel a1;
    static Box vertical = Box.createVerticalBox();
    static JFrame f = new JFrame();
    static DataOutputStream dout;

    BufferedReader reader;
    BufferedWriter writer;
    String name = "Pashok";

    ClientView() {

        f.setLayout(null);

        JPanel p1 = new JPanel();
        p1.setBackground(new Color(7, 94, 84));
        p1.setBounds(0, 0, 450, 70);
        p1.setLayout(null);
        f.add(p1);

        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/3.png"));
        Image i2 = i1.getImage().getScaledInstance(25, 25, Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel back = new JLabel(i3);
        back.setBounds(5, 20, 25, 25);
        p1.add(back);

        back.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent ae) {
                System.exit(0);
            }
        });

        ImageIcon i4 = new ImageIcon(ClassLoader.getSystemResource("icons/mirzapur.png"));
        Image i5 = i4.getImage().getScaledInstance(60,60, Image.SCALE_DEFAULT);
        ImageIcon i6 = new ImageIcon(i5);
        JLabel profile = new JLabel(i6);
        profile.setBounds(40, 5, 60, 60);
        p1.add(profile);

        ImageIcon i7 = new ImageIcon(ClassLoader.getSystemResource("icons/video.png"));
        Image i8 = i7.getImage().getScaledInstance(30, 30, Image.SCALE_DEFAULT);
        ImageIcon i9 = new ImageIcon(i8);
        JLabel video = new JLabel(i9);
        video.setBounds(300, 20, 30, 30);
        p1.add(video);

        ImageIcon i10 = new ImageIcon(ClassLoader.getSystemResource("icons/phone.png"));
        Image i11 = i10.getImage().getScaledInstance(35, 30, Image.SCALE_DEFAULT);
        ImageIcon i12 = new ImageIcon(i11);
        JLabel phone = new JLabel(i12);
        phone.setBounds(360, 20, 35, 30);
        p1.add(phone);

        ImageIcon i13 = new ImageIcon(ClassLoader.getSystemResource("icons/3icon.png"));
        Image i14 = i13.getImage().getScaledInstance(10, 25, Image.SCALE_DEFAULT);
        ImageIcon i15 = new ImageIcon(i14);
        JLabel morevert = new JLabel(i15);
        morevert.setBounds(420, 20, 10, 25);
        p1.add(morevert);

        JLabel name = new JLabel("Mirzapur");
        name.setBounds(110, 15, 100, 18);
        name.setForeground(Color.WHITE);
        name.setFont(new Font("SAN_SERIF", Font.BOLD, 18));
        p1.add(name);

        JLabel status = new JLabel("Kaleen, Guddu, Bablu, Sweety, IG Dubey, Shukla");
        status.setBounds(110, 35, 160, 18);
        status.setForeground(Color.WHITE);
        status.setFont(new Font("SAN_SERIF", Font.BOLD, 14));
        p1.add(status);

        a1 = new JPanel();
        a1.setBounds(5, 75, 440, 570);
        a1.setBackground(Color.WHITE);
        f.add(a1);

        text = new JTextField();
        text.setBounds(5, 655, 310, 40);
        text.setFont(new Font("SAN_SERIF", Font.PLAIN, 16));
        f.add(text);

        JButton send = new JButton("Send");
        send.setBounds(320, 655, 123, 40);
        send.setBackground(new Color(7, 94, 84));
        send.setForeground(Color.WHITE);
        send.addActionListener(this);
        send.setFont(new Font("SAN_SERIF", Font.PLAIN, 16));
        f.add(send);

        f.setSize(450, 700);
        f.setLocation(20, 50);
        f.setUndecorated(true);
        f.getContentPane().setBackground(Color.WHITE);

        f.setVisible(true);

        try {
            Socket socket = new Socket("localhost", 2003);
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent ae) {
        try {
            String out = "<html><p>" + name + "</p><p>" + text.getText() + "</p></html>";

            JPanel p2 = formatLabel(out);

            a1.setLayout(new BorderLayout());

            JPanel right = new JPanel(new BorderLayout());
            right.setBackground(Color.WHITE);
            right.add(p2, BorderLayout.LINE_END);
            vertical.add(right);
            vertical.add(Box.createVerticalStrut(15));

            a1.add(vertical, BorderLayout.PAGE_START);

            try {
                writer.write(out);
                writer.write("\r\n");
                writer.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }

            text.setText("");

            f.repaint();
            f.invalidate();
            f.validate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JPanel formatLabel(String out) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel output = new JLabel("<html><p style=\"width: 150px\">" + out + "</p></html>");
        output.setFont(new Font("Tahoma", Font.PLAIN, 16));
        output.setBackground(new Color(37, 211, 102));
        output.setOpaque(true);
        output.setBorder(new EmptyBorder(0, 15, 0, 50));

        panel.add(output);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        JLabel time = new JLabel();
        time.setText(sdf.format(cal.getTime()));

        panel.add(time);

        return panel;
    }

    public void run() {
        try {
            String msg = "";
            while(true) {
                msg = reader.readLine();
                if (msg.contains(name)) {
                    continue;
                }

                JPanel panel = formatLabel(msg);

                JPanel left = new JPanel(new BorderLayout());
                left.setBackground(Color.WHITE);
                left.add(panel, BorderLayout.LINE_START);
                vertical.add(left);

                a1.add(vertical, BorderLayout.PAGE_START);

                f.repaint();
                f.invalidate();
                f.validate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ClientView one = new ClientView();
        Thread t1 = new Thread(one);
        t1.start();
    }
}
*/