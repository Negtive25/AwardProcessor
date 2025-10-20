package org.com.code.certificateProcessor.pojo;

import lombok.*;

import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StandardAward {

  private long id;
  private String standardAwardId;
  private String name;
  private String category;
  private String level;
  private double score;
  private long isActive;
  private long createdBy;
  private long updatedBy;
  private LocalDate createdAt;
  private LocalDate updatedAt;

  public Map<String, Object> toMap() {
    return Map.of(
        "standardAwardId", standardAwardId,
        "name", name,
        "category", category,
        "level", level,
        "score", score,
        "isActive", isActive,
        "createdBy", createdBy,
        "updatedBy", updatedBy,
        "createdAt", createdAt,
        "updatedAt", updatedAt
    );
  }
}
