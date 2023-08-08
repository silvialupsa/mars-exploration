package com.codecool.marsexploration.mapexplorer.simulation;

import com.codecool.marsexploration.mapexplorer.configuration.model.Configuration;

public interface OutcomeAnalyzer {
    boolean hasReachedOutcome(SimulationContext context, Configuration configuration);
}

