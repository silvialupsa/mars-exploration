package com.codecool.marsexploration.mapexplorer.simulation;

import com.codecool.marsexploration.mapexplorer.commandCenter.CommandCenterImpl;
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
        Map<String, List<Coordinate>> commandCenterResources= new HashMap<>();
        for (int i = 0; i < simulationContext.getTimeoutSteps(); i++) {
            for (MarsRover rover : simulationContext.getRover()) {
                if (simulationContext.getExplorationOutcome().get(rover) == ExplorationOutcome.COLONIZABLE) {
                    fileLogger.logInfo("STEP " + simulationContext.getNumberOfSteps() + "; EVENT "+ simulationContext.getExplorationOutcome().get(rover)+"; UNIT " + rover.getNamed() + "; POSITION [" + rover.getCurrentPosition().X() + "," + rover.getCurrentPosition().Y() + "]");
                    fileLogger.logInfo("OUTCOME " + simulationContext.getExplorationOutcome().get(rover) + " for " + rover.getName());
                    simulationContext.setExplorationOutcome(rover, ExplorationOutcome.CONSTRUCTION);
                    System.out.println("inainte de a lasa resursele in rover" + rover.getResources());
                    simulationContext.setCommandCenterMap(rover, new CommandCenterImpl(rover.getId(), rover.getCurrentPosition(), 0, rover.getResources()));
                    commandCenterResources = rover.getResources();
//                    simulationContext.getCommandCenterMap().get(rover).setResourcesOnStock(rover.getResources());
                    rover.setResources(new HashMap<>());

                    System.out.println("--------------------------");
                    System.out.println("dupa ce am lasat resursele in command center" + simulationContext.getCommandCenterMap().get(rover).getResourcesOnStock());
                    System.out.println("dupa ce am lasat resursele in rover" + rover.getResources());
                    continue;
                }
                if(simulationContext.getExplorationOutcome().get(rover) == ExplorationOutcome.CONSTRUCTION){
                    if(simulationContext.getCommandCenterMap().get(rover).getStatus() >= 10){
                        simulationContext.setExplorationOutcome(rover, ExplorationOutcome.EXTRACTIONS);
                        continue;
                    }
                    simulationContext.getCommandCenterMap().get(rover).incrementStatus();
                    simulationContext.getCommandCenterMap().get(rover).setResourcesOnStock(rover.getResources());
                    int status = simulationContext.getCommandCenterMap().get(rover).getStatus();
                    fileLogger.logInfo("STEP " + simulationContext.getNumberOfSteps() + "STATUS " +status + "/10 " + "; EVENT "+ simulationContext.getExplorationOutcome().get(rover)+"; UNIT " + rover.getNamed() + "; POSITION [" + rover.getCurrentPosition().X() + "," + rover.getCurrentPosition().Y() + "]");
                    fileLogger.logInfo("OUTCOME " + simulationContext.getExplorationOutcome().get(rover) + " for " + rover.getName());
                    continue;
                }
                if(simulationContext.getExplorationOutcome().get(rover) == ExplorationOutcome.EXTRACTIONS){
                    fileLogger.logInfo("STEP " + simulationContext.getNumberOfSteps() + "; EVENT "+ simulationContext.getExplorationOutcome().get(rover)+"; UNIT " + rover.getNamed() + "; POSITION [" + rover.getCurrentPosition().X() + "," + rover.getCurrentPosition().Y() + "]");
                    fileLogger.logInfo("OUTCOME " + simulationContext.getExplorationOutcome().get(rover) + " for " + rover.getName());

                }
                if(simulationContext.getExplorationOutcome().get(rover) == ExplorationOutcome.DONE_EXTRACTION){


                    fileLogger.logInfo("STEP " + simulationContext.getNumberOfSteps()  + "; EVENT "+ simulationContext.getExplorationOutcome().get(rover)+"; UNIT " + rover.getNamed() + "; POSITION [" + rover.getCurrentPosition().X() + "," + rover.getCurrentPosition().Y() + "]");
                    fileLogger.logInfo("OUTCOME " + simulationContext.getExplorationOutcome().get(rover) + " for " + rover.getName());
                    rover.setCurrentPosition(simulationContext.getCommandCenterMap().get(rover).getLocation());
                    simulationContext.setExplorationOutcome(rover, ExplorationOutcome.EXTRACTIONS);

                    System.out.println("inainte de a lasa resursele in command center" + simulationContext.getCommandCenterMap().get(rover).getResourcesOnStock());
                    System.out.println("inainte de a lasa resursele in rover" + rover.getResources());
                    simulationContext.getCommandCenterMap().get(rover).setResourcesOnStock(rover.getResources());
                    rover.setResources(new HashMap<>());
                    System.out.println("dupa ce am lasat resursele in command center" + simulationContext.getCommandCenterMap().get(rover).getResourcesOnStock());
                    System.out.println("dupa ce am lasat resursele in rover" + rover.getResources());
                    continue;
                }
                roverTravelSteps( rover, visitedCoordinate);
               // System.out.println("______________________________________________");
              //  System.out.println("dupa ce am lasat resursele in command center" + simulationContext.getCommandCenterMap().get(rover).getResourcesOnStock());
             //   System.out.println("______________________________________________");
            }

            simulationContext.setNumberOfSteps(simulationContext.getNumberOfSteps() + 1);
            fileLogger.logInfo("----------------------------------------");
        }
        configurationValidator.roverMap(simulationContext.getSpaceshipLocation(), configuration, visitedCoordinate, simulationContext.getCommandCenterMap());

    }

    private void roverTravelSteps(MarsRover rover, Map<MarsRover, List<Coordinate>> visitedCoordinate) {
        List<Coordinate> adjacentCoordinate = configurationValidator.checkAdjacentCoordinate(rover.getCurrentPosition(), configuration);
        Random random = new Random();
        if (!adjacentCoordinate.isEmpty()) {
            Coordinate roverPosition = rover.getCurrentPosition();
            Coordinate newRandomRoverPosition = adjacentCoordinate.get(random.nextInt(adjacentCoordinate.size()));
            if (!visitedCoordinate.containsKey(rover)) {
                visitedCoordinate.put(rover, new ArrayList<>(Collections.singleton(roverPosition)));
            } else {
                visitedCoordinate.get(rover).add(roverPosition);
            }
            rover.setCurrentPosition(newRandomRoverPosition);
            if (configurationValidator.checkAdjacentCoordinate(roverPosition, configuration).size() < 8) {
                rover.setResources(findResources(configuration, rover.getCurrentPosition()));
            }

            if(simulationContext.getExplorationOutcome().get(rover) != ExplorationOutcome.EXTRACTIONS) {
                fileLogger.logInfo("STEP " + simulationContext.getNumberOfSteps() + "; EVENT searching; UNIT " + rover.getNamed() + "; POSITION [" + roverPosition.X() + "," + roverPosition.Y() + "]");
                fileLogger.logInfo("OUTCOME " + simulationContext.getExplorationOutcome().get(rover) + " for " + rover.getName());
                isOutcomeReached(rover, simulationContext, configuration);
            } else {
                if(rover.getResources().size() >=3){
                    simulationContext.setExplorationOutcome(rover, ExplorationOutcome.DONE_EXTRACTION);
                }
                fileLogger.logInfo("STEP " + simulationContext.getNumberOfSteps() + "; EVENT extraction; UNIT " + rover.getNamed() + "; POSITION [" + roverPosition.X() + "," + roverPosition.Y() + "]");
                fileLogger.logInfo("OUTCOME " + simulationContext.getExplorationOutcome().get(rover) + " for " + rover.getName());
            }
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

    private boolean isOutcomeReached(MarsRover rover, SimulationContext context, Configuration configuration) {
        return analyzers.stream()
                .anyMatch(analyzer -> analyzer.hasReachedOutcome(rover, context, configuration));
    }
}