package com.paperboard.user;

import com.paperboard.paper.Paper;
import com.paperboard.review.Grade;

import java.io.Serializable;

public class CoChair extends UserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    public CoChair() {
    }

    public int makeFinalDecision(Paper paper, Grade decision) {
        if (paper == null) {
            return 0;
        }
        paper.setDecision(decision);
        return 1;
    }
}
