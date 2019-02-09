package pacman.entries.pacman;

import pacman.controllers.Controller;
import pacman.controllers.HumanController;
import pacman.controllers.KeyBoardInput;
import pacman.controllers.examples.*;
import pacman.entries.pacman.PacManControllers.BT.PMBTController;
import pacman.entries.pacman.PacManControllers.DataCollectorController;
import pacman.entries.pacman.PacManControllers.GA.PMGAController;
import pacman.entries.pacman.PacManControllers.MCTSPMController;
import pacman.entries.pacman.PacManControllers.NN.PMNNController;
import pacman.entries.pacman.PacManControllers.SuperPacMan;
import pacman.game.Constants;
import pacman.game.Constants.MOVE;

import java.util.EnumMap;

public class PacManControllerManager {
    private Controller<MOVE> controller;

    public PacManControllerManager(String name, Controller<EnumMap<Constants.GHOST, MOVE>> ghostController) {
        switch (name) {
            case "Super":
                // plain controller
                controller = new SuperPacMan();
                break;
            case "BT":
                // behavior tree
                controller = new PMBTController(false);
                break;
            case "GA":
                // plain genetic algorithm
                controller = new PMGAController();
                break;
            case "PA":
                // parametric genetic algorithm
                controller = new PMBTController(true);
                break;
            case "NN":
                // neural network
                controller = new PMNNController();
                break;
            case "MC":
                // monte carlo
                controller = new MCTSPMController(ghostController);
                break;
            case "DC":
                // data collector
                controller = new DataCollectorController(new KeyBoardInput());
                break;
            case "ST":
                // starter
                controller = new StarterPacMan();
                break;
            case "RA":
                // Random
                controller = new RandomPacMan();
                break;
            case "RN":
                // Random non reverse
                controller = new RandomNonRevPacMan();
                break;
            case "NP":
                // Nearest pill
                controller = new NearestPillPacMan();
                break;
            case "NV":
                // Nearest pill visual
                controller = new NearestPillPacManVS();
                break;
            case "HC":
                // Human controller
                controller = new HumanController(new KeyBoardInput());
            default:
                System.out.println("Wrong parameter!");
                System.exit(0);
        }
    }

    public Controller<MOVE> getController() {
        return controller;
    }
}
