package it.polimi.ingsw.effects.move;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import it.polimi.ingsw.Board;
import it.polimi.ingsw.Cell;
import it.polimi.ingsw.Player;
import it.polimi.ingsw.Worker;
import it.polimi.ingsw.Turn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

public class NoMoveUpAfterBuildTest {
    int moves = 1;
    NoMoveUpAfterBuild noMoveUpAfterBuild = new NoMoveUpAfterBuild(moves);
    Cell workerCell;
    Turn turn;
    Board board;
    Player player = new Player("pippo",12);
    Worker worker = new Worker(player, 12);

    @BeforeEach
    void setUp(){
        turn = new Turn(player);
        board = new Board();
    }

    //negative
    //gives the the cell (0,1,1) as available
    @Test
    void playerShouldNotBeAbleToMoveUp(){
        Cell buildCell = board.getCell(1,1,0);
        turn.updateTurnInfoAfterBuild(buildCell.getPosition());
        board.getCell(1,1,0).setBuilding(true);
        workerCell = board.getCell(0,0,0);
        workerCell.setWorker(worker);
        board.getCell(0,1,0).setBuilding(true);
        board.newCell(0,1,1);
        Set<Cell> collect = new HashSet<>();
        collect.add(new Cell(1,0,0));
        assertEquals(collect,noMoveUpAfterBuild.move(workerCell.getPosition(),board,turn));
    }
}
