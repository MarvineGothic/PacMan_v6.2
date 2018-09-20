package pacman.entries.pacman.BT;

import pacman.entries.BT.Composite.BTSelector;
import pacman.entries.BT.Composite.BTSequence;
import pacman.entries.BT.TreeBuilder;
import pacman.entries.BT.utils.Node;
import pacman.entries.utils.ParametricPhenotype;
import pacman.game.Constants.GHOST;

import java.util.ArrayList;
import java.util.List;

import static pacman.entries.utils.Parameters.*;
import static pacman.entries.utils.Utils.getAllJunctionPaths;
import static pacman.entries.utils.Utils.getPillsThreshold;

public class PacManBuilder extends TreeBuilder {

    public static List<int[]> allPathsList = new ArrayList<>();
    public static List<int[]> safePathsList = new ArrayList<>();
    public static int pacManIdx;
    public static GHOST closestGhost;
    public static int closestTarget = -1;
    public static int closestRunFromTargetIndex = -1;
    public static int closestGhostDist;
    public static double pillsPerCent = -1;

    public static int[] activePillsIndices;
    public static int[] safeJunctionsNDE;
    public static int[] safePills;
    public static int[] safePathToPills;

    public static int[] safePathToPower;
    public static int[] activePowerPillsIndices;

    public PacManBuilder(Node root) {
        super(root);
    }

    public PacManBuilder() {
        constructTree();
        Node.setTreeBuilder(this);
    }

    @Override
    public TreeBuilder addPhenotype(ParametricPhenotype parametricPhenotype) {
        // initialize fields from ParametricPhenotype to PacMan:
        MIN_DISTANCE = parametricPhenotype.P_MIN_DISTANCE;
        MIN_DISTANCE_2 = parametricPhenotype.P_MIN_DISTANCE_2;
        PILLS_THRESHOLD = parametricPhenotype.P_PILLS_THRESHOLD;
        GHOST_EDIBLE_TIME = parametricPhenotype.P_GHOST_EDIBLE_TIME;
        return this;
    }

    public void init() {
        int currentLevel = game.getCurrentLevel();
        if (currentLevel != gameLevel) {
            gameLevel = currentLevel;
            junctions = game.getJunctionIndices();
            allPathsList = getAllJunctionPaths(game, junctions);
            totalPills = game.getPillIndices();
            if (PILLS_THRESHOLD != -1)
                PILLS_THRESHOLD = getPillsThreshold(game, allPathsList);
        }
    }

    @Override
    public void constructTree() {
        root = new BTSelector().
                add(new BTSequence().
                        add(new GetClosestDangerousGhost()).
                        add(new BTSelector().
                                add(new BTSequence().
                                        add(new GetClosestPill()).
                                        add(new GhostMinDistance()).
                                        add(new MoveTowardsTarget())).
                                add(new BTSequence().
                                        add(new GetClosestPower()).
                                        add(new MoveTowardsTarget())).
                                add(new BTSequence().
                                        add(new GetClosestPill()).
                                        add(new MoveTowardsTarget())).
                                add(new BTSequence().
                                        add(new CheckSafeJunctionsNDE()).
                                        add(new MoveTowardsTarget())).
                                add(new MoveFromTarget()).
                                add(new GetAnyPossibleWay())
                        )).
                add(new BTSequence().
                        add(new GetClosestEdibleGhost()).
                        add(new CheckPowersLeft()).
                        add(new BTSelector().
                                add(new BTSequence().
                                        add(new CheckDistanceToEdibleGhost()).
                                        add(new MoveTowardsTarget())
                                ).
                                add(new BTSequence().
                                        add(new CheckPillsPercent()).
                                        add(new MoveTowardsTarget())))
                ).
                add(new BTSelector().
                        add(new BTSequence().
                                add(new GetClosestPill()).
                                add(new MoveTowardsTarget())).
                        add(new BTSequence().
                                add(new GetClosestPower()).
                                add(new CheckPillsThreshold()).
                                add(new MoveTowardsTarget())).
                        add(new BTSequence().
                                add(new CheckSafeJunctionsNDE()).
                                add(new MoveTowardsTarget())).
                        add(new GetAnyPossibleWay()));
    }
}