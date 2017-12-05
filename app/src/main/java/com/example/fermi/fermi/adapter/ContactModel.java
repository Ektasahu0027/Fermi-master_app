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

public class ContactModel {

    private String personname,personemail,personudid;
    private String personprofile, Status;


    public ContactModel() {
       // this.typing_alert="";
    }



    public String getPersonudid() {
        return personudid;
    }
    public void setPersonudid(String personudid) {
        this.personudid = personudid;
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

    public String getPersonemail() {
        return personemail;
    }

    public void setPersonemail(String personemail) {
        this.personemail = personemail;
    }



    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }
}