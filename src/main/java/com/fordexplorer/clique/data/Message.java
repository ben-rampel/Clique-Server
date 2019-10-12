package com.fordexplorer.clique.data;

import jdk.vm.ci.meta.Local;

import java.time.LocalDateTime;
import java.util.Date;

public class Message {
    private Person author;
    private String content;
    private LocalDateTime date;

    public Message(Person author, String content) {
        this.author = author;
        this.content = content;
        this.date = LocalDateTime.now();
    }

    public Person getAuthor() {
        return author;
    }

    public void setAuthor(Person author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

}
