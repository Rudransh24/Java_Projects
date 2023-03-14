package com.example.demo.service;

import com.example.demo.dto.UserDto;
import com.example.demo.dto.UserUpdateDto;
import com.example.demo.entity.User;

import java.io.IOException;
import java.util.List;

public interface UserService {
  User getUserByUsername(String username);

  List<User> getAllUsers();

  String deleteUserByUsername(String username);

  String saveUser(UserDto user);

  String updateUser(String username, UserUpdateDto user) throws IOException;
}
