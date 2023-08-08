package com.codecool.marsexploration.mapexplorer.simulation;

import com.codecool.marsexploration.mapexplorer.configuration.model.Configuration;
import com.codecool.marsexploration.mapexplorer.exploration.ExplorationOutcome;

public class TimeoutAnalyzer implements OutcomeAnalyzer {
    @Override
    public boolean hasReachedOutcome(SimulationContext context, Configuration configuration) {

        context.setExplorationOutcome(ExplorationOutcome.TIMEOUT);
        return context.getNumberOfSteps() >= context.getTimeoutSteps();
    }
}
