package org.com.code.certificateProcessor.pojo;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Admin {

  private long id;
  private String username;
  private String password;
  private String fullName;
  private String auth;
  private LocalDate createdAt;
  private LocalDate updatedAt;
}
