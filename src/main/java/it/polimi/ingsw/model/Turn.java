package it.polimi.ingsw.model;

import java.util.Set;

/**
 * This class defines a turn of the game
 */

public class Turn {

    private Position workerStartingPosition = null;
    private Position firstBuildingPosition = null;

    private boolean moveBeforeBuild = false;
    private boolean buildAfterMove = false;
    private boolean buildBeforeMove = false;

    private boolean moveUp = false;
    private boolean moveDown = false;

    private int moveTimes = 0;
    private int buildTimes = 0;

    private int workerUsed = 0;
    private final int playerId;

    public Turn(Player player) {
        this.playerId = player.getId();
    }

    public boolean isMoveBeforeBuild() {
        return moveBeforeBuild;
    }

    public boolean isBuildAfterMove() {return buildAfterMove;}

    public boolean isMoveUp() {
        return moveUp;
    }

    public boolean isMoveDown() {
        return moveDown;
    }

    public int getMoveTimes() {
        return moveTimes;
    }

    public int getBuildTimes() {
        return buildTimes;
    }

    public int getWorkerUsed() {
        return workerUsed;
    }

    public int getPlayerId() {
        return playerId;
    }

    public boolean isBuildBeforeMove() {
        return buildBeforeMove;
    }

    public Position getWorkerStartingPosition() {
        return workerStartingPosition;
    }

    public Position getFirstBuildingPosition() {
        return firstBuildingPosition;
    }

    /**
     * Needs to be called after a move action to update the data
     * with the choices of the current player.
     *
     * @param startingPosition the position where the worker starts the turn
     * @param destinationPosition the position where the worker ends the turn
     * @param board the board object
     */

    public void updateTurnInfoAfterMove (Position startingPosition, Position destinationPosition, Board board) {
        Cell workerCell = board.getCell(startingPosition);
        Cell destinationCell = board.getCell(destinationPosition);


        //saving the the worker_id to ensure only this player will do something again in this turn
        if (moveTimes == 0 && !moveBeforeBuild) {
            this.workerStartingPosition = startingPosition;
            this.workerUsed = destinationCell.getWorkerId();
        }

        //This is set as true if this is the first move of the turn
        if (!moveBeforeBuild)
            this.moveBeforeBuild = true;

        //Verify if at least one time the worker has moved up
        if (verifyMoveUp(startingPosition.getZ(), destinationPosition.getZ()))
            this.moveUp = true;

        //Verify if at least one time the worker has moved down
        if (verifyMoveDown(startingPosition.getZ(), destinationPosition.getZ()))
            this.moveDown = true;

        this.moveTimes ++; //without condition, this method has to be call after a move action
    }


    /**
     * This method verifies if the worker moved up
     *
     * @param starting_Z z coordinate of the worker starting position
     * @param destination_Z z coordinate of the worker ending position
     * @return true if the worker moved up
     */
    protected boolean verifyMoveUp (int starting_Z, int destination_Z) {
        return (destination_Z - starting_Z)>=1;
    }

    /**
     * This method verifies if the worker moved down
     *
     * @param starting_Z z coordinate of the worker starting position
     * @param destination_Z z coordinate of the worker ending position
     * @return true if the worker moved down
     */
    protected boolean verifyMoveDown (int starting_Z, int destination_Z) {
        return (starting_Z - destination_Z)>=1;
    }

    /**
     * This method needs to be called after a build action to update the data
     * with the choices of the current player.
     *
     * @param buildingPosition the Cell where the worker has built
     */

    public void updateTurnInfoAfterBuild (Position buildingPosition, int workerId) {
        //set up the parameters after the standard build
        if(!moveBeforeBuild) {
            if(buildTimes==0&&moveTimes==0) {
                this.workerUsed = workerId;
            }
            this.buildBeforeMove = true;
            this.firstBuildingPosition = buildingPosition;
        }

        if (!buildAfterMove && moveBeforeBuild) {
            this.buildAfterMove = true;
            this.firstBuildingPosition = buildingPosition;
        }

        buildTimes ++;
    }

    public boolean canDecline () {
        return (this.moveBeforeBuild && this.buildAfterMove);
    }

    public boolean isBuildOptional (Set worker1builds, Set worker2builds) {
        return ((this.isBuildAfterMove()||
                !this.isMoveBeforeBuild())&&
                !(worker1builds.isEmpty()&&worker2builds.isEmpty()));
    }

    public boolean isMoveOptional (Set worker1moves, Set worker2moves) {
        return (this.isMoveBeforeBuild()&&
                !(worker1moves.isEmpty()&&worker2moves.isEmpty()));
    }
}


