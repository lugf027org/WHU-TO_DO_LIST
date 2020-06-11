package model;

import java.io.Serializable;

public class Person implements Serializable{
    private String name;
    private String password;

    public Person(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
