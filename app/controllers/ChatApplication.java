package controllers;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.ChatMap;
import model.ChatMessage;
import play.Logger;
import play.libs.Json;
import play.data.Form;
import play.libs.EventSource;
import play.mvc.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatApplication extends Controller {

    /** Keeps track of all connected browsers per room **/
    private static ChatMap<String, EventSource> chatSocketsOnline = new ChatMap<String, EventSource>();

    /**
     * Controller action serving AngularJS chat page
     */
    public static Result index() {
        return ok(views.html.index.render("Chat using Server Sent Events and AngularJS"));
    }

    /**
     * Controller action for POSTing chat messages
     */
    public static Result postMessage() {

        ChatMessage msg = Form.form(ChatMessage.class).bindFromRequest().get();
        sendEvent(msg);
        return ok();
    }

    /**
     * Send event to the friend channel to whom the user is posting the message
     */
    public static void sendEvent(ChatMessage msg) {


        String friend  = msg.friend;
        System.out.println(friend);
        if(chatSocketsOnline.containsKey(msg.friend) && chatSocketsOnline.containsKey(msg.chatUser.name)) {
            EventSource friendChat =(EventSource)chatSocketsOnline.get(friend);
            EventSource userChat =(EventSource)chatSocketsOnline.get(msg.chatUser.name);
            System.out.println(msg.chatUser.name);
            System.out.println(msg.chatUser.value);
            friendChat.send(EventSource.Event.event(Json.toJson(msg)));
            userChat.send(EventSource.Event.event(Json.toJson(msg)));
        }
        else
        {
            msg.text = "Hello! I'm not available right now! I'll get back later";
            EventSource friendChat =(EventSource)chatSocketsOnline.get(msg.chatUser.name);
            msg.chatUser.name = msg.friend;
            msg.chatUser.value = msg.friend;
            friendChat.send(EventSource.Event.event(Json.toJson(msg)));
        }
    }

    /**
     * Establish the SSE HTTP 1.1 connection.
     * The new EventSource socket is stored in the chatSocketsOnline Map
     * to keep track of users online and their sockets for the source of communication.
     *
     * onDisconnected removes the browser from the chatSocketsOnline Map if the
     * browser window has been exited.
     * @return
     */
    public static Result chatFeed(String user) {
            String remoteAddress = request().remoteAddress();
        Logger.info(remoteAddress + " - SSE conntected");

        if(!chatSocketsOnline.containsKey(user)) {
            return ok(new EventSource() {
                @Override
                public void onConnected() {
                    EventSource currentSocket = this;

                    this.onDisconnected(() -> {
                        Logger.info(remoteAddress + " - SSE disconntected");
                        if (chatSocketsOnline.containsKey(user))
                            chatSocketsOnline.remove(user);
                    });

                    System.out.println(user + "connected");
                    chatSocketsOnline.put(user, this);
                }
            });
        }
        else
            return ok();
    }
}
