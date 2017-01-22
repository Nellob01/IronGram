package com.example.services;

import com.example.entities.User;
import org.springframework.data.repository.CrudRepository;


/**
 * Created by noelaniekan on 1/4/17.
 */
public interface UserRepository extends CrudRepository<User, Integer> {
    User findByName(String name);
}

