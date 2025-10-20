package org.com.code.certificateProcessor.pojo;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Student {

  private long id;
  private String studentId;
  private String password;
  private String name;
  private String college;
  private String major;
  private String auth;
  private LocalDate createdAt;
  private LocalDate updatedAt;

  private Double sumOfScore;
}
