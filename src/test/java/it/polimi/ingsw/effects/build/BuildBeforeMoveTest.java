package it.polimi.ingsw.effects.build;

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

public class BuildBeforeMoveTest {
    int builds = 1;
    BuildBeforeMove buildBeforeMove = new BuildBeforeMove(builds);
    Cell workerCell;
    Turn turn;
    Board board;
    Player player = new Player("pippo",12);
    Worker worker = new Worker(player, 12);

    @BeforeEach
    void setUp(){
        turn = new Turn (player);
    }

    //negative
    @Test
    void buildConditionShouldReturnTrueAlsoIfTheWorkerDidNotMove(){
        workerCell = new Cell(0,0,0);
        workerCell.setWorker(worker);
        assertTrue(buildBeforeMove.checkBuildConditions(workerCell,turn));
    }

    //positive
    @Test
    void BuildConditionShouldThrowExceptionWithNullParameters() {
        assertThrows(NullPointerException.class, () -> {
            buildBeforeMove.checkBuildConditions(null, null);
        });
    }

}