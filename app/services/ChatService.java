package services;

import model.ChatMessage;
import model.ChatUser;
import model.SocketData;
import play.libs.EventSource;
import play.libs.Json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by Mani on 12/11/2014.
 */
public class ChatService {

    private HashMap<String, EventSource> chatSocketsOnline = new HashMap<String, EventSource>();

    public ChatService() {

    }

    /**
     * Update when user is Offline
     *
     * @param key
     */
    public void setUserOffline(String key) {

        chatSocketsOnline.remove(key);
    }

    /**
     * Update when user is Online
     *
     * @param key
     * @param value
     */
    public void setUserOnline(String key, EventSource value) {

        chatSocketsOnline.put(key, value);
    }

    /**
     * To check if the user is Online
     *
     * @param userId
     * @return
     */
    public boolean isUserOnline(String userId) {
        return chatSocketsOnline.containsKey(userId);
    }

    /**
     * Send event to the friend channel to whom the user is posting the message
     */
    public void sendEvent(ChatMessage msg) {

        try {
            SocketData chatMessage = new SocketData();
            chatMessage.eventName = "chatMessage";
            chatMessage.data = msg;
            String tofriend = msg.toFriend.value;
            String fromUser = msg.fromUser.value;

            if (chatSocketsOnline.containsKey(tofriend) && chatSocketsOnline.containsKey(fromUser)) {
                EventSource friendChat = chatSocketsOnline.get(tofriend);
                EventSource userChat = chatSocketsOnline.get(fromUser);
                friendChat.send(EventSource.Event.event(Json.toJson(chatMessage)));
                userChat.send(EventSource.Event.event(Json.toJson(chatMessage)));
            } else if (chatSocketsOnline.containsKey(fromUser)) {
                msg.text = "Hello! I'm not available right now! I'll get back later";
                EventSource userChat = chatSocketsOnline.get(msg.fromUser.value);
                msg.fromUser = msg.toFriend;
                msg.toFriend = msg.fromUser;
                userChat.send(EventSource.Event.event(Json.toJson(chatMessage)));

            } else {
                throw new Exception("User connections failed!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * update all the chat lists of the users online
     */
    public void updateChatList() {
        Set<String> onlineUserids = chatSocketsOnline.keySet();
        List<ChatUser> onlineUsersList = new ArrayList<ChatUser>();
        SocketData chatList = new SocketData();
        chatList.eventName = "chatList";
        chatList.data = onlineUsersList;
        for (EventSource onlineSocket : chatSocketsOnline.values()) {
            onlineSocket.send(EventSource.Event.event(Json.toJson(chatList)));
        }
    }
}
