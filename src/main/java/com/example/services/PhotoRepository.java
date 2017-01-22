package com.example.services;

import com.example.entities.Photo;
import com.example.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by noelaniekan on 1/4/17.
 */
public interface PhotoRepository extends CrudRepository<Photo, Integer> {
    List<Photo> findAllByRecipient(User receiver);
}


