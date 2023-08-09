package com.codecool.marsexploration.mapexplorer.simulation;

import com.codecool.marsexploration.mapexplorer.configuration.model.Configuration;
import com.codecool.marsexploration.mapexplorer.exploration.ExplorationOutcome;
import com.codecool.marsexploration.mapexplorer.rovers.model.MarsRover;

public class SuccessAnalyzer implements OutcomeAnalyzer {
    @Override
    public boolean hasReachedOutcome(MarsRover rover, SimulationContext context, Configuration configuration) {
        context.setExplorationOutcome(rover, ExplorationOutcome.COLONIZABLE);
        int sizeOfMineral = context.getMonitoredResources().get("%") != null ? context.getMonitoredResources().get("%").size() : 0;
        int sizeOfWater = context.getMonitoredResources().get("*") != null ? context.getMonitoredResources().get("*").size() : 0;
        boolean checkIfWeHaveOneMountainAndOnePit = false;
        if (context.getMonitoredResources().containsKey("#") && context.getMonitoredResources().containsKey("&")) {
            checkIfWeHaveOneMountainAndOnePit = (context.getMonitoredResources().get("#").size() >= 1 && context.getMonitoredResources().get("&").size() >= 1);
        }
        boolean checkIfWeHaveMinimOneResourceFromEach = context.getMonitoredResources().containsKey("&")
                && context.getMonitoredResources().containsKey("#")
                && context.getMonitoredResources().containsKey("%")
                && context.getMonitoredResources().containsKey("*");

        boolean checkIfTheSizeOfMineralIsBiggerThan4AndTheSizeOfWaterIsBiggerThan3 = (sizeOfMineral > 4 && sizeOfWater > 3);

        context.setExplorationOutcome(rover, ExplorationOutcome.COLONIZABLE);
        return (checkIfWeHaveOneMountainAndOnePit
                || checkIfWeHaveMinimOneResourceFromEach
                || checkIfTheSizeOfMineralIsBiggerThan4AndTheSizeOfWaterIsBiggerThan3);
    }
}
