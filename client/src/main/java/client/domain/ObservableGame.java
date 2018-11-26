package client.domain;

import shared.dto.GameStateDTO;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * An observable wrapper for a GameState.
 *
 * An event is triggered every time a player performs an action and the game's state changes.
 */
public abstract class ObservableGame {
    private GameStateDTO gamestate = null;
    private Map<Integer,Consumer<ObservableGame>> observers = new HashMap<>();
    private Map<Integer,Consumer<Exception>> exceptionHandlers = new HashMap<>();
    private int observerID = 0;
    private int exceptionHandlerID = 0;

    /**
     * Returns the last game state that was received from the server.
     *
     * @return the current game state
     */
    public GameStateDTO getGamestate(){
        return gamestate;
    }

    /**
     * Registers an observer that will be called every time a new game state is received from the server.
     *
     * @param callback A function to call every time the game state changes
     * @return An integer that can be used to unregister this observer
     */
    public int addObserver(Consumer<ObservableGame> callback){
        int observerID = this.observerID;
        this.observerID++;

        observers.put(observerID, callback);
        return observerID;
    }

    /**
     * Unregisters a previously registered observer.
     *
     * @param observerID The ID of the observer to remove
     */
    public void removeObserver(int observerID){
        observers.remove(observerID);
    }

    public int addExceptionHandler(Consumer<Exception> handler){
        int handlerID = this.exceptionHandlerID;
        this.exceptionHandlerID++;

        exceptionHandlers.put(handlerID, handler);
        return handlerID;
    }

    public void removeExceptionHandler(int handlerID){
        exceptionHandlers.remove(handlerID);
    }

    /**
     * Must be called every time the server sends a new game state.
     * Saves the new game state and notifies all observers of the update.
     *
     * @param gamestate The latest game state received from the server
     */
    protected void updateGamestate(GameStateDTO gamestate){
        this.gamestate = gamestate;
        notifyObservers();
    }

    /**
     * notifies observers
     */
    private void notifyObservers() {
        for (Consumer<ObservableGame> callback : observers.values()){
            callback.accept(this);
        }
    }

    protected void handleException(Exception ex){
        for (Consumer<Exception> handler : exceptionHandlers.values()){
            handler.accept(ex);
        }
    }
}
