package it.polimi.ingsw.model.effects.move;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Position;
import it.polimi.ingsw.model.Turn;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * This class implements a movement where the worker can't move on the original
 * position
 */

public class MoveNotOnInitialPosition extends StandardMove{
    public MoveNotOnInitialPosition(int moves) {
        super(moves);
    }

    /**Excludes the first position from the next possible moves
     * @param workerPosition the worker's Cell
     * @param board the board
     * @param turn the player's turn
     * @return a <code>Set&lt;Cell&gt;</code> collect that only has the cells where the player can move
     * without the initialPosition
     */

    @Override
    public Set<Position> move (Position workerPosition, Board board, Turn turn) {
        Cell workerCell = board.getCell(workerPosition);

        if (!checkMoveConditions(workerCell, turn)) return new HashSet<>();
        else {
            Position initialPosition = turn.getWorkerStartingPosition();

            if (initialPosition != null) {
                return super.move(workerPosition, board, turn)
                        .stream()
                        .filter(a -> !(a.getX()==initialPosition.getX()&& a.getY()==initialPosition.getY()))
                        .collect(Collectors.toSet());
            }
            //if the player didn't already move they can move normally
            else return super.move(workerPosition, board, turn);
        }
    }


    //The player needs to move two times in a row
    @Override
    protected boolean checkMoveConditions(Cell workerCell, Turn turn) {
        if (turn.isMoveBeforeBuild() && turn.isBuildAfterMove())
            return false;
        else
            return super.checkMoveConditions(workerCell, turn);
    }
}
