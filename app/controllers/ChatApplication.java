package controllers;

import model.ChatMessage;
import play.Logger;
import play.data.Form;
import play.libs.EventSource;
import play.mvc.Controller;
import play.mvc.Result;
import services.ChatService;

public class ChatApplication extends Controller {

    /**
     * Keeps track of all connected browsers *
     */
    private static ChatService chatService = new ChatService();

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
        chatService.sendEvent(msg);
        return ok();
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
            return ok(new EventSource() {
                @Override
                public void onConnected() {
                    EventSource currentSocket = this;

                    this.onDisconnected(() -> {
                        Logger.info(remoteAddress + " - " + user + " SSE disconntected");
                        if (chatService.isUserOnline(user))
                            chatService.setUserOffline(user);
                    });
                    System.out.println(user + "connected");
                    chatService.setUserOnline(user, this);
                }
            });
    }


    /* update friends list of the users when the online user sockets vary */
    public static Result updateFriendList() {
        String remoteAddress = request().remoteAddress();
        Logger.info(remoteAddress + " - SSE conntected");
        chatService.updateChatList();
        return ok();
    }


}



