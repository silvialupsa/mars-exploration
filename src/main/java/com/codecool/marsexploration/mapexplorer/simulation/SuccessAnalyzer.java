package com.codecool.marsexploration.mapexplorer.simulation;

import com.codecool.marsexploration.mapexplorer.configuration.model.Configuration;
import com.codecool.marsexploration.mapexplorer.exploration.ExplorationOutcome;

public class SuccessAnalyzer implements OutcomeAnalyzer {
    @Override
    public boolean hasReachedOutcome(SimulationContext context, Configuration configuration) {
        context.setExplorationOutcome(ExplorationOutcome.COLONIZABLE);
        boolean checked = false;
        if (context.getMonitoredResources().containsKey("#") && context.getMonitoredResources().containsKey("&")) {
            checked = (context.getMonitoredResources().get("#").size() >= 1 && context.getMonitoredResources().get("&").size() >= 1);
        }

        int sizeOfMineral = context.getMonitoredResources().get("%") != null ? context.getMonitoredResources().get("%").size() : 0;
        int sizeOfWater = context.getMonitoredResources().get("*") != null ? context.getMonitoredResources().get("*").size() : 0;

        context.setExplorationOutcome(ExplorationOutcome.COLONIZABLE);
        return (context.getMonitoredResources().containsKey("&")
                && context.getMonitoredResources().containsKey("#")
                && context.getMonitoredResources().containsKey("%")
                && context.getMonitoredResources().containsKey("*"))
                || (sizeOfMineral > 4
                && sizeOfWater > 3)
                || checked;
    }
    //todo return with 3 booleans
}
