package org.com.code.certificateProcessor.pojo.entity;

import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Admin {

  private long id;
  private String password;

  private String username;
  private String fullName;
  private String auth;
  private LocalDate createdAt;
  private LocalDate updatedAt;
}
