package com.codecool.marsexploration.mapexplorer.simulation;

import com.codecool.marsexploration.mapexplorer.configuration.model.Configuration;
import com.codecool.marsexploration.mapexplorer.exploration.ExplorationOutcome;
import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;
import com.codecool.marsexploration.mapexplorer.rovers.model.MarsRover;

import java.util.List;

public class LackOfResourcesAnalyzer implements OutcomeAnalyzer {
    private static final double EXPLORED_PERCENTAGE_THRESHOLD = 0.10; // Adjust this threshold as needed

    @Override
    public boolean hasReachedOutcome(SimulationContext context, Configuration configuration) {
        context.setExplorationOutcome(ExplorationOutcome.ERROR);
        return isAlmostExplored(context, configuration) && noRightConditionsForColonization(context);
    }

    private boolean isAlmostExplored(SimulationContext context, Configuration configuration) {
//            int totalCoordinates = configuration.map().length() * configuration.map().length() ;
        int totalCoordinates = 33 * 33;
        int visitedCoordinates = context.getNumberOfSteps();
        return (double) visitedCoordinates / totalCoordinates >= EXPLORED_PERCENTAGE_THRESHOLD;
    }

    private boolean noRightConditionsForColonization(SimulationContext context) {
        return !(context.getMonitoredResources().containsKey("&")
                && context.getMonitoredResources().containsKey("#")
                && context.getMonitoredResources().containsKey("%")
                && context.getMonitoredResources().containsKey("*"));
    }
}

