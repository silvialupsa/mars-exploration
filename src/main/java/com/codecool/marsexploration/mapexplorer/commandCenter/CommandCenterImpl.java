package com.codecool.marsexploration.mapexplorer.commandCenter;

import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;

import java.util.HashMap;
import java.util.List;

public class CommandCenterImpl implements CommandCenter{
    private int id;
    private Coordinate location;
    private int status;
    private HashMap<String, List<Coordinate>> resourcesOnStock;

    public CommandCenterImpl(int id, Coordinate location, int status, HashMap<String, List<Coordinate>> resourcesOnStock) {
        this.id = id;
        this.location = location;
        this.status = status;
        this.resourcesOnStock = resourcesOnStock;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Coordinate getLocation() {
        return location;
    }

    public void setLocation(Coordinate location) {
        this.location = location;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public HashMap<String, List<Coordinate>> getResourcesOnStock() {
        return resourcesOnStock;
    }

    public void setResourcesOnStock(HashMap<String, List<Coordinate>> resourcesOnStock) {
        this.resourcesOnStock = resourcesOnStock;
    }


}
