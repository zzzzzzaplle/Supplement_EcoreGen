package com.paperboard.user;

import com.paperboard.review.ReviewAssignment;
import com.paperboard.review.Grade;

import java.io.Serializable;
import java.util.List;

public class Reviewer extends UserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<ReviewAssignment> assignments;

    public Reviewer() {
    }

    public List<ReviewAssignment> getReviewAssignments() {
        return assignments;
    }

    public void setReviewAssignments(List<ReviewAssignment> assignments) {
        this.assignments = assignments;
    }

    public void addReviewAssignment(ReviewAssignment assignment) {
        if (this.assignments == null) {
            this.assignments = new java.util.ArrayList<>();
        }
        this.assignments.add(assignment);
    }

    public int calculateUnsubmittedReviews() {
        if (assignments == null) {
            return 0;
        }
        int count = 0;
        for (ReviewAssignment a : assignments) {
            if (a.getGrade() == null || a.getGrade() == Grade.UNDECIDED) {
                count++;
            }
        }
        return count;
    }

    public double calculateSubmittedReviewAverageScore() {
        if (assignments == null) {
            return 0.0;
        }
        int count = 0;
        int total = 0;
        for (ReviewAssignment a : assignments) {
            if (a.getGrade() != null && a.getGrade() != Grade.UNDECIDED) {
                total += (a.getGrade() == Grade.ACCEPT) ? 1 : 0;
                count++;
            }
        }
        if (count == 0) {
            return 0.0;
        }
        return (double) total / count;
    }
}
