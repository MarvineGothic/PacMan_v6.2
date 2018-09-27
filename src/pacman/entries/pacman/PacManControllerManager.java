package pacman.entries.pacman;

import pacman.controllers.Controller;
import pacman.entries.pacman.PacManControllers.BT.PacManBTBuilder;
import pacman.entries.pacman.PacManControllers.GA.PMWGAController;
import pacman.entries.pacman.PacManControllers.SuperPacMan;
import pacman.game.Constants.*;
import pacman.game.Game;

public class PacManControllerManager extends Controller<MOVE> {
    private Controller<MOVE> controller;

    public PacManControllerManager(int number) {
        switch (number){
            case 0:
                // plain controller
                controller = new SuperPacMan();
                break;
            case 1:
                // behavior tree
                controller = new PacManBTBuilder(false);
                break;
            case 2:
                // plain genetic algorithm
                controller = new PMWGAController();
                break;
            case 3:
                // parametric genetic algorithm
                controller = new PacManBTBuilder(true);
        }
    }

    @Override
    public MOVE getMove(Game game, long timeDue) {
        return controller.getMove(game, timeDue);
    }
}
