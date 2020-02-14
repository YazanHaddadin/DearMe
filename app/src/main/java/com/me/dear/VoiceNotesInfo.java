package com.me.dear;

public class VoiceNotesInfo {
    private String name,path;
    private int duration;

    public VoiceNotesInfo(){}

    public VoiceNotesInfo(String name, String path, int duration){
        this.name = name;
        this.path = path;
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
