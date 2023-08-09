package com.codecool.marsexploration.mapexplorer.simulation;

import com.codecool.marsexploration.mapexplorer.configuration.model.Configuration;
import com.codecool.marsexploration.mapexplorer.exploration.ExplorationOutcome;

public class SuccessAnalyzer implements OutcomeAnalyzer {
    @Override
    public boolean hasReachedOutcome(SimulationContext context, Configuration configuration) {
        context.setExplorationOutcome(ExplorationOutcome.COLONIZABLE);
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

        context.setExplorationOutcome(ExplorationOutcome.COLONIZABLE);
        return (checkIfWeHaveOneMountainAndOnePit
                || checkIfWeHaveMinimOneResourceFromEach
                || checkIfTheSizeOfMineralIsBiggerThan4AndTheSizeOfWaterIsBiggerThan3);
    }
}
