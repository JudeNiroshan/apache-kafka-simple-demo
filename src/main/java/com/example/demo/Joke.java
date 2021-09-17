package com.example.demo;

public class Joke {
    private String timestamp;
    private String joke;

    public Joke(String timestamp, String joke) {
        this.timestamp = timestamp;
        this.joke = joke;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getJoke() {
        return joke;
    }

    public void setJoke(String joke) {
        this.joke = joke;
    }
}
