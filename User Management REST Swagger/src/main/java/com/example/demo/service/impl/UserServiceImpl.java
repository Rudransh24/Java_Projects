package com.example.demo.service.impl;

import com.example.demo.dto.UserDto;
import com.example.demo.dto.UserUpdateDto;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

  @Autowired private UserRepository userRepository;

  @Override
  public User getUserByUsername(String username) {
    return userRepository.findUserByUsername(username);
  }

  @Override
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  @Override
  @Transactional
  public String deleteUserByUsername(String username) {
    userRepository.removeUserByUsername(username);
    return "User deleted successfully";
  }

  @Override
  public String saveUser(UserDto user) {
    User userEntity =
        User.builder()
            .username(user.getUsername())
            .age(user.getAge())
            .email(user.getEmail())
            .location(user.getLocation())
            .name(user.getName())
            .build();
    userRepository.save(userEntity);
    return "User added successfully";
  }

  @Override
  @Transactional
  public String updateUser(String username, UserUpdateDto user) throws IOException {
    try {
      User userCurrent = userRepository.findUserByUsername(username);
      userCurrent.setEmail(user.getEmail());
      userCurrent.setAge(user.getAge());
      userCurrent.setLocation(user.getLocation());
      userCurrent.setName(user.getName());
      userRepository.updateUserDetails(
          userCurrent.getEmail(),
          userCurrent.getAge(),
          userCurrent.getLocation(),
          userCurrent.getName(),
          username);
    } catch (Exception e) {
      throw new IOException("Username not found!");
    }

    return "User details updated successfully";
  }
}
