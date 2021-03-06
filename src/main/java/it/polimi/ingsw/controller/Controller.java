package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.server.serializable.*;
import it.polimi.ingsw.model.effects.GodPower;
import it.polimi.ingsw.model.effects.GodPowerManager;
import it.polimi.ingsw.model.effects.winCondition.StandardLoseCondition;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.controller.server.ProxyObserver;
import it.polimi.ingsw.controller.server.ServerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class implements the controller of the server.
 * It's notified by ServerView and manages the match, it chooses which action every player needs to make,
 * it reacts to the players' actions by checking if they are correct and consequently it modifies the game model.
 * <p>The initialization procedure follows this order:
 * <p><ul>
 * <li><code>onInitialization()</code>
 * <li><code>onGodPowerInitialization(int playerId, String godPower)</code>: called for each player
 * <li><code>onWorkerPositionsInitialization()</code>
 * <li><code>onWorkerPositionsInitialization(int playerId, List&lt;Position&gt; workerPositions)</code>: called for each player
 * </ul>
 * After the initialization procedure <code>nextOperation()</code> is called.
 */
public class Controller implements ProxyObserver {
    private Game game;
    private List <GodPower> godPowersLeft;
    private ServerView serverView;
    private boolean isDisconnected = false;

    public Controller(Game game, ServerView serverView) {
        this.game = game;
        this.serverView = serverView;
    }

    /**Decides which player should perform which action, relying on Turn and GodPower information */
    public void nextOperation(){
        int playerId = getTurn().getPlayerId();
        boolean canForceDome = getPlayerGodPower(playerId).isAskToBuildDomes();
        Position worker1Position = getWorkerPosition(playerId, 1);
        Position worker2Position = getWorkerPosition(playerId, 2);

        Set<Position> worker1Moves  =  getPlayerGodPower(playerId).move(worker1Position , getBoard(), getTurn());
        Set<Position> worker1Builds =  getPlayerGodPower(playerId).build(worker1Position, getBoard(), getTurn());
        Set<Position> worker2Moves  =  getPlayerGodPower(playerId).move(worker2Position , getBoard(), getTurn());
        Set<Position> worker2Builds =  getPlayerGodPower(playerId).build(worker2Position, getBoard(), getTurn());

        if (checkLose(playerId, worker1Moves, worker1Builds, worker2Moves, worker2Builds, getTurn())) return;
        if (checkWin (playerId, null, null)) return;

        SerializableRequest request = new SerializableRequestAction(playerId,
                                            getTurn().isMoveOptional(worker1Moves, worker2Moves),
                                            getTurn().isBuildOptional(worker1Builds, worker2Builds),
                                            getTurn().canDecline(),
                                            worker1Moves, worker2Moves,
                                            worker1Builds, worker2Builds,
                                            canForceDome);

        game.notifyAnswerOnePlayer(request);
    }

    /**Terminates the current turn
     *@param playerId player who asked to end the turn
     */
    @Override
    public void onEndedTurn (int playerId) {
        if (!(playerId == getTurn().getPlayerId())){ // INITIAL CHECK
            game.notifyAnswerOnePlayer(new SerializableRequestError(playerId, "REJECTED"));
            return;
        }

        int nextPlayerId = nextPlayerId(playerId);
        Turn newTurn = getPlayerGodPower(playerId).endTurn(getTurn(), getGodPowers(), getPlayer(nextPlayerId));
        game.setTurn(newTurn);
        SerializableUpdate update = new SerializableUpdateTurn(nextPlayerId, false);
        game.notifyJustUpdateAll(update);
        nextOperation();
    }

