package it.polimi.ingsw.effects.build;

import it.polimi.ingsw.Board;
import it.polimi.ingsw.Cell;
import it.polimi.ingsw.Position;
import it.polimi.ingsw.Turn;

import java.util.Set;

/**
 * This class defines the possibility to build before moving
 */

public class BuildBeforeMove extends StandardBuild {

    public BuildBeforeMove(int builds) {
        super(builds);
    }

    //TODO: Non sono sicuro che chiamare la classe padre, mi permetta di usare il metodo canIbuild con Override, bisognerebbe fare testing
    @Override
    public Set<Position> build(Position workerPosition, Board board, Turn turn) {
        return super.build(workerPosition, board, turn);
    }




    @Override
    protected boolean checkBuildConditions(Cell workerCell, Turn turn) {
        if (!workerCell.isWorker()) //robusto, devo invocare il metodo su un worker
            return false;

        if (workerCell.getPlayerId() != turn.getPlayerId()) //il player deve essere uguale
            return false;

        //Posso costruire prima di muovermi

        if (turn.getWorkerUsed() != 0 && workerCell.getWorkerId() != turn.getWorkerUsed()) //if the id doesn't match, false
            return false;

        if(!turn.isMoveBeforeBuild())
            return false;
        else
            return (turn.getBuildTimes() < builds);

    }
}
