package me.miki.shindo.management.remote.update;

import com.google.gson.JsonObject;
import me.miki.shindo.Shindo;
import me.miki.shindo.utils.JsonUtils;
import me.miki.shindo.utils.Multithreading;
import me.miki.shindo.utils.network.HttpUtils;

public class Update {

    String updateLink = "https://shindoclient.github.io/";
    String updateVersionString = "something is broken lmao";
    int updateBuildID = 0;

    public void setUpdateLink(String in){
        this.updateLink = in;
    }
    public String getUpdateLink(){
        return updateLink;
    }

    public void setVersionString(String in){
        this.updateVersionString = in;
    }
    public String getVersionString(){
        return updateVersionString;
    }

    public void setBuildID(int in){this.updateBuildID = in;}
    public int getBuildID(){
        return updateBuildID;
    }

    public void check(){
        try{
            Multithreading.runAsync(this::checkUpdates);
        } catch (Exception ignored){}
    }

    public void checkForUpdates(){
        Shindo g = Shindo.getInstance();
        if (g.getVersionIdentifier() < this.updateBuildID){
            g.setUpdateNeeded(true);
        }
    }

    private void checkUpdates() {
        JsonObject jsonObject = HttpUtils.readJson("https://shindoclient.github.io/data/meta/client.json", null);
        if (jsonObject != null) {
            setUpdateLink(JsonUtils.getStringProperty(jsonObject, "updatelink", "https://shindoclient.github.io/"));
            setVersionString(JsonUtils.getStringProperty(jsonObject, "latestversionstring", "something is broken lmao"));
            setBuildID(JsonUtils.getIntProperty(jsonObject, "latestversion", 0));
            checkForUpdates();
        }
    }

}
