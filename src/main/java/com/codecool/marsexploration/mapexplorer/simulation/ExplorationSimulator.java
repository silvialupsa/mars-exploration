package com.codecool.marsexploration.mapexplorer.simulation;

import com.codecool.marsexploration.mapexplorer.configuration.ConfigurationValidator;
import com.codecool.marsexploration.mapexplorer.configuration.model.Configuration;
import com.codecool.marsexploration.mapexplorer.exploration.ExplorationOutcome;
import com.codecool.marsexploration.mapexplorer.logger.FileLogger;
import com.codecool.marsexploration.mapexplorer.maploader.MapLoaderImpl;
import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;
import com.codecool.marsexploration.mapexplorer.rovers.model.MarsRover;

import java.util.*;

public class ExplorationSimulator {
    private SimulationContext simulationContext;
    private ConfigurationValidator configurationValidator;
    private Configuration configuration;

    private List<OutcomeAnalyzer> analyzers;
    private FileLogger fileLogger;

    public ExplorationSimulator(FileLogger fileLogger, SimulationContext simulationContext, ConfigurationValidator configurationValidator, Configuration configuration) {
        this.simulationContext = simulationContext;
        this.configuration = configuration;
        this.configurationValidator = configurationValidator;
        this.analyzers = new ArrayList<>();
        this.analyzers.add(new SuccessAnalyzer());
        this.analyzers.add(new LackOfResourcesAnalyzer());
        this.analyzers.add(new TimeoutAnalyzer());
        this.fileLogger = fileLogger;
    }

    public void startExploring() {
        fileLogger.clearLogFile();
        for (MarsRover rover : simulationContext.getRover()) {
            System.out.println(" outcome " + simulationContext.getExplorationOutcome());
            simulationContext.setExplorationOutcome(ExplorationOutcome.SEARCHING);
            List<Coordinate> visitedCoordonate = new ArrayList<>();
            simulationContext.setNumberOfSteps(0);
            simulationContext.setMonitoredResources(new HashMap<>());
            Random random = new Random();
//            System.out.println("afara");
//            System.out.println("steps " + simulationContext.getNumberOfSteps());
//            boolean bool1 = simulationContext.getNumberOfSteps() < simulationContext.getTimeoutSteps();
//            boolean bool2 = simulationContext.getExplorationOutcome() != ExplorationOutcome.COLONIZABLE;
//            boolean bool3 = !isOutcomeReached(simulationContext, configuration);
//            System.out.println(" 1 + " + bool1);
//            System.out.println("2  " + bool2);
//            System.out.println("3 " + bool3);
            while (simulationContext.getNumberOfSteps() < simulationContext.getTimeoutSteps() && simulationContext.getExplorationOutcome() != ExplorationOutcome.COLONIZABLE
                    && !isOutcomeReached(simulationContext, configuration)) {
                System.out.printf("interior");
                List<Coordinate> adjacentCoordinate = configurationValidator.checkAdjacentCoordinate(rover.getCurrentPosition(), configuration);
                if (!adjacentCoordinate.isEmpty()) {

                    Coordinate roverPosition = rover.getCurrentPosition();
                    Coordinate newRandomRoverPosition = adjacentCoordinate.get(random.nextInt(adjacentCoordinate.size()));
                    visitedCoordonate.add(roverPosition);
                    if (!new HashSet<>(visitedCoordonate).containsAll(adjacentCoordinate) && !new HashSet<>(adjacentCoordinate).containsAll(visitedCoordonate)) {
                        while (visitedCoordonate.contains(newRandomRoverPosition)) {
                            newRandomRoverPosition = adjacentCoordinate.get(random.nextInt(adjacentCoordinate.size()));
                        }
                    }
                    rover.setCurrentPosition(newRandomRoverPosition);
                    simulationContext.setNumberOfSteps(simulationContext.getNumberOfSteps() + 1);

                    fileLogger.logInfo("STEP " + simulationContext.getNumberOfSteps() + "; EVENT searching; UNIT " + rover.getNamed() + "; POSITION [" + roverPosition.X() + "," + roverPosition.Y() + "]");
                    if (configurationValidator.checkAdjacentCoordinate(roverPosition, configuration).size() < 8) {
                        simulationContext.getMonitoredResources().putAll(findResources(configuration, rover.getCurrentPosition()));
                    }
                }
            }
            fileLogger.logInfo("EVENT outcome; OUTCOME " + simulationContext.getExplorationOutcome());
            configurationValidator.roverMap(simulationContext.getSpaceshipLocation(), configuration, visitedCoordonate);
            rover.setCurrentPosition(simulationContext.getSpaceshipLocation());

        }
    }

    public HashMap<String, List<Coordinate>> findResources(Configuration configuration, Coordinate currentRoverPosition) {
        List<String> mapLoader = new MapLoaderImpl().readAllLines(configuration.map());
        String map = String.join("", mapLoader);
        HashMap<String, List<Coordinate>> resourcesMap = new HashMap<>();
        int startX = (currentRoverPosition.X() == 0 ? 0 : currentRoverPosition.X() - 1);
        int startY = (currentRoverPosition.Y() == 0 ? 0 : currentRoverPosition.Y() - 1);
        int stopX = (currentRoverPosition.X() == mapLoader.size() - 1 ? currentRoverPosition.X() : currentRoverPosition.X() + 1);
        int stopY = (currentRoverPosition.Y() == mapLoader.size() - 1 ? currentRoverPosition.Y() : currentRoverPosition.Y() + 1);
        for (int i = startX; i <= stopX; i++) {
            for (int j = startY; j <= stopY; j++) {
                try {
                    char symbol = map.charAt(i * (mapLoader.size() - 1) + j);
                    if (symbol != ' ' && symbol != '\n') {
                        String symbolKey = Character.toString(symbol);
                        Coordinate coordinate = new Coordinate(j, i);
                        resourcesMap.computeIfAbsent(symbolKey, k -> new ArrayList<>()).add(coordinate);
                    }
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        }
        return resourcesMap;
    }

    private boolean isOutcomeReached(SimulationContext context, Configuration configuration) {
        for (OutcomeAnalyzer analyzer : analyzers) {
            if (analyzer.hasReachedOutcome(context, configuration)) {
                return true;
                //todo replace with stream and reduce between analyzers
            }
        }
        return false;
    }

}
