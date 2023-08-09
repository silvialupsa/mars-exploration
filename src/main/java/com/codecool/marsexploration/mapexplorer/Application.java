package com.codecool.marsexploration.mapexplorer;

import com.codecool.marsexploration.mapexplorer.configuration.ConfigurationValidatorImpl;
import com.codecool.marsexploration.mapexplorer.configuration.model.Configuration;
import com.codecool.marsexploration.mapexplorer.database.Resources;
import com.codecool.marsexploration.mapexplorer.database.ResourcesImpl;
import com.codecool.marsexploration.mapexplorer.logger.ConsoleLogger;
import com.codecool.marsexploration.mapexplorer.logger.FileLogger;
import com.codecool.marsexploration.mapexplorer.logger.Logger;
import com.codecool.marsexploration.mapexplorer.maploader.MapLoader;
import com.codecool.marsexploration.mapexplorer.maploader.MapLoaderImpl;
import com.codecool.marsexploration.mapexplorer.maploader.model.Coordinate;
import com.codecool.marsexploration.mapexplorer.rovers.InitializeRover;
import com.codecool.marsexploration.mapexplorer.rovers.model.MarsRover;
import com.codecool.marsexploration.mapexplorer.simulation.ExplorationSimulator;
import com.codecool.marsexploration.mapexplorer.simulation.SimulationContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class Application {
    private static final String workDir = "src/main";

    public static void main(String[] args) {
        Logger consoleLogger = new ConsoleLogger();
        consoleLogger.logInfo("Legend:");
        consoleLogger.logInfo("Rover-\uD83D\uDE97");
        consoleLogger.logInfo("Spaceship-\uD83D\uDE80");
        consoleLogger.logInfo("Mountain-\uD83D\uDDFB");
        consoleLogger.logInfo("Pit-\uD83D\uDEB5");
        consoleLogger.logInfo("Mineral-\uD83D\uDD36");
        consoleLogger.logInfo("Water-\uD83D\uDCA7");
        String dbFile = "src/main/resources/ResourcesMars.db";
        Resources resourcesDatabase = new ResourcesImpl(dbFile, consoleLogger);
        resourcesDatabase.deleteAll();
        for (int i = 0; i < 3; i++) {
            String mapFile = workDir + "/resources/exploration-" + i + ".map";
            HashMap<String, List<Coordinate>> resourcesRover1 = new HashMap<>();
            HashMap<String, List<Coordinate>> resourcesRover2 = new HashMap<>();
            HashMap<String, List<Coordinate>> resourcesRover3 = new HashMap<>();
            Map<MarsRover, HashMap<String, List<Coordinate>>> monitoredResources = new HashMap<>();
            Random random = new Random();
            int x = random.nextInt(32);
            int y = random.nextInt(32);
            Coordinate landingSpot = new Coordinate(x, y);
            ConfigurationValidatorImpl configurationValidator = new ConfigurationValidatorImpl();
            Configuration mapConfiguration = new Configuration(mapFile, landingSpot, List.of("#", "&", "*", "%"), 30);
            if (configurationValidator.checkLandingSpots(landingSpot, mapConfiguration)) {
                InitializeRover initializeRover = new InitializeRover();
                MarsRover rover1 = initializeRover.initializeRover(landingSpot, 2, resourcesRover1, mapConfiguration);
                MarsRover rover2 = initializeRover.initializeRover(landingSpot, 2, resourcesRover2, mapConfiguration);
                MarsRover rover3 = initializeRover.initializeRover(landingSpot, 2, resourcesRover3, mapConfiguration);

                List<MarsRover> rovers = List.of(rover1, rover2, rover3);


                SimulationContext simulationContext = new SimulationContext(0, 60, rovers, landingSpot, mapFile, monitoredResources);
                FileLogger fileLogger = new FileLogger(workDir + "/resources/ResultsAfterExploration-" + i + ".map");
                ExplorationSimulator explorationSimulator = new ExplorationSimulator(fileLogger, simulationContext, configurationValidator, mapConfiguration);
                explorationSimulator.startExploring();
//                for (MarsRover rover : rovers) {
           //         resourcesDatabase.add(rovers.getNamed(), simulationContext.getNumberOfSteps(), simulationContext.getNumberOfResources(), simulationContext.getExplorationOutcome().toString());

//                }
                consoleLogger.logInfo("File ResultsAfterExploration-" + i + ".map successful created.");
            } else {
                consoleLogger.logError("Invalid landing Spot for spaceship-" + i);
            }
            System.out.println();
        }

    }


}

