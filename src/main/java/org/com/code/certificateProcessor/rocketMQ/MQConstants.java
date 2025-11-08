package org.com.code.certificateProcessor.rocketMQ;

public interface MQConstants {

    interface Topic {
        String SUBMISSION = "submissions";
    }

    interface Tag {
        String STUDENT_AWARD_SUBMISSION = "student-award-submission";
    }
    interface Producer {
        String SUBMISSION = "submission";
    }

    interface Consumer {
        String STUDENT_AWARD_SUBMISSION_CONSUMER = "student-award-submission-consumer";
    }
}

