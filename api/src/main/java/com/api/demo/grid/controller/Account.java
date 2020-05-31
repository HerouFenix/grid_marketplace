package com.api.demo.grid.controller;


import com.api.demo.grid.dtos.UserDTO;
import com.api.demo.grid.exception.ExceptionDetails;
import com.api.demo.grid.models.User;
import com.api.demo.grid.pojos.UserUpdatePOJO;
import com.api.demo.grid.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@CrossOrigin
public class Account {

    @Autowired
    private UserService mUserService;


    @PostMapping("/grid/sign-up")
    public User createUser(@Valid @RequestBody UserDTO user) throws ExceptionDetails {

        return mUserService.saveUser(user);
    }

    @PostMapping("/grid/login")
    public ResponseEntity<User> login(@RequestHeader("Authorization") String auth) {

        String value = ControllerUtils.getUserFromAuth(auth);
        User user = mUserService.getUser(value);

        return ResponseEntity.ok().body(user);
    }

}
