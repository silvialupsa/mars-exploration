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
        Map<MarsRover, List<Coordinate>> visitedCoordinate = new HashMap<>();
        Random random = new Random();
        for (int i = 0; i < simulationContext.getTimeoutSteps(); i++) {
            for (MarsRover rover : simulationContext.getRover()) {
                List<Coordinate> adjacentCoordinate = configurationValidator.checkAdjacentCoordinate(rover.getCurrentPosition(), configuration);
                if (!adjacentCoordinate.isEmpty()) {
                    Coordinate roverPosition = rover.getCurrentPosition();
                    Coordinate newRandomRoverPosition = adjacentCoordinate.get(random.nextInt(adjacentCoordinate.size()));
                    if (!visitedCoordinate.containsKey(rover)) {
                        visitedCoordinate.put(rover, new ArrayList<>(Collections.singleton(roverPosition)));
                    } else {
                        visitedCoordinate.get(rover).add(roverPosition);
                    }
                    rover.setCurrentPosition(newRandomRoverPosition);
                    fileLogger.logInfo("STEP " + simulationContext.getNumberOfSteps() + "; EVENT searching; UNIT " + rover.getNamed() + "; POSITION [" + roverPosition.X() + "," + roverPosition.Y() + "]");

                }
            }
//            isOutcomeReached(simulationContext, configuration);
//            System.out.println("outcome " + simulationContext.getExplorationOutcome() + " rover " + simulationContext.getRover().get(indexOfRover).getNamed());
            simulationContext.setNumberOfSteps(simulationContext.getNumberOfSteps() + 1);
        }
        configurationValidator.roverMap(simulationContext.getSpaceshipLocation(), configuration, visitedCoordinate);
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
        return analyzers.stream()
                .anyMatch(analyzer -> analyzer.hasReachedOutcome(context, configuration));
    }

}


//START EXPLORING
//        fileLogger.clearLogFile();
//
//        setInitializeData();
//        List<Coordinate> visitedCoordinate = new ArrayList<>();
//        while (checkIfWeHaveNumberOfStepsAndOutcomeIsNotColonizable()) {
//                explore(simulationContext.getRover(), visitedCoordinate);
//        }
//        System.out.println(visitedCoordinate);


//        fileLogger.clearLogFile();
//        for (MarsRover rover : simulationContext.getRover()) {
//            System.out.println(" outcome " + simulationContext.getExplorationOutcome());
//            simulationContext.setExplorationOutcome(ExplorationOutcome.SEARCHING);
//            List<Coordinate> visitedCoordonate = new ArrayList<>();
//            simulationContext.setNumberOfSteps(0);
//            simulationContext.setMonitoredResources(new HashMap<>());
//            Random random = new Random();
//            while (simulationContext.getNumberOfSteps() < simulationContext.getTimeoutSteps() && simulationContext.getExplorationOutcome() != ExplorationOutcome.COLONIZABLE
//                    && !isOutcomeReached(simulationContext, configuration)) {
//                List<Coordinate> adjacentCoordinate = configurationValidator.checkAdjacentCoordinate(rover.getCurrentPosition(), configuration);
//                if (!adjacentCoordinate.isEmpty()) {
//
//                    Coordinate roverPosition = rover.getCurrentPosition();
//                    Coordinate newRandomRoverPosition = adjacentCoordinate.get(random.nextInt(adjacentCoordinate.size()));
//                    visitedCoordonate.add(roverPosition);
//                    if (!new HashSet<>(visitedCoordonate).containsAll(adjacentCoordinate) && !new HashSet<>(adjacentCoordinate).containsAll(visitedCoordonate)) {
//                        while (visitedCoordonate.contains(newRandomRoverPosition)) {
//                            newRandomRoverPosition = adjacentCoordinate.get(random.nextInt(adjacentCoordinate.size()));
//                        }
//                    }
//                    rover.setCurrentPosition(newRandomRoverPosition);
//                    simulationContext.setNumberOfSteps(simulationContext.getNumberOfSteps() + 1);
//
//                    fileLogger.logInfo("STEP " + simulationContext.getNumberOfSteps() + "; EVENT searching; UNIT " + rover.getNamed() + "; POSITION [" + roverPosition.X() + "," + roverPosition.Y() + "]");
//                    if (configurationValidator.checkAdjacentCoordinate(roverPosition, configuration).size() < 8) {
//                        simulationContext.getMonitoredResources().putAll(findResources(configuration, rover.getCurrentPosition()));
//                    }
//                }
//            }
//            fileLogger.logInfo("EVENT outcome; OUTCOME " + simulationContext.getExplorationOutcome());
//            configurationValidator.roverMap(simulationContext.getSpaceshipLocation(), configuration, visitedCoordonate);
//            rover.setCurrentPosition(simulationContext.getSpaceshipLocation());
//
//        }


