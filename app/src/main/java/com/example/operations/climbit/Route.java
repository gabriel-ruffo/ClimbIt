package com.example.operations.climbit;

import java.io.Serializable;

/**
 * Route.java
 * Purpose: This class contains the Route object that holds all information pertaining to a
 * rock climbing route. It is a simple getter/setter object that can easily be expanded.
 *
 * @author Gabriel Ruffo
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
    private String image;

    Route(String name, String grade, String setter, String start,
          String finish, float rating, String feltLike, String location, String image) {
        this.name = name;
        this.grade = grade;
        this.setter = setter;
        this.start = start;
        this.finish = finish;
        this.rating = rating;
        this.feltLike = feltLike;
        this.location = location;
        this.image = image;
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

    String getSetter() {
        return setter;
    }

    String getStart() {
        return start;
    }

    String getFinish() {
        return finish;
    }

    float getRating() {
        return rating;
    }

    String getFeltLike() {
        return feltLike;
    }

    String getLocation() {
        return location;
    }

    String getImage() {
        return image;
    }
}
