package chat_network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class TCPService {
    public static boolean isTCPPortOpen(String ipAddr, int port) {
        try {
            InetAddress address = InetAddress.getByName(ipAddr);
            Socket socket = new Socket(address, port);
            socket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
