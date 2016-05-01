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
package org.poker.dispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import net.jcip.annotations.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 * @param <T>
 */
@ThreadSafe
public class GameEventDispatcher<T> implements IGameEventDispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameEventDispatcher.class);
    public static final String EXIT_EVENT_TYPE = "exit";
    private final Map<String, IGameEventProcessor<T>> processors;
    private final T target;
    private List<GameEvent> events = new ArrayList<>();
    private volatile boolean exit = false;
    private ExecutorService executors;

    public GameEventDispatcher(T target, Map<String, IGameEventProcessor<T>> processors, ExecutorService executors) {
        this.target = target;
        this.processors = processors;
        this.executors = executors;
    }

    @Override
    public synchronized void dispatch(GameEvent event) {
        events.add(event);
        this.notify();
    }

    private void process(GameEvent event) {
        IGameEventProcessor<T> processor = processors.get(event.getType());
        if (processor != null) {
            executors.execute(() -> processor.process(target, event));
        }
    }

    @Override
    public synchronized void exit() {
        exit = true;
        this.notify();
    }
    
    private void doTask() throws InterruptedException {
        List<GameEvent> lastEvents;
        synchronized (this) {
            if (events.isEmpty()) {
                this.wait();
            }
            lastEvents = events;
            events = new ArrayList<>();
        }
        for (int i = 0; i < lastEvents.size() && !exit; i++) {
            GameEvent event = lastEvents.get(i);
            if (EXIT_EVENT_TYPE.equals(event.getType())) {
                exit = true;
            } else {
                process(event);
            }
        }
    }

    @Override
    public void run() {
        while (!exit) {
            try {
                doTask();
            } catch (Exception ex) {
                LOGGER.error("GameEventDispatcher<" + target.getClass() + ">.run(): " + target, ex);
            }
        }
        executors.shutdown();
    }
}
