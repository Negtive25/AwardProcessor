package org.com.code.certificateProcessor.pojo.entity;

import lombok.*;

import java.time.LocalDate;

@Builder
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
