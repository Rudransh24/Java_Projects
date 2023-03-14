package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Table(name = "user_table")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements Serializable {
  private static final long serialVersionUID = -645834567897L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @NotBlank
  @Size(max = 50)
  @Email
  @Column(name = "email")
  private String email;

  @NotBlank
  @Size(max = 120)
  @Column(name = "username")
  private String username;

  @NotBlank
  @Size(max = 200)
  @Column(name = "name")
  private String name;

  @NotBlank
  @Size(max = 255)
  @Column(name = "age")
  private Integer age;

  @NotBlank
  @Size(max = 255)
  @Column(name = "location")
  private String location;
}
