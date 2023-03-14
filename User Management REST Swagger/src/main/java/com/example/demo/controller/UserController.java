package com.example.demo.controller;

import com.example.demo.dto.UserDto;
import com.example.demo.dto.UserUpdateDto;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/user")
public class UserController {
  @Autowired private UserService userService;

  @GetMapping("/fetch/{username}")
  public ResponseEntity getUserById(@PathVariable String username) {
    return ResponseEntity.ok().body(userService.getUserByUsername(username));
  }

  @GetMapping("/fetchAll")
  public ResponseEntity getAllUsers() {
    return ResponseEntity.ok().body(userService.getAllUsers());
  }

  @DeleteMapping("/delete/{username}")
  public ResponseEntity deleteUserById(@PathVariable String username) {
    return ResponseEntity.ok().body(userService.deleteUserByUsername(username));
  }

  @PostMapping("/create")
  public ResponseEntity saveUser(@RequestBody UserDto user) {
    return ResponseEntity.ok().body(userService.saveUser(user));
  }

  @PutMapping("/update/{username}")
  public ResponseEntity updateUser(@PathVariable String username, @RequestBody UserUpdateDto user)
      throws IOException {
    return ResponseEntity.ok().body(userService.updateUser(username, user));
  }
}
