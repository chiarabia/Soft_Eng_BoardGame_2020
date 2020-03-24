package it.polimi.ingsw.Effects;

import it.polimi.ingsw.Board;
import it.polimi.ingsw.Cell;
import it.polimi.ingsw.Turn;

import java.util.Set;
import java.util.stream.Collectors;

public class SwapMove extends StandardMove {
    @Override
    public Set<Cell> move(Cell workerCell, Board board, Turn turn) {
        final Set<Cell> collect = board.getStream()
                .filter(a -> a.isFree() || a.isWorker())
                .filter(a -> !a.equals(workerCell))
                .filter(a -> a.getX()<=workerCell.getX()+1)
                .filter(a -> a.getX()>=workerCell.getX()-1)
                .filter(a -> a.getY()<=workerCell.getY()+1)
                .filter(a -> a.getY()>=workerCell.getY()-1)
                .filter(a -> heightsDifference(workerCell.getZ(), a.getZ()) <= 1)
                .filter(a -> !(a.getWorkerID()==workerCell.getWorkerID())) // toglie l'altro worker dall'insieme di caselle disponibili
                .collect(Collectors.toSet());
        return collect;
    }
}
