package model;

import play.db.ebean.Model;

/**
 * Created by Mani on 12/9/2014.
 */
public class ChatMessage extends Model {

    public String text;
    public ChatUser fromUser;
    public String time;
    public ChatUser toFriend;


}
