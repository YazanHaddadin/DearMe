package com.me.dear;

public class VoiceNotesInfo {
    private String name,price;
    private int imageId;
    public VoiceNotesInfo(){}

    public VoiceNotesInfo(String name, String price, int imageId){
        this.name = name;
        this.price = price;
        this.imageId = imageId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
