package com.fordexplorer.clique.data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "PersonId")
    private Person author;

    @Basic
    private String content;

    @Column(name = "Date", columnDefinition = "TIMESTAMP")
    private LocalDateTime date;

    public Message(Person author, String content) {
        this.author = author;
        this.content = content;
        this.date = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