    /**Handles a ConsolidateMove object received from a client
     *@param playerId player who sent the object
     *@param workerId workerId to be modified
     *@param newPosition position chosen by player*/
    @Override
    public void onConsolidateMove(int playerId, int workerId, Position newPosition) {
        Position worker1Position = getWorkerPosition(getTurn().getPlayerId(), 1);
        Position worker2Position = getWorkerPosition(getTurn().getPlayerId(), 2);
        Set<Position> worker1Moves  =  getPlayerGodPower(getTurn().getPlayerId()).move(worker1Position , getBoard(), getTurn());
        Set<Position> worker2Moves  =  getPlayerGodPower(getTurn().getPlayerId()).move(worker2Position , getBoard(), getTurn());

        if (!(playerId == getTurn().getPlayerId() && // INITIAL CHECK
            (workerId == 1 || workerId == 2) &&
            newPosition != null &&
            (workerId==1 && worker1Moves.stream().anyMatch(x->x.equals(newPosition)) || workerId==2 && worker2Moves.stream().anyMatch(x->x.equals(newPosition))) )){
                game.notifyAnswerOnePlayer(new SerializableRequestError(playerId, "REJECTED"));
                return;
        }

        Board board = getBoard();
        Position workerPosition = getWorkerPosition(playerId, workerId);
        SerializableUpdate update = getPlayerGodPower(playerId).moveInto(board, workerPosition, newPosition);
        getTurn().updateTurnInfoAfterMove(workerPosition, newPosition, board);
        game.notifyJustUpdateAll(update);

        checkWin(playerId, workerPosition, newPosition);
        nextOperation();
    }

    /**Handles a ConsolidateBuild object received from a client
     *@param playerId player who sent the object
     *@param forceDome true if client asked to force a dome
     *@param newPosition position chosen by player*/
    @Override
    public void onConsolidateBuild(int playerId,int workerID,  Position newPosition, boolean forceDome) {
        Position worker1Position = getWorkerPosition(getTurn().getPlayerId(), 1);
        Position worker2Position = getWorkerPosition(getTurn().getPlayerId(), 2);
        Set<Position> worker1Builds =  getPlayerGodPower(getTurn().getPlayerId()).build(worker1Position, getBoard(), getTurn());
        Set<Position> worker2Builds =  getPlayerGodPower(getTurn().getPlayerId()).build(worker2Position, getBoard(), getTurn());

        if (!(playerId == getTurn().getPlayerId() && // INITIAL CHECK
            newPosition != null &&
            (!forceDome || getPlayerGodPower(playerId).isAskToBuildDomes()) &&
            (worker1Builds.stream().anyMatch(x->x.equals(newPosition))||worker2Builds.stream().anyMatch(x->x.equals(newPosition))) )){
                game.notifyAnswerOnePlayer(new SerializableRequestError(playerId, "REJECTED"));
                return;
        }


        Board board = getBoard();
        SerializableUpdate update = getPlayerGodPower(playerId).buildUp(newPosition, board, forceDome);
        getTurn().updateTurnInfoAfterBuild(newPosition, workerID);
        game.notifyJustUpdateAll(update);
        nextOperation();
    }

    /**Handles a client disconnection
     *@param playerId player who disconnected*/
    @Override
    public void onPlayerDisconnection(int playerId) {
        if (getPlayer(playerId)!=null && !isDisconnected) {
            isDisconnected = true;
            SerializableUpdate update = new SerializableUpdateDisconnection(playerId);
            game.notifyJustUpdateAll(update);
            serverView.stopAllEventGenerators();
        }
    }

    /**Reports to the players that someone has won
     *@param playerId player who has won*/
    public void onPlayerWin (int playerId)  {
        isDisconnected = true;
        game.notifyJustUpdateAll(new SerializableUpdateWinner(playerId));
        serverView.stopAllEventGenerators();
    }

    /**Reports to the players that someone has lost
     *@param playerId player who has lost*/
    public void onPlayerLoss(int playerId)  {
        int nextPlayerId = nextPlayerId(playerId);
        List<GodPower> godPowerList = getGodPowers();

        Turn newTurn = getPlayerGodPower(playerId).endTurn(getTurn(), godPowerList, getPlayer(nextPlayerId));
        game.setTurn(newTurn);
        removePlayerInfos(playerId);

        List<SerializableUpdate> tempUpdates = new ArrayList<>();
        tempUpdates.add( new SerializableUpdateLoser(playerId));
        tempUpdates.add(new SerializableUpdateTurn(nextPlayerId, false));
        game.notifyJustUpdateAll(tempUpdates);
        nextOperation();
    }

    /**Starts initialization procedures and asks the first player to choose a GodPower.
     * <p>It's the first called controller method */
    @Override
    public void onInitialization(){
        System.out.println("Game initialization started");
        try {
            godPowersLeft = GodPowerManager.createGodPowers(getNumOfPlayers());
            List<String> godPowersNames = getGodPowersLeftNames();
            List<String> playersNames = new ArrayList<>();
            for (Player player : getPlayers()) playersNames.add(player.getName());
            SerializableUpdateInitializeNames update = new SerializableUpdateInitializeNames(playersNames);
            SerializableRequest request = new SerializableRequestInitializeGodPower(1, godPowersNames);
            game.notifyUpdateAllAndAnswerOnePlayer(update, request);
        } catch (Exception e){}
    }

