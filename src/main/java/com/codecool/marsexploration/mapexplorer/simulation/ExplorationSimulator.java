package com.codecool.marsexploration.mapexplorer.simulation;

import com.codecool.marsexploration.mapexplorer.configuration.ConfigurationValidator;
import com.codecool.marsexploration.mapexplorer.configuration.model.Configuration;
import com.codecool.marsexploration.mapexplorer.exploration.ExplorationOutcome;
import com.codecool.marsexploration.mapexplorer.logger.FileLogger;
import com.codecool.marsexploration.mapexplorer.maploader.MapLoaderImpl;
import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;

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
        List<Coordinate> visitedCoordonate = new ArrayList<>();
        Random random = new Random();
        fileLogger.clearLogFile();
        while (simulationContext.getNumberOfSteps() < simulationContext.getTimeoutSteps() && simulationContext.getExplorationOutcome() != ExplorationOutcome.COLONIZABLE
                && !isOutcomeReached(simulationContext, configuration)) {
            List<Coordinate> adjacentCoordinate = configurationValidator.checkAdjacentCoordinate(simulationContext.getRover().getCurrentPosition(), configuration);
            if (adjacentCoordinate.size() > 0) {
                Coordinate roverPosition = simulationContext.getRover().getCurrentPosition();
                Coordinate newRandomRoverPosition = adjacentCoordinate.get(random.nextInt(adjacentCoordinate.size()));
                visitedCoordonate.add(roverPosition);
                if (!new HashSet<>(visitedCoordonate).containsAll(adjacentCoordinate) && !new HashSet<>(adjacentCoordinate).containsAll(visitedCoordonate)) {
                    while (visitedCoordonate.contains(newRandomRoverPosition)) {
                        newRandomRoverPosition = adjacentCoordinate.get(random.nextInt(adjacentCoordinate.size()));
                    }
                }
                simulationContext.getRover().setCurrentPosition(newRandomRoverPosition);
                simulationContext.setNumberOfSteps(simulationContext.getNumberOfSteps() + 1);

                fileLogger.logInfo("STEP " + simulationContext.getNumberOfSteps() + "; EVENT searching; UNIT " + simulationContext.getRover().getNamed() + "; POSITION [" + roverPosition.X() + "," + roverPosition.Y() + "]");
                if (configurationValidator.checkAdjacentCoordinate(roverPosition, configuration).size() < 8) {
                    simulationContext.getMonitoredResources().putAll(findResources(configuration, simulationContext.getRover().getCurrentPosition()));
                }
            }
        }
        fileLogger.logInfo("EVENT outcome; OUTCOME " + simulationContext.getExplorationOutcome());
        configurationValidator.roverMap(simulationContext.getSpaceshipLocation(), configuration, visitedCoordonate);
        simulationContext.getRover().setCurrentPosition(simulationContext.getSpaceshipLocation());
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
