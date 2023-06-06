package chat_server;

import chat_network.TCPConnection;
import chat_network.TCPConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ChatServer implements TCPConnectionListener {

    public static void main(String[] args){
        new ChatServer();
    }

    private static final ArrayList<TCPConnection> connections = new ArrayList<>();
    private static final ArrayList<String> users = new ArrayList<>();


    private static Map<String,TCPConnection> clientList = new HashMap<>() ;
    private static Map<String, Map<String, TCPConnection>> groups = new HashMap<>() ;
    private ChatServer(){
        System.out.println("Server is running...");
        try(ServerSocket serverSocket = new ServerSocket(1234)){
            while(true){
                try{
                    new TCPConnection(this, serverSocket.accept());//ждем соединение
                }catch (IOException e){
                    System.out.println("TCPConnection: " + e);
                }
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

// synchronized т.к. соединений будет много
    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        sendToAllConnections("User connected: " + tcpConnection);
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String value) {
        sendToAllConnections(value);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendToAllConnections("User disconnected: " + tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection: " + e);
    }

    private void sendToAllConnections(String value){
        System.out.println(value);
        int cnt = connections.size();
        for(int i = 0; i < cnt; i++){
            connections.get(i).sendString(value);
        }
    }

    public static synchronized void broadcast(String message, String excludeClient) {
        // Loop through the client list and broadcast the message
        clientList
                .forEach( (k, v) -> {
                            if (!k.equals(excludeClient)) { // exclude sender client
                                v.sendString(message);
                            }
                        }
                );
    }

    /**
     * Send personal message to a user (including yourself like in Messenger)
     */
    public synchronized void personalMsg(String message, String clientName, TCPConnection client) {
        if (isInClients(clientName)) { // Check if the client name is in the client list
            (clientList.get(clientName)).sendString(message);
        }
        else {
            client.sendString("User with such name does not exist!");
        }
    }

    /**
     * Creates a group (if not already created) and notifies the users
     */
    public static synchronized void createGroup(String groupName, TCPConnection client) {
        if ( ! groups.containsKey(groupName) ) { // If the group is not already created
            groups.put(groupName, new HashMap<String, TCPConnection>()) ;
            broadcast("Group: " + groupName + " created!", null);
        }
        else {
            client.sendString("Group already exists!");
        }
    }

    /**
     * Removes a group (if exists) and notifies the users
     */
    public static synchronized void removeGroup(String groupName, TCPConnection client) {
        if ( groups.containsKey(groupName) ) {
            groups.remove(groupName);
            broadcast("Group: " + groupName + " removed!", null);
        }
        else {
            client.sendString("Group does not exists!");
        }
    }

    /**
     * Lets clients join a group and notifies the group about the new member
     */
    public static synchronized void joinGroup(String groupName,String clientName, TCPConnection client) {
        if ( groups.containsKey(groupName) ) {
            if (!(groups.get(groupName)).containsKey(clientName)) {
                (groups.get(groupName)).put(clientName, client);
                client.sendString("You have joined the group!");
                sendToGroup(groupName, "User '" + clientName + "' has joined the group!", clientName, client);
            }
            else {
                client.sendString("You are already in the group");
            }
        }
        else {
            client.sendString("Such group does not exist!");
        }
    }

    /**
     * Sends given message to a particular group
     */
    public static synchronized void sendToGroup(String groupName, String message, String clientName, TCPConnection client) {
        if ( groups.containsKey(groupName) ) { // If the group exists
            if ( (groups.get(groupName)).containsKey(clientName) ) { // If the sender is actually a group member
                (groups.get(groupName))
                        .forEach( (k, v) -> {
                                    if (!k.equals(clientName)) v.sendString(message); //exclude sender client
                                }
                        );
            }
            else {
                client.sendString("You have to be in the group to send messages to the group members!");
            }
        }
        else {
            client.sendString("Such group does not exist!");
        }
    }

    /**
     * Adds client to the list of clients
     */
    public synchronized static void addUser(String clientName) {
        if (!clientName.isEmpty()) {
            users.add(clientName);
            //client.sendString("You have registered successfully!");
        }
        else {
            System.out.println("Please provide a name!");
        }
    }

    /**
     * Removes client from group and notifies the group members
     */
    public synchronized void leaveGroup(String groupName, String clientName, TCPConnection client) {
        if (groups.containsKey(groupName)) { // If the group exists
            if ((groups.get(groupName)).containsKey(clientName)) { // If the user is a member
                sendToGroup(groupName, "User '" + clientName + "' has left the group!", clientName, client);
                (groups.get(groupName)).remove(clientName);
                client.sendString("You have left the group!");
            }
            else {
                client.sendString("You cannot leave a group u haven't joined!");
            }
        }
        else {
            client.sendString("Such group does not exist!");
        }
    }

    /**
     * Removes client from the list of clients (no if-else statements
     * because every user needs to be registered so that they can send messages)
     */
    public static void unregister(String clientName) {
        clientList.remove(clientName);
    }


    /**
     * Returns the list of registered clients (users)
     */
    public synchronized Map<String, TCPConnection> getClientList() {
        return clientList;
    }

    /**
     * Returns groups hashmap
     */
    public static synchronized Set<String> getGroups() {
        return groups.keySet();
    }

    /**
     * Returns true if the client is already in the client list
     */
    public synchronized boolean isInClients(String clientName) {
        return clientList.containsKey(clientName);
    }


}
