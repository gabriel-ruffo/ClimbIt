package com.example.operations.climbit;

import java.io.Serializable;

/**
 * Route.java
 * Purpose: This class contains the Route object that holds all information pertaining to a
 * rock climbing route. It is a simple getter/setter object that can easily be expanded.
 *
 * @author Gabriel Ruffo
 * @version 1.2
 * @since 07/19/2017
 */
class Route implements Serializable {
    private String name;
    private String grade;
    private String setter;
    private String start;
    private String finish;
    private float rating;
    private String feltLike;
    private String location;

    Route(String name, String grade, String setter, String start,
          String finish, float rating, String feltLike, String location) {
        this.name = name;
        this.grade = grade;
        this.setter = setter;
        this.start = start;
        this.finish = finish;
        this.rating = rating;
        this.feltLike = feltLike;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String getGrade() {
        return grade;
    }

    void setGrade(String grade) {
        this.grade = grade;
    }

    String getSetter() {
        return setter;
    }

    void setSetter(String setter) {
        this.setter = setter;
    }

    String getStart() {
        return start;
    }

    void setStart(String start) {
        this.start = start;
    }

    String getFinish() {
        return finish;
    }

    void setFinish(String finish) {
        this.finish = finish;
    }

    float getRating() {
        return rating;
    }

    void setRating(float rating) {
        this.rating = rating;
    }

    String getFeltLike() {
        return feltLike;
    }

    void setFeltLike(String feltLike) {
        this.feltLike = feltLike;
    }

    String getLocation() {
        return location;
    }

    void setLocation(String location) {
        this.location = location;
    }
}
