package it.polimi.ingsw.model.effects.consolidateMove;

import it.polimi.ingsw.model.Board;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.Turn;

import it.polimi.ingsw.controller.server.serializable.SerializableUpdateActions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.*;


public class StandardConsolidateMoveTest {
    StandardConsolidateMove standardConsolidateMove = new StandardConsolidateMove();
    Cell workerCell;
    Cell destinationCell;
    Board board;
    Turn turn;
    Player player = new Player("pippo",12);
    Worker worker = new Worker(player, 12);
    SerializableUpdateActions updateInfos;

    @BeforeEach
    void setUp(){
        turn = new Turn(player);
        board = new Board();
        updateInfos = null;
    }

    //positive
    @Test
    void moveIntoShouldSaveTheRightCellWhenMovingOnSameLevel() {
        board.newCell(0,1,1);
        workerCell = board.getCell(0,1,1);
        workerCell.setWorker(worker);
        board.newCell(1,1,1);
        destinationCell = board.getCell(1,1,1);
        updateInfos = standardConsolidateMove.moveInto(board, workerCell.getPosition(), destinationCell.getPosition());
        assertAll("MovingOnSameLevel", () -> assertTrue(destinationCell.isWorker()),
                () -> assertEquals(1, updateInfos.getUpdateMove().size()),
                () -> assertSame(updateInfos.getUpdateMove().get(0).getNewPosition(), destinationCell.getPosition()),
                () -> assertSame(updateInfos.getUpdateMove().get(0).getStartingPosition(), workerCell.getPosition()));
    }

    //positive
    @Test
    void moveIntoShouldSaveTheRightWorkerWhenMovingOnSameLevel(){
        board.newCell(0,1,1);
        workerCell = board.getCell(0,1,1);
        workerCell.setWorker(worker);
        board.newCell(1,1,1);
        destinationCell = board.getCell(1,1,1);
        standardConsolidateMove.moveInto(board, workerCell.getPosition(), destinationCell.getPosition());
        assertSame(worker, destinationCell.getWorker());
    }

    //positive
    @Test
    void moveIntoShouldRemoveTheWorkerFromTheStartingCellWhenMovingOnSameLevel(){
        board.newCell(0,1,1);
        workerCell = board.getCell(0,1,1);
        workerCell.setWorker(worker);
        board.newCell(1,1,1);
        destinationCell = board.getCell(1,1,1);
        standardConsolidateMove.moveInto(board, workerCell.getPosition(), destinationCell.getPosition());
        assertFalse(workerCell.isWorker());
    }

    //positive
    @Test
    void moveIntoShouldSaveTheRightCellWhenMovingUp(){
        board.newCell(0,1,0);
        workerCell = board.getCell(0,1,0);
        workerCell.setWorker(worker);
        board.newCell(1,1,1);
        destinationCell = board.getCell(1,1,1);
        standardConsolidateMove.moveInto(board, workerCell.getPosition(), destinationCell.getPosition());
        assertTrue(destinationCell.isWorker());
    }

    //positive
    @Test
    void moveIntoShouldSaveTheRightWorkerWhenMovingUp(){
        board.newCell(0,1,0);
        workerCell = board.getCell(0,1,0);
        workerCell.setWorker(worker);
        board.newCell(1,1,1);
        destinationCell = board.getCell(1,1,1);
        standardConsolidateMove.moveInto(board, workerCell.getPosition(), destinationCell.getPosition());
        assertSame(worker, destinationCell.getWorker());
    }

    //positive
    @Test
    void moveIntoShouldRemoveTheWorkerFromTheStartingCellWhenMovingUp(){
        board.newCell(0,1,0);
        workerCell = board.getCell(0,1,0);
        workerCell.setWorker(worker);
        board.newCell(1,1,1);
        destinationCell = board.getCell(1,1,1);
        standardConsolidateMove.moveInto(board, workerCell.getPosition(), destinationCell.getPosition());
        assertFalse(workerCell.isWorker());
    }

    //positive
    @Test
    void moveIntoShouldSaveTheRightCellWhenMovingDown() {
        board.newCell(0,1,1);
        workerCell = board.getCell(0,1,1);
        workerCell.setWorker(worker);
        board.newCell(1,1,0);
        destinationCell = board.getCell(1,1,0);
        standardConsolidateMove.moveInto(board, workerCell.getPosition(), destinationCell.getPosition());
        assertTrue(destinationCell.isWorker());
    }

    //positive
    @Test
    void moveIntoShouldSaveTheRightWorkerWhenMovingDown(){
        board.newCell(0,1,1);
        workerCell = board.getCell(0,1,1);
        workerCell.setWorker(worker);
        board.newCell(1,1,0);
        destinationCell = board.getCell(1,1,0);
        standardConsolidateMove.moveInto(board, workerCell.getPosition(), destinationCell.getPosition());
        assertSame(worker, destinationCell.getWorker());
    }

    //positive
    @Test
    void moveIntoShouldRemoveTheWorkerFromTheStartingCellWhenMovingDown(){
        board.newCell(0,1,1);
        workerCell = board.getCell(0,1,1);
        workerCell.setWorker(worker);
        board.newCell(1,1,0);
        destinationCell = board.getCell(1,1,0);
        standardConsolidateMove.moveInto(board, workerCell.getPosition(), destinationCell.getPosition());
        assertFalse(workerCell.isWorker());
    }


    //positive
    @Test
    void moveIntoThrowsExceptionWithNullParameters() {
        assertThrows(NullPointerException.class, () -> {
            standardConsolidateMove.moveInto(null, null, null);
        });
    }
}
