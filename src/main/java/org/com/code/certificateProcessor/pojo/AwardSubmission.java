package org.com.code.certificateProcessor.pojo;

import lombok.*;
import org.com.code.certificateProcessor.pojo.enums.AwardSubmissionStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AwardSubmission {

  private long id;
  private String submissionId;
  private String studentId;
  private String imageUrl;
  private AwardSubmissionStatus status;
  private Map<String, Object> ocrFullText;
  private String matchedAwardId;
  private long finalScore;
  private String rejectionReason;
  private List<Map<String, Object>> aiSuggestion;
  private String duplicateCheckResult;
  private long reviewedBy;
  private LocalDate submittedAt;
  private LocalDate completedAt;
}
