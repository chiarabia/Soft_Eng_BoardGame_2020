package it.polimi.ingsw.controller.server.serializable;

public class SerializableUpdateLoser implements SerializableUpdate {
    private final int playerId;

    public int getPlayerId() {
        return playerId;
    }

    public SerializableUpdateLoser(int playerId) {
        this.playerId = playerId;
    }
}
