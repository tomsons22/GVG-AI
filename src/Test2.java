import core.ArcadeMachine;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Diego
 * Date: 04/10/13
 * Time: 16:29
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class Test2
{

    public static void main(String[] args)
    {
        //Available controllers:
        String sampleRandomController = "controllers.sampleRandom.Agent";
        String sampleOneStepController = "controllers.sampleonesteplookahead.Agent";
        String sampleMCTSController = "controllers.sampleMCTS.Agent";
        String sampleOLMCTSController = "controllers.sampleOLMCTS.Agent";
        String sampleGAController = "controllers.sampleGA.Agent";
        String tester = "controllers.Tester.Agent";
        String NovTea = "controllers.NovTea.Agent";

        //Available games:
        String gamesPath = "examples/gridphysics/";

        //CIG 2014 Training Set Games
        String games[] = new String[]{"butterflies", "aliens", "boulderdash", "chase", "frogs",
                "missilecommand", "portals", "sokoban", "survivezombies", "zelda"};

        //CIG 2014 Validation Set Games
        //String games[] = new String[]{"camelRace", "digdug", "firestorms", "infection", "firecaster",
        //     "overload", "pacman", "seaquest", "whackamole", "eggomania"};

        //CIG 2015 New Training Set Games
        //String games[] = new String[]{"bait", "boloadventures", "brainman", "chipschallenge",  "modality",
        //                              "painter", "realportals", "realsokoban", "thecitadel", "zenpuzzle"};


        //CIG 2014 TEST SET / GECCO 2015 VALIDATION SET
        //String games[] = new String[]{"roguelike", "surround", "catapults", "plants", "plaqueattack",
        //        "jaws", "labyrinth", "boulderchase", "escape", "lemmings"};


        //Other settings
        boolean visuals = true;
        String recordActionsFile = null; //where to record the actions executed. null if not to save.
        int seed = new Random().nextInt();

        //Game and level to play
        int levelIdx = 1; //level names from 0 to 4 (game_lvlN.txt).
        String gameName = "painter";
        String game = gamesPath + gameName + ".txt";
        String level1 = gamesPath + gameName + "_lvl" + levelIdx +".txt";

        // 1. This starts a game, in a level, played by a human.
        //ArcadeMachine.playOneGame(game, level1, recordActionsFile, seed);

        // 2. This plays a game in a level by the controller.
        //ArcadeMachine.runOneGame(game, level1, visuals, YBCriberController, recordActionsFile, seed);
        //ArcadeMachine.runOneGame(game, level1, visuals, tester, recordActionsFile, seed);
        //ArcadeMachine.runOneGame(game, level1, visuals, NovTea, recordActionsFile, seed, false);

        // 3. This replays a game from an action file previously recorded
        //String readActionsFile = "actionsFile_aliens_lvl0.txt";  //This example is for
        //ArcadeMachine.replayGame(game, level1, visuals, readActionsFile);

         //4. This plays a single game, in N levels, M times :
        //String level2 = gamesPath + games[gameIdx] + "_lvl" + 1 +".txt";
        //int M = 1;
        //ArcadeMachine.runGames(game, new String[]{level1, level2}, M, sampleMCTSController, null);

        //5. This plays N games, in the first L levels, M times each. Actions to file optional (set saveActions to true).
        int N = 1, L = 1, M = 3;
        boolean saveActions = false;
        String[] levels = new String[L];
        String[] actionFiles = new String[L*M];
        for(int i = 0; i < N; ++i)
        {
            int actionIdx = 0;
            game = gamesPath + games[i] + ".txt";
            for(int j = 0; j < L; ++j){
                levels[j] = gamesPath + games[i] + "_lvl" + j +".txt";
                if(saveActions) for(int k = 0; k < M; ++k)
                    actionFiles[actionIdx++] = "actions_game_" + i + "_level_" + j + "_" + k + ".txt";
            }
            ArcadeMachine.runGames(game, levels, M, NovTea, saveActions? actionFiles:null);
        }
    }
}