    /**Proceeds the initialization with the godPowers.
     * Sets the god power chosen by the player and asks the next player to chose a remaining one,
     * or, if no players are left, it starts the initialization of the workers' <code>Position</code>.
     *@param playerId player who has chosen god power
     *@param godPower name of the god power chosen */
    @Override
    public void onGodPowerInitialization(int playerId, String godPower) {
        if (!(playerId > 0 && playerId <= getNumOfPlayers() && // INITIAL CHECK
                getGodPowers().size() == playerId - 1 &&
                godPower != null &&
                godPowersLeft.stream().map(GodPower::getGodName).anyMatch(x->x.equals(godPower)))){
            game.notifyAnswerOnePlayer(new SerializableRequestError(playerId, "REJECTED"));
            return;
        }

        chooseGodPower(godPower);

        SerializableUpdateInitializeGodPower update = new SerializableUpdateInitializeGodPower(godPower, playerId);
        if (playerId == getPlayers().size()){
            game.notifyJustUpdateAll(update);
            onWorkerPositionsInitialization();
        } else {
            SerializableRequest request = new SerializableRequestInitializeGodPower(playerId + 1, getGodPowersLeftNames());
            game.notifyUpdateAllAndAnswerOnePlayer(update, request);
        }
    }


    /**Starts the initialization of the workers' <code>Position</code> by asking the first player to choose its workers' <code>Position</code>*/
    public void onWorkerPositionsInitialization(){
        try {
            SerializableRequest request = new SerializableRequestInitializeWorkerPositions(getBoard()
                    .getStream()
                    .filter(Cell::isFree)
                    .map(Cell::getPosition)
                    .collect(Collectors.toList()), 1);
            game.notifyAnswerOnePlayer(request);
        } catch (Exception e){}
    }

    /**Proceeds with the workers <code>Position</code> initialization.
     * Sets the <code>Position</code> chosen by a player and asks next player to chose, or, if no players are left,the first turn of the game starts.
     *@param playerId player who has chosen positions
     *@param workerPositions positions chosen */
    @Override
    public void onWorkerPositionsInitialization(int playerId, List<Position> workerPositions) {
        if (!(playerId > 0 && playerId <= getNumOfPlayers() && // INITIAL CHECK
                getPlayer(playerId) !=null &&
                workerPositions != null &&
                workerPositions.size() == 2 &&
                !workerPositions.get(0).equals(workerPositions.get(1)) &&
                workerPositions.stream().allMatch(x->x.getZ()==0) &&
                !getPlayer(playerId).areWorkersSet() &&
                ((playerId > 1 && getPlayer(playerId-1).areWorkersSet())||playerId==1) &&
                getBoard().isFreeZone(workerPositions.get(0).getX(), workerPositions.get(0).getY()) &&
                getBoard().isFreeZone(workerPositions.get(1).getX(), workerPositions.get(1).getY()) )){
            game.notifyAnswerOnePlayer(new SerializableRequestError(playerId, "REJECTED"));
            return;
        }

        Player player = getPlayer(playerId);
        Worker worker1 = new Worker(player, 1);
        Worker worker2 = new Worker(player, 2);
        player.addWorker(worker1);
        player.addWorker(worker2);
        Cell worker1Cell = getBoard().getCell(workerPositions.get(0));
        Cell worker2Cell = getBoard().getCell(workerPositions.get(1));
        worker1Cell.setWorker(worker1);
        worker2Cell.setWorker(worker2);

        SerializableUpdateInitializeWorkerPositions update = new SerializableUpdateInitializeWorkerPositions(workerPositions, playerId);
        if (playerId == getPlayers().size()){
            SerializableUpdateTurn updateTurn = new SerializableUpdateTurn(1, true);
            game.setTurn(new Turn(getPlayers().get(0)));
            List <SerializableUpdate> tempUpdates = new ArrayList<>();
            tempUpdates.add(update);
            tempUpdates.add(updateTurn);
            game.notifyJustUpdateAll(tempUpdates);
            System.out.println("Game started");
            nextOperation();
        } else {
            SerializableRequest request = new SerializableRequestInitializeWorkerPositions(getBoard().getStream().filter(Cell::isFree).map(Cell::getPosition).collect(Collectors.toList()),playerId + 1);
            game.notifyUpdateAllAndAnswerOnePlayer(update, request);
        }
    }


