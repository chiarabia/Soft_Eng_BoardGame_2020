package it.polimi.ingsw.client;

import it.polimi.ingsw.exceptions.GameEndedException;
import it.polimi.ingsw.server.serializable.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


/**
 * Contiene metodi di invio messaggi e gestisce la ricezione
 */
public class ClientCommunicator extends Thread {
    private final Socket serverSocket;
    private List<Client> observerList = new ArrayList<>();
    public void addObserver(Client client){observerList.add(client);}

    public Object waitForObject () throws IOException, ClassNotFoundException {
        ObjectInputStream fileObjectIn = new ObjectInputStream(serverSocket.getInputStream());
        return fileObjectIn.readObject();
    }

    public void sendObject (Object object) {
        try {
            ObjectOutputStream fileObjectOut = new ObjectOutputStream(serverSocket.getOutputStream());
            fileObjectOut.writeObject(object);
            fileObjectOut.flush();
        } catch (Exception e){}
    }

    public void sendMessage (String message) {
        sendObject(new Message(message));
    }

    public String waitForMessage () throws IOException, ClassNotFoundException {
        return ((Message) waitForObject()).getMessage();
    }

    public void stopProcess(){
        try { serverSocket.close(); } catch (Exception e){}
    }

    public void run(){
        try {
            while (true) { reactToServer(waitForObject()); }
        } catch (GameEndedException e) {stopProcess();
        } catch (Exception e) {
            for(int i = 0; i<observerList.size(); i++)observerList.get(i).onError();
            stopProcess();
        }
    }

    public ClientCommunicator(int port, String IP) throws IOException {
        serverSocket = new Socket(IP, port);
    }

    //metodo principale, gestisce tutti messaggi col server e invia le risposte al server
    private void reactToServer(Object object) throws Exception {
        if (object instanceof SerializableUpdateInitializeGame) { //ricevo un oggetto contente i GodPower dei vari giocatori che vengono aggiunti alla board
            for(int i = 0; i<observerList.size(); i++)observerList.get(i).onUpdateInitializeGame((SerializableUpdateInitializeGame) object);
        }
        if (object instanceof SerializableRequestInitializeGame) { // chiedo quale GodPower il plauer voglia scegleire
            for(int i = 0; i<observerList.size(); i++)observerList.get(i).onRequestInitializeGame((SerializableRequestInitializeGame) object);
        }
        if (object instanceof SerializableUpdateMove) {   //messaggio che ricevo dopo aver consolidato una move
            for(int i = 0; i<observerList.size(); i++)observerList.get(i).onUpdateMove((SerializableUpdateMove) object);
        }
        if (object instanceof SerializableUpdateBuild) {//messaggio che ricevo dopo aver consolidato una build
            for(int i = 0; i<observerList.size(); i++)observerList.get(i).onUpdateBuild((SerializableUpdateBuild) object);
        }
        //in questo messaggio sono contenute le informazioni sulle mosse disponibili per entrambi i workers
        //le informazioni riguardo all'opzionalità delle suddette azioni, se il player è in condizioni di passare il turno oppure no
        if (object instanceof SerializableRequestAction) {
            for(int i = 0; i<observerList.size(); i++)observerList.get(i).onRequestAction((SerializableRequestAction) object);
        }
        if (object instanceof SerializableUpdateTurn) { //messaggio che ricevo quando cambio il turno
            for(int i = 0; i<observerList.size(); i++)observerList.get(i).onUpdateTurn((SerializableUpdateTurn) object);
        }
        if (object instanceof SerializableUpdateInitializeNames){
            for(int i = 0; i<observerList.size(); i++)observerList.get(i).onUpdateInitializeNames((SerializableUpdateInitializeNames) object);
        }
        if (object instanceof SerializableUpdateLoser) { //messaggio che mosra che un gicoatore ah perso
            for(int i = 0; i<observerList.size(); i++)observerList.get(i).onUpdateLoser ((SerializableUpdateLoser) object);
        }
        if (object instanceof SerializableUpdateWinner) { //messagio dopo vincita
            for(int i = 0; i<observerList.size(); i++)observerList.get(i).onUpdateWinner((SerializableUpdateWinner) object);
        }
        if (object instanceof SerializableUpdateDisconnection) { //messaggio se salta la connessione
            for(int i = 0; i<observerList.size(); i++)observerList.get(i).onUpdateDisconnection((SerializableUpdateDisconnection) object);
        }
        if (object instanceof Message){
            if (((Message) object).getMessage().equals("Hello")) {
                for (int i = 0; i < observerList.size(); i++) observerList.get(i).onHello();
            }
            else if (((Message) object).getMessage().equals("ERROR_NOT_VALID_NAME")) {
                for (int i = 0; i < observerList.size(); i++) observerList.get(i).onNotValidNameError();
            }
            else {
                for(int i = 0; i<observerList.size(); i++)observerList.get(i).onPlayerIdAssigned(((Message) object).getMessage());
            }
        }
    }
}
