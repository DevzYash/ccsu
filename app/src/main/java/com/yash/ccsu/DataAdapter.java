package com.yash.ccsu;

public class DataAdapter {
    String Title, Subtitle, link;
    int image;

    public DataAdapter(String title, String subtitle, int image, String link) {
        Title = title;
        this.image = image;
        Subtitle = subtitle;
        this.link = link;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getSubtitle() {
        return Subtitle;
    }

    public void setSubtitle(String subtitle) {
        Subtitle = subtitle;
    }
}
