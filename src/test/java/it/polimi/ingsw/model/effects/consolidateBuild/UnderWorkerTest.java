package it.polimi.ingsw.model.effects.consolidateBuild;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.controller.server.serializable.SerializableUpdateActions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UnderWorkerTest {
    UnderWorker underWorker = new UnderWorker();
    Board board;
    Cell workerCell;
    Cell destinationCell;
    Player player = new Player("pippo",12);
    Worker worker = new Worker(player, 12);
    SerializableUpdateActions updateInfos;

    @BeforeEach
    void setUP () {
        board = new Board();
        updateInfos = null;
    }

    @Test
    void BuildUpShouldForceWorkerAbove () {
        workerCell = board.getCell(2,2,0);
        workerCell.setWorker(worker);
        updateInfos = underWorker.buildUp(workerCell.getPosition(), board, false);
        destinationCell =  board.getCell(2,2,1);
        assertAll ("underWorker",  () -> assertTrue(destinationCell.isWorker()),
                () -> assertEquals(destinationCell.getWorker(), worker),
                () -> assertNull(workerCell.getWorker()),
                () -> assertEquals(updateInfos.getUpdateBuild().get(0).getNewPosition(), workerCell.getPosition()),
                () -> assertEquals(updateInfos.getUpdateMove().get(0).getNewPosition(), destinationCell.getPosition()));
    }


}