    /**Checks if a player has won
     *@param playerId player to be checked
     *@param workerPosition beginning cell of a worker's move, null if no movement is happening
     *@param destinationPosition final cell of a worker's move, null if no movement is happening*/
    private boolean checkWin (int playerId, Position workerPosition, Position destinationPosition) {
        if (getPlayers().stream().filter(Objects::nonNull).count()==1) {
            onPlayerWin(playerId);
            return true;
        }
        if (workerPosition == null && destinationPosition == null) {
            for (GodPower p: getGodPowers()) {
                if (p!=null) {
                    if (p.win(null, null, getBoard())) {
                        onPlayerWin(p.getPlayerId());
                        return true;
                    }
                }
            }
        }
        else {
            if (getPlayerGodPower(playerId).win(workerPosition, destinationPosition, getBoard())) {
                onPlayerWin(playerId);
                return true;
            }
        }
        return false;
    }

    /**Checks if a player has lost
     *@param turn current turn
     *@param playerId player to be checked
     *@param worker1Moves worker 1 moves
     *@param worker1Builds worker 1 builds
     *@param worker2Moves worker 2 moves
     *@param worker2Builds worker 2 builds */
    private boolean checkLose (int playerId, Set<Position> worker1Moves, Set<Position> worker1Builds, Set<Position> worker2Moves, Set<Position> worker2Builds, Turn turn) {
        StandardLoseCondition playerLoseCondition = getPlayerGodPower(playerId).getLoseCondition();

        if (!turn.canDecline() && playerLoseCondition.lose(worker1Moves, worker1Builds) && playerLoseCondition.lose(worker2Moves, worker2Builds)) {
            onPlayerLoss(playerId);
            return true;
        }
        return false;
    }

    /**Returns a list of names of godPowers that haven't been chosen yet
     * @return <code>List&lt;String&gt;</code>
     */
    private List<String> getGodPowersLeftNames (){
        List<String> godPowersNames = new ArrayList<>();
        for (GodPower godPower: godPowersLeft) godPowersNames.add(godPower.getGodName());
        return godPowersNames;
    }

    /**Extracts a godPower from the list of the godPowers that haven't been chosen yet,
     * and then it adds it to the godPowers list in the game model
     *@param godPower name of the chosen god power
     */
    private void chooseGodPower(String godPower){
        for (int i = 0; i < godPowersLeft.size(); i++){
            if (godPowersLeft.get(i).getGodName().equals(godPower)){
                getGodPowers().add(godPowersLeft.get(i));
                godPowersLeft.remove(i);
                break;
            }
        }
    }

    /** Sets the GodPower and Player to null and removes the workers of the player
     * @param playerId player to be removed
     */
    private void removePlayerInfos (int playerId) {
        getBoard().getCell(getWorkerPosition(playerId, 1)).setWorker(null);
        getBoard().getCell(getWorkerPosition(playerId, 2)).setWorker(null);
        game.removeGodPower(playerId);
        game.removePlayer(playerId);
    }

    /**Returns the player next to the one passed in the argument
     * @param playerId player ID
     */
    private int nextPlayerId(int playerId){
        int firstPlayerId = (playerId % getNumOfPlayers()) + 1;
        for (int i = firstPlayerId; i < firstPlayerId + getNumOfPlayers() - 1; i++)
            if (getPlayer((i-1) %getNumOfPlayers()+1) != null) return ((i-1) % getNumOfPlayers()) +1;
        return playerId;
    }

    private int getNumOfPlayers() {return game.getNumOfPlayers();}
    private Board getBoard() { return game.getBoard(); }
    private Turn getTurn() { return game.getTurn(); }
    private List<Player> getPlayers() { return game.getPlayers(); }
    private List<GodPower> getGodPowers() { return game.getGodPowers(); }
    private Player getPlayer(int playerId) {return game.getPlayer(playerId); }
    private GodPower getPlayerGodPower(int playerId) { return game.getPlayerGodPower(playerId); }
    private Position getWorkerPosition(int playerId, int workerId) { return game.getWorkerPosition(playerId, workerId); }
    public List<GodPower> getGodPowersLeft() { return godPowersLeft; }
}
