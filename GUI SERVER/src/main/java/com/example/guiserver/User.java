package com.example.guiserver;

import java.io.Serializable;

public class User implements Serializable {

    public String Name;
    public String Status;

    public User(String name, String status) {
        Name = name;
        Status = status;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
