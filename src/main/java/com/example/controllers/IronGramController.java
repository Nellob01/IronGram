package com.example.controllers;

import com.example.entities.Photo;
import com.example.entities.User;
import com.example.services.PhotoRepository;
import com.example.services.UserRepository;
import com.example.utilities.PasswordStorage;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.io.FileOutputStream;
import java.io.File;
import java.time.LocalDateTime;
import java.util.List;


/**
 * Created by noelaniekan on 1/4/17.
 */
@RestController  //this returns a serialized JSON object
public class IronGramController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PhotoRepository photoRepository;

    Server dbui = null;

    @PostConstruct
    public void init() throws SQLException {
        dbui = Server.createWebServer().start();
    }

    @PreDestroy
    public void preDestory() {
        dbui.stop();
    }


    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public User login(String userName, String password, HttpSession session, HttpServletResponse response) throws Exception {
        User user = userRepository.findByName(userName);
        if (user == null) {
            user = new User(userName, PasswordStorage.createHash(password));
            userRepository.save(user);
        } else if (!PasswordStorage.verifyPassword(password, user.getPasswordHash())){
            throw new Exception("Wrong Password");
        }

        session.setAttribute("userName", userName);
        response.sendRedirect("/");  //this redirects. may not need this for front end projects
        return user;
    }

    @RequestMapping(path = "/user", method = RequestMethod.GET)
    public User getUser(HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        return userRepository.findByName(userName);
    }

    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public Photo upload(MultipartFile photo, Integer timer,String recipient, HttpSession session, HttpServletResponse response) throws Exception {
        String userName = (String) session.getAttribute("userName");
        if (userName == null) {
            throw new Exception("Not logged in.");
        }

        //person Object that sent the photo
        User sender = userRepository.findByName(userName);
        //person Object that gets the photo
        User recipientObj = (recipient == null) ? sender : userRepository.findByName(recipient);

        if (recipientObj == null) throw new Exception("Recipient is not an IronGram member");

        //check to make sure we have photo
        if (!photo.getContentType().startsWith("image")) {
            throw new Exception("Images only please");
        }


        //all this creates a random file name
        File photoFile = File.createTempFile("image", photo.getOriginalFilename(), new File("public"));
        FileOutputStream fos = new FileOutputStream(photoFile);
        fos.write(photo.getBytes());

        Photo p = new Photo(sender, recipientObj, photoFile.getName(), LocalDateTime.now(), timer);

        photoRepository.save(p);

        response.sendRedirect("/");
        return p;
    }

    @RequestMapping(path = "/photos", method = RequestMethod.GET)
    public List<Photo> showPhotos(HttpSession session) {

        User user = userRepository.findByName((String) session.getAttribute("userName")); //logged in person

        List<Photo> photosInDbOld = (List<Photo>) photoRepository.findAll();  //all photos in DB

        for (Photo p : photosInDbOld) { //loop through them all
            LocalDateTime photoTime = p.getCreatedTime();   //time photo was created
            LocalDateTime timeNow = LocalDateTime.now();    //now

            //http://www.leveluplunch.com/java/examples/calculate-time-difference/
            long difference = java.time.Duration.between(photoTime, timeNow).getSeconds();  //difference of those things

            if (difference > p.getTimeToStoreFile()) {
                File photoFile = new File("public/" + p.getFileName());
                photoFile.delete();
                photoRepository.delete(p);

            }
        }
        return photoRepository.findAllByRecipient(user);
    }


    @RequestMapping(path = "/logout", method = RequestMethod.POST)
    public void logout(HttpSession session, HttpServletResponse response) throws IOException {
        session.invalidate();
        response.sendRedirect("/");
    }



}