package com.example.entities;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by noelaniekan on 1/4/17.
 */
@Entity
@Table(name = "photo")
public class Photo {

    @Id
    @GeneratedValue
    int id;

    @ManyToOne
    User sender;

    @ManyToOne
    User recipient;

    @Column(nullable = false)
    String fileName;

    @Column(nullable = false)
    LocalDateTime createdTime;

    @Column(nullable = false)
    Integer timeToStoreFile;


    public Photo() {
    }

    public Photo(User sender, User recipient, String fileName, LocalDateTime createdTime, Integer timeToStoreFile) {
        this.sender = sender;
        this.recipient = recipient;
        this.fileName = fileName;
        this.createdTime = createdTime;
        this.timeToStoreFile = timeToStoreFile;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public Integer getTimeToStoreFile() {
        return timeToStoreFile;
    }

    public void setTimeToStoreFile(Integer timeToStoreFile) {
        this.timeToStoreFile = timeToStoreFile;
    }

}
