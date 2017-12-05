package com.example.fermi.fermi;

/**
 * Created by system1 on 30-08-2017.
 */

public class User {
    String name = "",email="",profile="",udid="";
    boolean hasAnswered = false, isHelper = false, maths = false, physics = false, chemistry = false, biology = false, arts = false, health = false, fitness = false;

    User() {
        //default constructor required
    }

    public boolean isHasAnswered() {
        return hasAnswered;
    }

    public void setHasAnswered(boolean hasAnswered) {
        this.hasAnswered = hasAnswered;
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
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getUdid() {
        return udid;
    }
    public void setUdid(String udid) {
        this.udid = udid;
    }

    public boolean isHelper() {
        return isHelper;
    }

    public void setHelper(boolean isHelper) {
        this.isHelper = isHelper;
    }

    public boolean isMaths() {
        return maths;
    }

    public void setMaths(boolean maths) {
        this.maths = maths;
    }

    public boolean isPhysics() {
        return physics;
    }

    public void setPhysics(boolean physics) {
        this.physics = physics;
    }

    public boolean isChemistry() {
        return chemistry;
    }

    public void setChemistry(boolean chemistry) {
        this.chemistry = chemistry;
    }

    public boolean isBiology() {
        return biology;
    }

    public void setBiology(boolean biology) {
        this.biology = biology;
    }

    public boolean isArts() {
        return arts;
    }

    public void setArts(boolean arts) {
        this.arts = arts;
    }

    public boolean isHealth() {
        return health;
    }

    public void setHealth(boolean health) {
        this.health = health;
    }

    public boolean isFitness() {
        return fitness;
    }

    public void setFitness(boolean fitness) {
        this.fitness = fitness;
    }
}
