package com.example.fermi.fermi.adapter;

/**
 * Created by Parsania Hardik on 11-May-17.
 */
/*
public class ChatModel {

    private String name;
    private String profile,lastmessage;


    public ChatModel(String name,String profile,String lastmessage) {
        this.name = name;
        this.profile = profile;
        this.lastmessage=lastmessage;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getLastmessage() {
        return lastmessage;
    }

    public void setLastmessage(String lastmessage) {
        this.lastmessage = lastmessage;
    }
}
*/


/**
 * Created by system1 on 30-08-2017.
 */

public class ChatModel {

    private String personname,personudid,chatmegalert,personemail;
    private String personprofile, personlastmessage,msg_Direction,typing_alert;
   private long messagetime;

    public ChatModel() {
        //messagetime = new Date().getTime();
        //default constructor required
    }
    public ChatModel(String personname,String personprofile,String personudid,String personemail,String chatmegalert,String personlastmessage,String msg_Direction,long messagetime) {
       this.chatmegalert = chatmegalert;
        this.messagetime=messagetime;
        this.personlastmessage=personlastmessage;
        this.msg_Direction=msg_Direction;
        this.personname=personname;
        this.personprofile=personprofile;
        this.personudid=personudid;
        this.personemail=personemail;
        this.typing_alert="";
        //default constructor required
    }

    public String getMsgDirection() {
        return msg_Direction;
    }

    public void setMsgDirection(String msg_Direction) {
        this.msg_Direction = msg_Direction;
    }

    public String getTyping_alert() {
        return typing_alert;
    }

    public void setMsg_Direction(String typing_alert) {
        this.typing_alert = typing_alert;
    }

    public String getPersonemail() {
        return personemail;
    }

    public void setPersonemail(String personemail) {
        this.personemail = personemail;
    }
    public String getPersonname() {
        return personname;
    }

    public void setPersonname(String personname) {
        this.personname = personname;
    }

    public String getPersonprofile() {
        return personprofile;
    }

    public void setPersonprofile(String personprofile) {
        this.personprofile = personprofile;
    }

    public String getPersonlastmessage() {
        return personlastmessage;
    }

    public void setPersonlastmessage(String personlastmessage) {
        this.personlastmessage = personlastmessage;
    }

    public String getPersonudid() {
        return personudid;
    }
    public void setPersonudid(String personudid) {
        this.personudid = personudid;
    }
    public long getMessagetime() {
        return messagetime;
    }
    public void setMessagetime(long messagetime) {
        this.messagetime = messagetime;
    }
    public String getChatmegalert() {
        return chatmegalert;
    }

    public void setChatmegalert(String chatmegalert) {
        this.chatmegalert = chatmegalert;
    }
}