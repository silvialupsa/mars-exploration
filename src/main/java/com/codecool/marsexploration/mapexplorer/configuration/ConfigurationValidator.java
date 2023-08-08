package com.codecool.marsexploration.mapexplorer.configuration;

import com.codecool.marsexploration.mapexplorer.configuration.model.Configuration;
import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;

import java.util.List;

public interface ConfigurationValidator {
    boolean validateConfigurationObject(Configuration configuration);

    List<Coordinate> checkAdjacentCoordinate(Coordinate coordinate, Configuration configuration);

    boolean checkSymbols(List<String> symbols);

    boolean checkLandingSpots(Coordinate coordinate, Configuration configuration);

     void roverMap(Coordinate spaceshipLocation, Configuration mapConfiguration, List<Coordinate> coordinates);
}