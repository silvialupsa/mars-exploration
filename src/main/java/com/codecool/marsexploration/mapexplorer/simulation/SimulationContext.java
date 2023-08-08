package com.codecool.marsexploration.mapexplorer.simulation;

import com.codecool.marsexploration.mapexplorer.exploration.ExplorationOutcome;
import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;
import com.codecool.marsexploration.mapexplorer.rovers.model.MarsRover;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimulationContext {
    private int numberOfSteps;
    private int timeoutSteps;
    private List<MarsRover> rovers;
    private Coordinate spaceshipLocation;
    private String map;
    private  HashMap<String, List<Coordinate>>monitoredResources;
    private ExplorationOutcome explorationOutcome;


    // Constructor
    public SimulationContext(int numberOfSteps, int timeoutSteps, List<MarsRover> rovers,
                             Coordinate spaceshipLocation, String map,
                             HashMap<String, List<Coordinate>> monitoredResources) {
        this.numberOfSteps = numberOfSteps;
        this.timeoutSteps = timeoutSteps;
        this.rovers = rovers;
        this.spaceshipLocation = spaceshipLocation;
        this.map = map;
        this.monitoredResources = monitoredResources;
    }

    // Getters and Setters (You can generate them automatically in most IDEs)

    public ExplorationOutcome getExplorationOutcome() {
        return explorationOutcome;
    }

    public void setExplorationOutcome(ExplorationOutcome explorationOutcome) {
        this.explorationOutcome = explorationOutcome;
    }

    public int getNumberOfSteps() {
        return numberOfSteps;
    }

    public int getTimeoutSteps() {
        return timeoutSteps;
    }

    public List <MarsRover> getRover() {
        return rovers;
    }

    public Coordinate getSpaceshipLocation() {
        return spaceshipLocation;
    }

    public String getMap() {
        return map;
    }

    public  HashMap<String, List<Coordinate>> getMonitoredResources() {
        return monitoredResources;
    }

    public int getNumberOfResources(){
        int amountOfResources = 0;
        for (List<Coordinate> resourceList : monitoredResources.values()) {
            amountOfResources += resourceList.size();
        }
        return amountOfResources;
    }

    public void setNumberOfSteps(int numberOfSteps) {
        this.numberOfSteps = numberOfSteps;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public void setMonitoredResources(HashMap<String, List<Coordinate>> monitoredResources) {
        this.monitoredResources = monitoredResources;
    }
}
