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
package org.util.timer;

import java.util.concurrent.ExecutorService;
import net.jcip.annotations.ThreadSafe;
import org.poker.dispatcher.GameEvent;
import org.poker.dispatcher.IGameEventDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
@ThreadSafe
public class GameTimer implements IGameTimer {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameTimer.class);
    public static final String TIMEOUT_EVENT_TYPE = "timeOutCommand";

    private final String source;
    private long time;
    private IGameEventDispatcher dispatcher;
    private boolean reset = false;
    private volatile boolean exit = false;
    private final ExecutorService executors;
    private Long timeoutId;

    public GameTimer(String source, ExecutorService executors) {
        this.source = source;
        this.executors = executors;
    }

    @Override
    public synchronized IGameEventDispatcher getDispatcher() {
        return dispatcher;
    }

    @Override
    public synchronized void setDispatcher(IGameEventDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public synchronized void resetTimer(Long timeoutId) {
        this.timeoutId = timeoutId;
        this.reset = true;
        notify();
    }

    @Override
    public synchronized void exit() {
        this.exit = true;
        this.reset = false;
        this.timeoutId = null;
        notify();
    }

    @Override
    public void run() {
        LOGGER.debug("run");
        while (!exit) {
            try {
                doTask();
            } catch (Exception ex) {
                LOGGER.error("Timer interrupted", ex);
            }
        }
        LOGGER.debug("finish");
    }

    private synchronized void doTask() throws InterruptedException {
        if (timeoutId == null) {
            wait();
        }
        if (timeoutId != null) {
            reset = false;
            wait(time);
            if (!reset && timeoutId != null) {
                executors.execute(() -> dispatcher.dispatch(new GameEvent(TIMEOUT_EVENT_TYPE, source, timeoutId)));
                timeoutId = null;
            }
        }
    }
}
