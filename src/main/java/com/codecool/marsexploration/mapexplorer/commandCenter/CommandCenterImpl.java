package com.codecool.marsexploration.mapexplorer.commandCenter;

import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;

import java.util.HashMap;
import java.util.List;

public class CommandCenterImpl implements CommandCenter {
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

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public HashMap<String, List<Coordinate>> getResourcesOnStock() {
        return resourcesOnStock;
    }

    @Override
    public void setResourcesOnStock(HashMap<String, List<Coordinate>> resourcesOnStock) {
        this.resourcesOnStock.putAll(resourcesOnStock);
    }

    @Override
    public void incrementStatus() {
        this.status += 1;
    }


}
