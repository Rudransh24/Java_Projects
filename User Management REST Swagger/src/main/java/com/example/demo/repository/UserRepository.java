package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
  User findUserByUsername(String username);

  void removeUserByUsername(String username);

  @Query(
      value =
          "UPDATE user_table SET email=:email, age=:age, location=:location, name=:name WHERE "
              + "username=:username",
      nativeQuery = true)
  @Modifying
  void updateUserDetails(
      @Param("email") String email,
      @Param("age") Integer age,
      @Param("location") String location,
      @Param("name") String name,
      @Param("username") String username);
}
