package it.polimi.ingsw.effects.consolidateBuild;

import it.polimi.ingsw.Board;
import it.polimi.ingsw.Cell;
import it.polimi.ingsw.Position;
import it.polimi.ingsw.server.serializable.SerializableUpdateBuild;
import it.polimi.ingsw.server.serializable.SerializableUpdateActions;

public class StandardConsolidateBuild {
    public SerializableUpdateActions buildUp(Position buildingPosition, Board board, boolean forceDome) {
        Cell tempCell = board.getCell(buildingPosition);

        if (forceDome) tempCell.setDome(true);
        else {
            if (buildingPosition.getZ()<3) {
                tempCell.setBuilding(true);
                board.newCell(buildingPosition.getX(), buildingPosition.getY(), buildingPosition.getZ() + 1);
            }
            else tempCell.setDome(true);
        }

        SerializableUpdateBuild updateBuild = new SerializableUpdateBuild(buildingPosition, board.getCell(buildingPosition).isDome());
        SerializableUpdateActions serializableUpdateActions = new SerializableUpdateActions();
        serializableUpdateActions.addBuildAction(updateBuild);
        return serializableUpdateActions;
    }

}
