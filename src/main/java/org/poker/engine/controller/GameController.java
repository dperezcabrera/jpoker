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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.poker.api.core.Card;
import org.poker.api.game.BetCommand;
import org.poker.api.game.GameException;
import org.poker.api.game.GameInfo;
import org.poker.api.game.IGameController;
import org.poker.api.game.IStrategy;
import org.poker.api.game.PlayerInfo;
import org.poker.api.game.Settings;
import org.poker.dispatcher.GameEvent;
import org.poker.dispatcher.GameEventDispatcher;
import org.poker.dispatcher.IGameEventDispatcher;
import org.poker.dispatcher.IGameEventProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.util.timer.GameTimer;
import org.util.timer.IGameTimer;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
public class GameController implements IGameController, Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameController.class);
    private static final int DISPATCHER_THREADS = 1;
    private static final int EXTRA_THREADS = 2;
    public static final String SYSTEM_CONTROLLER = "system";
    public static final String INIT_HAND_EVENT_TYPE = "initHand";
    public static final String BET_COMMAND_EVENT_TYPE = "betCommand";

    public static final String END_GAME_PLAYER_EVENT_TYPE = "endGame";
    public static final String END_HAND_PLAYER_EVENT_TYPE = "endHand";
    public static final String CHECK_PLAYER_EVENT_TYPE = "check";
    public static final String GET_COMMAND_PLAYER_EVENT_TYPE = "getCommand";

    public static final String EXIT_CONNECTOR_EVENT_TYPE = "exit";
    public static final String ADD_PLAYER_CONNECTOR_EVENT_TYPE = "addPlayer";

    public static final String TIMEOUT_CONNECTOR_EVENT_TYPE = "timeOutCommand";
    public static final String CREATE_GAME_CONNECTOR_EVENT_TYPE = "createGame";

    private final Map<String, IGameEventDispatcher> players = new HashMap<>();
    private final List<String> playersByName = new ArrayList<>();
    private final List<ExecutorService> subExecutors = new ArrayList<>();
    private final Map<String, IGameEventProcessor<IStrategy>> playerProcessors;
    private final GameEventDispatcher<StateMachineConnector> connectorDispatcher;
    private final StateMachineConnector stateMachineConnector;
    private final IGameTimer timer;
    private Settings settings;
    private ExecutorService executors;
    private boolean finish;

    public GameController() {
        timer = new GameTimer(SYSTEM_CONTROLLER, buildExecutor(DISPATCHER_THREADS));
        stateMachineConnector = new StateMachineConnector(timer, players);
        connectorDispatcher = new GameEventDispatcher<>(stateMachineConnector, buildConnectorProcessors(), buildExecutor(1));
        stateMachineConnector.setSystem(connectorDispatcher);
        timer.setDispatcher(connectorDispatcher);
        playerProcessors = buildPlayerProcessors();
    }

    private ExecutorService buildExecutor(int threads) {
        ExecutorService result = Executors.newFixedThreadPool(threads);
        subExecutors.add(result);
        return result;
    }

    private static Map<String, IGameEventProcessor<StateMachineConnector>> buildConnectorProcessors() {
        Map<String, IGameEventProcessor<StateMachineConnector>> connectorProcessorsMap = new HashMap<>();
        connectorProcessorsMap.put(CREATE_GAME_CONNECTOR_EVENT_TYPE, (connector, event) -> connector.createGame((Settings) event.getPayload()));
        connectorProcessorsMap.put(ADD_PLAYER_CONNECTOR_EVENT_TYPE, (connector, event) -> connector.addPlayer(event.getSource()));
        connectorProcessorsMap.put(INIT_HAND_EVENT_TYPE, (connector, event) -> connector.startGame());
        connectorProcessorsMap.put(BET_COMMAND_EVENT_TYPE, (connector, event) -> connector.betCommand(event.getSource(), (BetCommand) event.getPayload()));
        connectorProcessorsMap.put(TIMEOUT_CONNECTOR_EVENT_TYPE, (connector, event) -> connector.timeOutCommand((Long) event.getPayload()));
        return connectorProcessorsMap;
    }

    private Map<String, IGameEventProcessor<IStrategy>> buildPlayerProcessors() {
        Map<String, IGameEventProcessor<IStrategy>> playerProcessorsMap = new HashMap<>();
        IGameEventProcessor<IStrategy> defaultProcessor = (strategy, event) -> strategy.updateState((GameInfo) event.getPayload());
        playerProcessorsMap.put(INIT_HAND_EVENT_TYPE, defaultProcessor);
        playerProcessorsMap.put(END_HAND_PLAYER_EVENT_TYPE, defaultProcessor);
        playerProcessorsMap.put(END_GAME_PLAYER_EVENT_TYPE, defaultProcessor);
        playerProcessorsMap.put(BET_COMMAND_EVENT_TYPE, (strategy, event) -> strategy.onPlayerCommand(event.getSource(), (BetCommand) event.getPayload()));
        playerProcessorsMap.put(CHECK_PLAYER_EVENT_TYPE, (strategy, event) -> strategy.check((List<Card>) event.getPayload()));
        playerProcessorsMap.put(GET_COMMAND_PLAYER_EVENT_TYPE, (strategy, event) -> {
            GameInfo<PlayerInfo> gi = (GameInfo<PlayerInfo>) event.getPayload();
            String playerTurn = gi.getPlayers().get(gi.getPlayerTurn()).getName();
            BetCommand command = strategy.getCommand(gi);
            connectorDispatcher.dispatch(new GameEvent(BET_COMMAND_EVENT_TYPE, playerTurn, command));
        });
        return playerProcessorsMap;
    }

    @Override
    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    @Override
    public synchronized boolean addStrategy(IStrategy strategy) {
        boolean result = false;
        String name = strategy.getName();
        if (!players.containsKey(name) && !SYSTEM_CONTROLLER.equals(name)) {
            players.put(name, new GameEventDispatcher<>(strategy, playerProcessors, buildExecutor(DISPATCHER_THREADS)));
            playersByName.add(name);
            result = true;
        }
        return result;
    }

    private void check(boolean essentialCondition, String exceptionMessage) throws GameException {
        if (!essentialCondition) {
            throw new GameException(exceptionMessage);
        }
    }

    @Override
    public synchronized void start() throws GameException {
        LOGGER.debug("start");
        check(settings != null, "No se ha establecido una configuración.");
        check(players.size() > 1, "No se han agregado un número suficiente de jugadores.");
        check(players.size() < settings.getMaxPlayers(), "El número de jugadores excede el máximo permitido por configuración.");
        check(settings.getMaxErrors() > 0, "El número de máximo de errores debe ser mayor que '0'.");
        check(settings.getMaxRounds() > 0, "El número de máximo de rondas debe ser mayor que '0'.");
        check(settings.getRounds4IncrementBlind()> 1, "El número de rondas hasta incrementar las ciegas debe ser mayor que '1'.");
        check(settings.getTime() > 0, "El tiempo máximo por jugador debe ser mayor que '0' y se indica en ms.");
        check(settings.getPlayerChip() > 0, "El número de fichas inicial por jugador debe ser mayor que '0', el valor recomendado es 5000.");
        check(settings.getSmallBind() > 0, "La apuesta de la ciega pequeña debe ser mayor que '0' idealmente es la centesima parte de las fichas iniciales por jugador.");
        executors = Executors.newFixedThreadPool(players.size() + EXTRA_THREADS);
        players.values().stream().forEach(executors::execute);
        stateMachineConnector.createGame(settings);
        timer.setTime(settings.getTime());
        playersByName.stream().forEach(stateMachineConnector::addPlayer);
        executors.execute(timer);
        finish = false;
        new Thread(this).start();
    }

    @Override
    public synchronized void run() {
        LOGGER.debug("run");
        connectorDispatcher.dispatch(new GameEvent(INIT_HAND_EVENT_TYPE, SYSTEM_CONTROLLER));
        connectorDispatcher.run();
        // Fin de la ejecución
        timer.exit();
        executors.shutdown();
        players.values().stream().forEach(IGameEventDispatcher::exit);
        subExecutors.stream().forEach(ExecutorService::shutdown);
        try {
            executors.awaitTermination(0, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            LOGGER.error("Error intentando eliminar cerrar todos los hilos", ex);
        }
        finish = true;
        notifyAll();
    }

    @Override
    public synchronized void waitFinish() {
        if (!finish) {
            try {
                wait();
            } catch (InterruptedException ex) {
                LOGGER.error("Esperando el final del juego", ex);
            }
        }
    }
}
