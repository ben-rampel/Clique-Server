package com.fordexplorer.clique.controller;

import com.fordexplorer.clique.data.Location;

import java.io.Serializable;
import java.util.Objects;

public class GroupCreationRequest implements Serializable {

    private static final long serialVersionUID = 5L;

    private String name;
    private String description;
    private Location location;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupCreationRequest that = (GroupCreationRequest) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, location);
    }
}
