package com.paperboard.user;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class UserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    public UserRole() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
