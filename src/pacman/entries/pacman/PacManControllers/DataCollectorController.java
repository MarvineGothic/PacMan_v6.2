package pacman.entries.pacman.PacManControllers;

import pacman.controllers.HumanController;
import pacman.controllers.KeyBoardInput;
import pacman.entries.pacman.utils.CollectedData;
import pacman.entries.pacman.utils.DataTuple;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static java.awt.event.KeyEvent.VK_SPACE;
import static pacman.entries.pacman.utils.Utils.*;

/**
 * The DataCollectorHumanController class is used to collect training data from playing PacMan.
 * Data about game state and what MOVE chosen is saved every time getMove is called.
 *
 * @author andershh
 */
public class DataCollectorController extends HumanController {
    public CollectedData collectedData;
    public int time = 500;
    public KeyBoardInput in;
    public Properties properties;
    public String file;

    public DataCollectorController(KeyBoardInput input, CollectedData cd) {
        this(input);
        collectedData = cd;
    }

    public DataCollectorController(KeyBoardInput input) {
        super(input);
        this.in = input;
        properties = getProperties(PACMAN_PROPERTIES);
        file = properties.getProperty("game.cd");
        collectedData = (CollectedData) loadFromFile(file);
        if (collectedData == null)
            collectedData = new CollectedData();
        List<DataTuple> remove = new ArrayList<>();
        for (int i = 0; i < collectedData.dataTuples.size(); i++) {
            if (collectedData.dataTuples.get(i).mazeIndex == 1) remove.add(collectedData.dataTuples.get(i));
        }
        collectedData.dataTuples.removeAll(remove);
        saveToFile(file, collectedData);
    }

    /**
     * To save game state-action pair in CollectedData, press Space key
     *
     * @param game
     * @param dueTime
     * @return
     */
    @Override
    public MOVE getMove(Game game, long dueTime) {
        MOVE move = super.getMove(game, dueTime);

        collectedData.addTuple(new DataTuple(game, move));
        //if (game.getTotalTime() > time) {
        if (in.getKey() == VK_SPACE)
            saveToFile(file, collectedData);
        //    time += time;
        //}
        //DataSaverLoader.SavePacManData(data);
        return move;
    }

}
