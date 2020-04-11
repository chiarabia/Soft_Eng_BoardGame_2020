package it.polimi.ingsw.effects.build;

import it.polimi.ingsw.Board;
import it.polimi.ingsw.Cell;
import it.polimi.ingsw.Turn;

import java.util.Set;
import java.util.stream.Collectors;

public class NotOnPerimeter extends StandardBuild {
    public Set<Cell> build(Cell workerCell, Board board, Turn turn) {
        Set<Cell> temp_cells = super.build(workerCell, board, turn);

        if(turn.getBuildTimes()>0)
            return temp_cells.stream()
                            .filter(a->!a.isPerimetral())
                            .collect(Collectors.toSet());
        else return temp_cells;
    }

    public NotOnPerimeter(int builds) {
        super(builds);
    }
}