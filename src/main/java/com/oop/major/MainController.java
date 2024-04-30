package com.oop.major;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.time.LocalDate;
import java.time.LocalTime;

import java.time.format.DateTimeFormatter;
import java.util.*;


@Controller
@RequestMapping

public class MainController {
    @Autowired
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public MainController(UserRepository userRepository, PostRepository postRepository, CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    @PostMapping(path = "/signup")
    public ResponseEntity<Object> addUser(@RequestBody Users request) {
        Optional<Users> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("Error", "Forbidden, Account already exists ");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } else {
            Users user = new Users();
            user.setName(request.getName());
            user.setPassword(request.getPassword());
            user.setEmail(request.getEmail());
            userRepository.save(user);
            return ResponseEntity.ok("Account Creation Successful");
        }
    }


    @PostMapping("/login")
    public ResponseEntity<Object> loginUser(@RequestBody Map<String, String> loginRequest) {
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");


        Optional<Users> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            Users user = existingUser.get();
            if (user.getPassword().equals(password)) {
                return ResponseEntity.ok("Login Successful");
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("Error", "Username/Password Incorrect");
                return ResponseEntity.badRequest().body(errorResponse);
            }
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("Error", "User does not exist");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<Users>> getAllUsers() {
        List<Users> userList = userRepository.findAll();
        List<Users> userResponseList = new ArrayList<>();

        for (Users user : userList) {
            Users userResponse = new Users();
            userResponse.setName(user.getName());
            userResponse.setUserID(user.getUserID());
            userResponse.setEmail(user.getEmail());
            userResponseList.add(userResponse);
        }

        for (Users user : userList) {
            user.setPassword(null);
        }
        return ResponseEntity.ok(userResponseList);
    }

    @GetMapping("/user")
    public ResponseEntity<Object> getUserDetails(@RequestParam("userID") int userID) {
        Optional<Users> userOptional = userRepository.findById((long) userID);

        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            Map<String, Object> userDetails = new HashMap<>();
            userDetails.put("name", user.getName());
            userDetails.put("userID", user.getUserID());
            userDetails.put("email", user.getEmail());
            return ResponseEntity.ok(userDetails);
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("Error", "User does not exist");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/")
    public ResponseEntity<Object> getUserFeed() {
        List<Post> allPosts = postRepository.findAllByOrderByDateDescTimeDesc();

        List<Map<String, Object>> postsResponse = new ArrayList<>();
        for (Post post : allPosts) {
            Map<String, Object> postMap = new HashMap<>();
            postMap.put("postID", post.getPostID());
            postMap.put("postBody", post.getPostBody());
            postMap.put("date", post.getDate());

            List<Map<String, Object>> commentList = new ArrayList<>();
            for (Comment comment : post.getComments()) {
                Map<String, Object> commentMap = new HashMap<>();
                commentMap.put("commentID", comment.getCommentID());
                commentMap.put("commentBody", comment.getCommentBody());


                Map<String, Object> commentCreator = new HashMap<>();
                Users creator = comment.getCommentCreator();
                commentCreator.put("userID", creator.getUserID());
                commentCreator.put("name", creator.getName());
                commentMap.put("commentCreator", commentCreator);

                commentList.add(commentMap);
            }
            postMap.put("comments", commentList);

            postsResponse.add(postMap);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("posts", postsResponse);

        return ResponseEntity.ok(response);

    }



    @PostMapping("/post")
    public ResponseEntity<Object> createPost(@RequestBody Map<String, Object> request) {
        String postBody = (String) request.get("postBody");
        int userID = (int) request.get("userID");

        Optional<Users> userOptional = userRepository.findById((long) userID);
        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            Post post = new Post();
            post.setPostBody(postBody);
            post.setPostCreator(user);
            LocalDate currentDate = LocalDate.now();
            LocalTime currentTime = LocalTime.parse(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            post.setDate(currentDate);
            post.setTime(currentTime);
            postRepository.save(post);
            return ResponseEntity.ok("Post created successfully");
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("Error", "User does not exist");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/post")
    public ResponseEntity<Object> getPost(@RequestParam("postID") int postId) {

        Optional<Post> postOptional = postRepository.findById((long) postId);

        if (postOptional.isPresent()) {
            Post post = postOptional.get();


            Map<String, Object> postResponse = new HashMap<>();
            postResponse.put("postID", post.getPostID());
            postResponse.put("postBody", post.getPostBody());
            postResponse.put("date", post.getDate());

            List<Map<String, Object>> commentList = new ArrayList<>();
            for (Comment comment : post.getComments()) {
                Map<String, Object> commentMap = new HashMap<>();
                commentMap.put("commentID", comment.getCommentID());
                commentMap.put("commentBody", comment.getCommentBody());
                Users commentCreator = comment.getCommentCreator();
                Map<String, Object> creatorMap = new HashMap<>();
                creatorMap.put("userID", commentCreator.getUserID());
                creatorMap.put("name", commentCreator.getName());
                commentMap.put("commentCreator", creatorMap);
                commentList.add(commentMap);
            }
            postResponse.put("comments", commentList);

            return ResponseEntity.ok(postResponse);
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("Error", "Post does not exist");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }


    @PatchMapping("/post")
    public ResponseEntity<Object> editPost(@RequestBody Map<String, Object> request) {
        int postId = (int) request.get("postID");
        String postBody = (String) request.get("postBody");

        Optional<Post> optionalPost = postRepository.findById((long) postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            post.setPostBody(postBody);
            postRepository.save(post);
            return ResponseEntity.ok("Post edited successfully");
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("Error", "Post does not exist");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @DeleteMapping("/post")
    public ResponseEntity<Object> deletePost(@RequestParam("postID") int postId) {
        Optional<Post> optionalPost = postRepository.findById((long) postId);
        if (optionalPost.isPresent()) {
            postRepository.deleteById((long) postId);
            return ResponseEntity.ok("Post deleted");
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("Error", "Post does not exist");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/comment")
    public ResponseEntity<Object> createComment(@RequestBody Map<String, Object> request) {
        String commentBody = (String) request.get("commentBody");
        int postId = (int) request.get("postID");
        int userId = (int) request.get("userID");

        Optional<Users> userOptional = userRepository.findById((long) userId);
        if (!userOptional.isPresent()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("Error", "User does not exist");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        Optional<Post> postOptional = postRepository.findById((long) postId);
        if (!postOptional.isPresent()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("Error", "Post does not exist");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        Comment comment = new Comment();
        comment.setCommentBody(commentBody);
        comment.setPost(postOptional.get());
        comment.setCommentCreator(userOptional.get());
        commentRepository.save(comment);

        return ResponseEntity.ok("Comment created successfully");
    }

    @GetMapping("/comment")
    public ResponseEntity<Object> getComment(@RequestParam("commentID") int commentID) {
        Optional<Comment> commentOptional = commentRepository.findById((long) commentID);

        if (commentOptional.isPresent()) {
            Comment comment = commentOptional.get();
            Map<String, Object> commentResponse = new HashMap<>();
            commentResponse.put("commentID", comment.getCommentID());
            commentResponse.put("commentBody", comment.getCommentBody());

            Users commentCreator = comment.getCommentCreator();
            Map<String, Object> commentCreatorInfo = new HashMap<>();
            commentCreatorInfo.put("userID", commentCreator.getUserID());
            commentCreatorInfo.put("name", commentCreator.getName());
            commentResponse.put("commentCreator", commentCreatorInfo);

            return ResponseEntity.ok(commentResponse);
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("Error", "Comment does not exist");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PatchMapping("/comment")
    public ResponseEntity<Object> editComment(@RequestBody Map<String, Object> request) {
        int commentId = (int) request.get("commentID");
        String commentBody = (String) request.get("commentBody");

        Optional<Comment> optionalComment = commentRepository.findById((long) commentId);
        if (optionalComment.isPresent()) {
            Comment comment = optionalComment.get();
            comment.setCommentBody(commentBody);
            commentRepository.save(comment);
            return ResponseEntity.ok("Comment edited successfully");
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("Error", "Comment does not exist");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @DeleteMapping("/comment")
    public ResponseEntity<Object> deleteComment(@RequestParam("commentID") int commentId) {

        Optional<Comment> optionalComment = commentRepository.findById((long) commentId);
        if (optionalComment.isPresent()) {
            commentRepository.deleteById((long) commentId);
            return ResponseEntity.ok("Comment deleted");
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("Error", "Comment does not exist");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }






}
