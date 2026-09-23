package com.paperboard.review;

import java.io.Serializable;

public class ReviewAssignment implements Serializable {

    private static final long serialVersionUID = 1L;

    private String feedback;

    private Grade grade;

    public ReviewAssignment() {
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }
}
