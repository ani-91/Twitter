package com.oop.major;

import jakarta.persistence.*;

import java.time.LocalTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
@Entity
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int postID;

    private String postBody;

    @ManyToOne(fetch = FetchType.LAZY) // Many posts are created by one user
    @JoinColumn(name = "user_id") // Name of the foreign key column in the post table
    private Users postCreator;

    private LocalDate date;
    private LocalTime time;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments;

    public Post() {}

    public int getPostID() {
        return postID;
    }

    public void setPostID(int postID) {
        this.postID = postID;
    }

    public String getPostBody() {
        return postBody;
    }

    public void setPostBody(String postBody) {
        this.postBody = postBody;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return LocalTime.parse(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public Users getPostCreator() {
        return postCreator;
    }

    public void setPostCreator(Users postCreator) {
        this.postCreator = postCreator;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }






}
