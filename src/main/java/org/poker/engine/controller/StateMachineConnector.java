/*
 * Copyright (C) 2015 David Pérez Cabrera <dperezcabrera@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.poker.engine.controller;

import java.util.Map;
import org.poker.api.core.Deck;
import org.poker.api.game.BetCommand;
import org.poker.api.game.Settings;
import org.poker.api.game.TexasHoldEmUtil;
import org.poker.dispatcher.GameEvent;
import org.poker.dispatcher.IGameEventDispatcher;
import static org.poker.engine.controller.GameController.SYSTEM_CONTROLLER;
import org.poker.engine.model.ModelContext;
import org.poker.engine.model.ModelUtil;
import org.poker.engine.states.BetRoundState;
import org.poker.engine.states.CheckState;
import org.poker.engine.states.EndGameState;
import org.poker.engine.states.EndHandState;
import org.poker.engine.states.InitHandState;
import org.poker.engine.states.ShowDownState;
import org.poker.engine.states.WinnerState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.util.statemachine.IState;
import org.util.statemachine.StateDecoratorBuilder;
import org.util.statemachine.StateMachine;
import org.util.statemachine.StateMachineInstance;
import org.util.timer.IGameTimer;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
public class StateMachineConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(StateMachineConnector.class);

    private static final String END_HAND_DELAY = "StateMachineConnector:endHandDelay";
    private static final String END_GAME_DELAY = "StateMachineConnector:endGameDelay";

    public static final String NEXT_PLAYER_TURN = "nextPlayerTurn";
    private final StateMachine<ModelContext> texasStateMachine = buildStateMachine();
    private final Map<String, IGameEventDispatcher> playersDispatcher;
    private final IGameTimer timer;
    private ModelContext model;
    private IGameEventDispatcher system;
    private StateMachineInstance<ModelContext> instance;
    private long timeoutId = 0;
    private Map<String, Double> scores;

    public StateMachineConnector(IGameTimer timer, Map<String, IGameEventDispatcher> playersDispatcher) {
        this.playersDispatcher = playersDispatcher;
        this.timer = timer;
    }

    public void setSystem(IGameEventDispatcher system) {
        this.system = system;
    }

    public void createGame(Settings settings) {
        if (model == null) {
            LOGGER.debug("createGame: {}", settings);
            model = new ModelContext(settings);
            model.setDealer(-1);
        }
    }

    public void addPlayer(String playerName) {
        if (model != null) {
            LOGGER.debug("addPlayer: \"{}\"", playerName);
            model.addPlayer(playerName);
        }
    }

    public void startGame() {
        LOGGER.debug("startGame");
        if (instance == null && model != null) {
            model.setDeck(new Deck());
            instance = texasStateMachine.startInstance(model);
            model.setDealer(0);
            execute();
        }
    }

    public void betCommand(String playerName, BetCommand command) {
        LOGGER.debug("betCommand: {} -> {}", playerName, command);
        if (instance != null && playerName.equals(model.getPlayerTurnName())) {
            BetCommand betCommand = command;
            if (betCommand == null) {
                betCommand = new BetCommand(TexasHoldEmUtil.BetCommandType.ERROR);
            }
            model.getPlayerByName(playerName).setBetCommand(betCommand);
            execute();
        }
    }

    public void timeOutCommand(Long timeoutId) {
        LOGGER.debug("timeOutCommand: id: {}", timeoutId);
        if (instance != null && timeoutId == this.timeoutId) {
            LOGGER.debug("timeOutCommand: player: {}", model.getPlayerTurnName());
            model.getPlayerByName(model.getPlayerTurnName()).setBetCommand(new BetCommand(TexasHoldEmUtil.BetCommandType.TIMEOUT));
            execute();
        }
    }

    private void execute() {
        if (instance.execute().isFinish()) {
            instance = null;
        }
    }

    private void notifyInitHand() {
        notifyEvent(GameController.INIT_HAND_EVENT_TYPE);
    }

    private void notifyBetCommand() {
        String playerTurn = model.getLastPlayerBet().getName();
        BetCommand lbc = model.getLastBetCommand();
        LOGGER.debug("notifyBetCommand -> {}: {}", playerTurn, lbc);
        for (String playerName : playersDispatcher.keySet()) {
            playersDispatcher.get(playerName).dispatch(
                    new GameEvent(GameController.BET_COMMAND_EVENT_TYPE, model.getLastPlayerBet().getName(), new BetCommand(lbc.getType(), lbc.getChips())));
        }
    }

    private void notifyCheck() {
        LOGGER.debug("notifyCheck: {}", GameController.CHECK_PLAYER_EVENT_TYPE, model.getCommunityCards());
        for (String playerName : playersDispatcher.keySet()) {
            playersDispatcher.get(playerName).dispatch(
                    new GameEvent(GameController.CHECK_PLAYER_EVENT_TYPE, SYSTEM_CONTROLLER, model.getCommunityCards()));
        }
    }

    private void notifyPlayerTurn() {
        String playerTurn = model.getPlayerTurnName();
        if (playerTurn != null) {
            LOGGER.debug("notifyPlayerTurn -> {}", playerTurn);
            playersDispatcher.get(playerTurn).dispatch(
                    new GameEvent(GameController.GET_COMMAND_PLAYER_EVENT_TYPE, SYSTEM_CONTROLLER, PlayerAdapter.toTableState(model, playerTurn)));
        }
        timer.resetTimer(++timeoutId);
    }

    private void notifyEndHand() {
        notifyEvent(GameController.END_HAND_PLAYER_EVENT_TYPE);
    }

    private void notifyEvent(String type) {
        LOGGER.debug("notifyEvent: {} -> {}", type, model);
        for (String playerName : playersDispatcher.keySet()) {
            playersDispatcher.get(playerName).dispatch(
                    new GameEvent(type, SYSTEM_CONTROLLER, PlayerAdapter.toTableState(model, playerName)));
        }
    }

    private void notifyEndGame() {
        LOGGER.debug("notifyEvent: {} -> {}", GameController.END_GAME_PLAYER_EVENT_TYPE, model);
        scores =  model.getScores();
        for (String playerName : playersDispatcher.keySet()) {
            playersDispatcher.get(playerName).dispatch(
                    new GameEvent(GameController.END_GAME_PLAYER_EVENT_TYPE, SYSTEM_CONTROLLER, scores));
        }
        system.dispatch(new GameEvent(GameController.EXIT_CONNECTOR_EVENT_TYPE, SYSTEM_CONTROLLER));
        notifyEvent(GameController.EXIT_CONNECTOR_EVENT_TYPE);
    }

    public Map<String, Double> getScores() {
        return scores;
    }
    
    private StateMachine<ModelContext> buildStateMachine() {
        StateMachine<ModelContext> sm = new StateMachine<>();
        final IState<ModelContext> initHandState = StateDecoratorBuilder.after(new InitHandState(), () -> notifyInitHand());
        final IState<ModelContext> betRoundState = StateDecoratorBuilder
                .create(new BetRoundState())
                .before(() -> notifyPlayerTurn())
                .after(() -> notifyBetCommand())
                .build();
        final IState<ModelContext> checkState = StateDecoratorBuilder.after(new CheckState(), () -> notifyCheck());
        final IState<ModelContext> showDownState = new ShowDownState();
        final IState<ModelContext> winnerState = new WinnerState();
        final IState<ModelContext> endHandState = StateDecoratorBuilder.before(new EndHandState(), () -> notifyEndHand());
        final IState<ModelContext> endGameState = StateDecoratorBuilder.after(new EndGameState(), () -> notifyEndGame());

        sm.setInitState(initHandState);

        // initHandState transitions
        sm.setDefaultTransition(initHandState, betRoundState);

        // betRoundState transitions
        sm.addTransition(betRoundState, betRoundState, c -> c.getPlayerTurn() != ModelUtil.NO_PLAYER_TURN);
        sm.addTransition(betRoundState, winnerState, c -> c.getPlayersAllIn() + c.getActivePlayers() == 1);
        sm.setDefaultTransition(betRoundState, checkState);

        // checkState transitions
        sm.addTransition(checkState, showDownState, c -> c.getGameState() == TexasHoldEmUtil.GameState.SHOWDOWN);
        sm.addTransition(checkState, betRoundState, c -> c.getPlayerTurn() != ModelUtil.NO_PLAYER_TURN);
        sm.setDefaultTransition(checkState, checkState);

        // betWinnerState transitions
        sm.setDefaultTransition(winnerState, endHandState);

        // showDownState transitions
        sm.setDefaultTransition(showDownState, endHandState);

        // endHandState transitions
        sm.addTransition(endHandState, initHandState, c -> c.getNumPlayers() > 1 && c.getRound() < c.getSettings().getMaxRounds());
        sm.setDefaultTransition(endHandState, endGameState);
        return sm;
    }
}
