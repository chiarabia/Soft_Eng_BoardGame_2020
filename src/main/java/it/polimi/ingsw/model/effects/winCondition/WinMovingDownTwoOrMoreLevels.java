package it.polimi.ingsw.model.effects.winCondition;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Position;

/**
 * This class handles the victory with an action of moving two levels down
 */

public class WinMovingDownTwoOrMoreLevels extends StandardWinCondition {
    private static int DOWN_LEVELS = 2;

    /** This method is used for the win condition that enables the worker to also win
     *  by going down two levels
     * @return true if the Player has won, false if the win condition is not met yet
     * @param workerPosition the worker's Cell before the move
     * @param board the board of the game
     * @param destinationPosition the worker's Cell after the move
     */

    @Override
    public boolean win (Position workerPosition, Position destinationPosition, Board board){
        if (workerPosition==null &&  destinationPosition==null) return false;
        Cell workerCell = board.getCell(workerPosition);
        Cell destinationCell = board.getCell(destinationPosition);
        return ((workerCell.getZ() - destinationCell.getZ()) >= DOWN_LEVELS);
    }
}
