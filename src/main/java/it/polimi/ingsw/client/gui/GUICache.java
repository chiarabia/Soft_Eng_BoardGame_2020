package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.GodCard;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class GUICache {
    int numberOfPlayers = 2;
    String playerName = "Unknown";
    List<GodCard> godPowers;


    public int getNumberOfPlayers(){return numberOfPlayers;}
    public String getPlayerName(){return playerName;}
    public List<GodCard> getGodPowers(){return godPowers;}

    public void setGodPowers(List<GodCard> godsNames){godPowers = godsNames;}
    public void setNumberOfPlayers(int number){numberOfPlayers = number;}
    public void setPlayerName(String name){playerName = name;}
}
