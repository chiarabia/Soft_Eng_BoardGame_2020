package it.polimi.ingsw.effects;

import it.polimi.ingsw.effects.build.*;
import it.polimi.ingsw.effects.consolidateBuild.StandardConsolidateBuild;
import it.polimi.ingsw.effects.consolidateMove.PushWorker;
import it.polimi.ingsw.effects.consolidateMove.StandardConsolidateMove;
import it.polimi.ingsw.effects.consolidateMove.SwapWorker;
import it.polimi.ingsw.effects.move.*;
import it.polimi.ingsw.effects.turn.NewTurn;
import it.polimi.ingsw.effects.winCondition.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class GodPowerManager {
    /*values​to keep memory of the player who changes the powers of the opponents (e.g: Hera, Athena, etc...)
     * 0 means nobody, otherwise 1, 2 or possibly 3 * */
    private static int opponentsCantMoveUpAfterIDPlayer;
    private static int opponentsCantWinOnPerimeterPlayer;

    /*JSON files' path*/
    private final static String root = "src\\main\\java\\it\\polimi\\ingsw\\Cards\\";

    /**
     * This method randomly extracts different 'numOfPlayers' cards in the form of List <String>,
     * regardless of names and/or number of cards in the Cards folder * /
     * @param numOfPlayers
     * @return List</String>
     * @throws IOException
     */

    private static List <String> chooseGodFiles (int numOfPlayers) throws IOException {
        List <String> cards = new ArrayList();
        Stream<Path> paths = Files.walk(Paths.get(root));
        paths.filter(Files::isRegularFile).forEach(x->{cards.add(x.toString().substring(root.length()));});
        // now the card list contains all 14 strings of JSON file names (eg "ApolloCard.json")

        Random rand = new Random();
        int numOfAvailableCards = cards.size();
        for (int i = numOfAvailableCards; i > numOfPlayers; i--) cards.remove(rand.nextInt(i));
        // only 'numOfPlayers' random strings are left in the card list now

        return cards;
    }

    /**
     * This method creates a GodPower Object corresponding to the nameOfFile string. The appropriate effects are read from the JSON file.
     * @param nameOfFile JSON file's name
     * @param numOfPlayer Player ID
     */

    private static GodPower power (String nameOfFile, int numOfPlayer) throws IOException, ParseException {
        GodPower godPower = new GodPower(numOfPlayer);
        FileReader fileReader = new FileReader(root + nameOfFile);
        JSONObject jsonObject = (JSONObject) (new JSONParser()).parse(fileReader);
        //Effects' strings
        String move = (String) jsonObject.get("move");
        String build = (String) jsonObject.get("build");
        String consolidateMove = (String) jsonObject.get("consolidateMove");
        String consolidateBuild = (String) jsonObject.get("consolidateBuild");
        String positiveWinConditions = (String) jsonObject.get("positiveWinConditions");
        String blockingWinConditions = (String) jsonObject.get("negativeWinConditions");
        String loseConditions = (String) jsonObject.get("loseConditions");
        String newTurn = (String) jsonObject.get("newTurn");

        int numOfBuilds = Math.toIntExact((Long) jsonObject.get("numOfBuilds"));
        int numOfMoves = Math.toIntExact((Long) jsonObject.get("numOfMoves"));

        switch (move) {
            case "unlimitedPerimetralMove":
                godPower.setMove(new UnlimitedMoveOnPerimeter(numOfMoves)); break;
            case "pushForward":
                godPower.setMove(new PushForward(numOfMoves)); break;
            case "moveNotOnInitialPosition":
                godPower.setMove(new MoveNotOnInitialPosition(numOfMoves)); break;
            case "swap":
                godPower.setMove(new SwapMove(numOfMoves)); break;
            case "":
                godPower.setMove(new StandardMove(numOfMoves)); break;
        }

        switch (build) {
            case "askToBuildBeforeMoveAndNotMoveUp":
                godPower.setAskToBuildBeforeMoveAndNotMoveUp(true);
                godPower.setBuild(new StandardBuild(numOfBuilds)); break;
            case "askToBuildDomes":
                godPower.setAskToBuildDomes(true);
                godPower.setBuild(new StandardBuild(numOfBuilds)); break;
            case "underMyself":
                godPower.setBuild(new UnderMyself(numOfBuilds)); break;
            case "notOnPerimeter":
                godPower.setBuild(new NotOnPerimeter(numOfBuilds)); break;
            case "onSamePositionBlockOnly":
                godPower.setBuild(new OnSamePositionBlockOnly(numOfBuilds)); break;
            case "notOnSamePosition":
                godPower.setBuild(new NotOnSamePosition(numOfBuilds)); break;
            case "":
                godPower.setBuild(new StandardBuild(numOfBuilds)); break;
        }

        godPower.setConsolidateBuild(new StandardConsolidateBuild());

        switch (consolidateMove) {
            case "pushWorker":
                godPower.setConsolidateMove(new PushWorker()); break;
            case "swapWorker":
                godPower.setConsolidateMove(new SwapWorker()); break;
            case "":
                godPower.setConsolidateMove(new StandardConsolidateMove()); break;
        }

        godPower.setPositiveWinConditions(new ArrayList());
        godPower.getPositiveWinConditions().add(new StandardWinCondition());

        switch (positiveWinConditions) {
            case "winMovingDownTwoOrMoreLevels":
                godPower.getPositiveWinConditions().add(new WinMovingDownTwoOrMoreLevels()); break;
            case "fiveCompletedTowers":
                godPower.getPositiveWinConditions().add(new FiveCompletedTowers()); break;
            case "": break;
        }

        godPower.setBlockingWinConditions(new ArrayList());
        switch (blockingWinConditions) {
            case "opponentsCantWinOnPerimeter":
                opponentsCantWinOnPerimeterPlayer = numOfPlayer; break;
            case "": break;
        }

        godPower.setLoseCondition(new StandardLoseCondition());

        godPower.setNewTurn(new NewTurn());
        switch (newTurn) {
            case "opponentsCantMoveUpAfterIDid":
                opponentsCantMoveUpAfterIDPlayer = numOfPlayer; break;
            case "": break;
        }

        return godPower;
    }

    /**
     * create a list of godPowers based on the number of players: he must generate two / three different random cards,
     * after building them, it changes some functions in the presence of divinities that modify the behavior of the adversaries
     * @param numOfPlayers player ID
     * @return List</GodPower>
     * @throws ParseException
     * @throws IOException
     */

    public static List<GodPower> createGodPowers (int numOfPlayers) throws ParseException, IOException {
        opponentsCantMoveUpAfterIDPlayer = 0;
        opponentsCantWinOnPerimeterPlayer = 0;
        List <GodPower> godPowerList = new ArrayList();
        List <String> godFiles = chooseGodFiles(numOfPlayers);

        for (int i = 1; i <= numOfPlayers; i++)
            godPowerList.add(power(godFiles.get(i-1), i));

        for (int i = 1; i <= numOfPlayers; i++) {
            if (opponentsCantWinOnPerimeterPlayer!=0 && numOfPlayers!=opponentsCantWinOnPerimeterPlayer){
                godPowerList.get(i-1).getBlockingWinConditions().add(new CantWinMovingOnPerimeter()); // changes the power of Hera's opponents
            }
            if (opponentsCantMoveUpAfterIDPlayer!=0 && numOfPlayers!=opponentsCantMoveUpAfterIDPlayer){
                //todo: modificare il potere degli avversari di Athena
            }
        }
        return godPowerList;
    }

}
