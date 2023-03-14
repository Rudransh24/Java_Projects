package com.example.demo.service;

import com.example.demo.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    void saveUser(User user);
    User getUserById(long id);
    void deleteUserById(long id);
    Page<User> findPaginatedUser(int pageNo, int pageSize, String sortField, String sortDirection);
}
