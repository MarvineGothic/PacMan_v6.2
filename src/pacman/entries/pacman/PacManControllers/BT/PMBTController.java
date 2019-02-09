package pacman.entries.pacman.PacManControllers.BT;

import pacman.entries.pacman.BehaviorTree.Composite.BTSelector;
import pacman.entries.pacman.BehaviorTree.Composite.BTSequence;
import pacman.entries.pacman.BehaviorTree.TreeBuilder;
import pacman.entries.pacman.BehaviorTree.Archetypes.Node;
import pacman.entries.pacman.GeneticAlgorithm.GeneticPopulation;
import pacman.entries.pacman.PacManControllers.BT.BTNodes.*;
import pacman.entries.pacman.utils.Parameters;
import pacman.entries.pacman.utils.ParametricPhenotype;
import pacman.game.Constants.GHOST;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static pacman.entries.pacman.GeneticAlgorithm.GeneticAlgorithm.PARAM_GA_PROPERTIES;
import static pacman.entries.pacman.utils.Parameters.PILLS_THRESHOLD;
import static pacman.entries.pacman.utils.Utils.*;

public class PMBTController extends TreeBuilder {

    public static ParametricPhenotype parametricPhenotype;
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

    public PMBTController(Node root) {
        super(root);
    }

    public PMBTController(boolean useTrainedParameters) {
        super();
        if (useTrainedParameters) {
            Properties props = new Properties();
            try (FileInputStream fis = new FileInputStream(new File(PARAM_GA_PROPERTIES))) {
                props.load(fis);
                GeneticPopulation savedPopulation = (GeneticPopulation) loadFromFile(props.getProperty("game.ga"));
                if (savedPopulation != null)
                    PMBTController.parametricPhenotype = new ParametricPhenotype(savedPopulation.getBestChromosome().getGenotype());
                else System.out.println("Error! Couldn't load saved population file!");
            } catch (IOException e) {
                System.out.println("Error! Was unable to load file!");
                e.printStackTrace();
            }
            Parameters.printParameters();
        }
    }

    public PMBTController(ParametricPhenotype parametricPhenotype) {
        super();
        PMBTController.parametricPhenotype = parametricPhenotype;
    }

    public void init() {
        int currentLevel = game.getCurrentLevel();
        if (currentLevel != gameLevel) {
            gameLevel = currentLevel;
            junctions = game.getJunctionIndices();
            allPathsList = getAllJunctionPaths(game, junctions);
            totalPills = game.getPillIndices();
            if (PILLS_THRESHOLD == 0)
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
                                        add(new CheckGhostMinDistance()).
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
                                add(new MoveToAnyPossibleWay())
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
                        add(new MoveToAnyPossibleWay()));
    }
}