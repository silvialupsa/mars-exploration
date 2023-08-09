package com.codecool.marsexploration.mapexplorer.rovers.model;

import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;

import java.util.HashMap;
import java.util.List;

public class MarsRover {
    private Coordinate currentPosition;
    private final int sight;
    private final HashMap<String, List<Coordinate>> resources;

    private int id;
    private String name;

    private static int counter = 0;

    public int getSight() {
        return sight;
    }

    public HashMap<String, List<Coordinate>> getResources() {
        return resources;
    }

    public void setResources(HashMap<String, List<Coordinate>> resources) {
        this.resources.clear();
        this.resources.putAll(resources);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static int getCounter() {
        return counter;
    }

    public static void setCounter(int counter) {
        MarsRover.counter = counter;
    }

    public MarsRover(Coordinate currentPosition, int sight, HashMap<String, List<Coordinate>> resources) {
        this.id = counter;
        counter++;
        if(counter == 3){
            counter = 0;
        }
        this.name = "rover-" + id;
        this.currentPosition = currentPosition;
        this.sight = sight;
        this.resources = resources;
    }

    public String getNamed() {
        return name;
    }

    public Coordinate getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Coordinate currentPosition) {
        this.currentPosition = currentPosition;
    }
}
