package com.techinterview.elementz.helpers;

import java.io.Serializable;

/**
 * To be used as model containing properties for items populated to List
 */
public class Users implements Serializable{

    private String name;
    private String lastName;
    private String birthday;
    private String sex;
    private String image;

    public Users(){

    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public String getFirstName() {
        return name;
    }

    public void setFirstName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