//REFACTOR
//    private boolean isOutcomeReached(SimulationContext context, Configuration configuration) {
//        return analyzers.stream().anyMatch(analyzer -> analyzer.hasReachedOutcome(context, configuration));
//    }
//
//    private boolean checkIfWeHaveNumberOfStepsAndOutcomeIsNotColonizable(){
//       return simulationContext.getNumberOfSteps() < simulationContext.getTimeoutSteps() && simulationContext.getExplorationOutcome() != ExplorationOutcome.COLONIZABLE
//                && !isOutcomeReached(simulationContext, configuration);
//    }
//
//    private void setInitializeData(){
//        System.out.println(" outcome " + simulationContext.getExplorationOutcome());
//        simulationContext.setExplorationOutcome(ExplorationOutcome.SEARCHING);
//        simulationContext.setNumberOfSteps(0);
//        simulationContext.setMonitoredResources(new HashMap<>());
//    }
//
//    private void explore(List<MarsRover> marsRovers, List<Coordinate> visitedCoordinate){
//
//        Random random = new Random();
//        for(int i=0; i< simulationContext.getNumberOfSteps(); i++){
//            for(MarsRover rover : marsRovers){
//                List<Coordinate> adjacentCoordinate = configurationValidator.checkAdjacentCoordinate(rover.getCurrentPosition(), configuration);
//                if (!adjacentCoordinate.isEmpty()) {
//                    Coordinate roverPosition = rover.getCurrentPosition();
//                    Coordinate newRandomRoverPosition = adjacentCoordinate.get(random.nextInt(adjacentCoordinate.size()));
//                    visitedCoordinate.add(roverPosition);
//
//                    newRandomRoverPosition = setNewRandomRoverPosition(visitedCoordinate, adjacentCoordinate, newRandomRoverPosition, random);
//
//                    rover.setCurrentPosition(newRandomRoverPosition);
//                    fileLogger.logInfo("STEP " + simulationContext.getNumberOfSteps() + "; EVENT searching; UNIT " + rover.getNamed() + "; POSITION [" + roverPosition.X() + "," + roverPosition.Y() + "]");
//                    if (configurationValidator.checkAdjacentCoordinate(roverPosition, configuration).size() < 8) {
//                        simulationContext.getMonitoredResources().putAll(findResources(configuration, rover.getCurrentPosition()));
//                    }
////                    setFinalData(visitedCoordinate, rover);
//                }
//            }
//            simulationContext.setNumberOfSteps(simulationContext.getNumberOfSteps() + 1);
//        }
//
//    }
//
//    public Coordinate setNewRandomRoverPosition(List<Coordinate> visitedCoordinate, List<Coordinate> adjacentCoordinate, Coordinate newRandomRoverPosition, Random random){
//        if (!new HashSet<>(visitedCoordinate).containsAll(adjacentCoordinate) && !new HashSet<>(adjacentCoordinate).containsAll(visitedCoordinate)) {
//            while (visitedCoordinate.contains(newRandomRoverPosition)) {
//                newRandomRoverPosition = adjacentCoordinate.get(random.nextInt(adjacentCoordinate.size()));
//            }
//        }
//        return  newRandomRoverPosition;
//    }
//
////    public void setFinalData(List<Coordinate> visitedCoordinate, MarsRover rover){
////        fileLogger.logInfo("EVENT outcome; OUTCOME " + simulationContext.getExplorationOutcome());
////        configurationValidator.roverMap(simulationContext.getSpaceshipLocation(), configuration, visitedCoordinate);
////        rover.setCurrentPosition(simulationContext.getSpaceshipLocation());
////    }


