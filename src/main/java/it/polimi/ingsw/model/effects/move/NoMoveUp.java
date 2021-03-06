package it.polimi.ingsw.model.effects.move;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Position;
import it.polimi.ingsw.model.Turn;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class implements a movement that cannot be a move up action
 */


public class NoMoveUp extends StandardMove {
    private StandardMove decoratedMove;

    public NoMoveUp (StandardMove decoratedMove){
        super(decoratedMove.moves);
        this.decoratedMove = decoratedMove;
    }

    /**Excludes positions placed at a higher height than the worker in the workerPosition.
     * @param workerPosition the worker's Cell
     * @param board the board
     * @param turn the player's turn
     * @return a <code>Set&lt;Cell&gt;</code> collect that only has the cells where the player can move
     * without the initialPosition
     */

    @Override
    public Set<Position> move (Position workerPosition, Board board, Turn turn) {
        Cell workerCell = board.getCell(workerPosition);
        Set<Position> standardMove = decoratedMove.move(workerPosition, board, turn);
        return standardMove.stream()
                    .filter(a -> a.getZ() <= workerCell.getZ())
                    .collect(Collectors.toSet());

    }

    @Override
    protected int heightsDifference(int z_worker, int z_cell) {
        return decoratedMove.heightsDifference(z_worker, z_cell);
    }
}

