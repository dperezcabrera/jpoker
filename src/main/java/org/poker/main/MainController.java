/* 
 * Copyright (C) 2016 David Pérez Cabrera <dperezcabrera@gmail.com>
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
package org.poker.main;

import org.poker.sample.strategies.RandomStrategy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.poker.api.game.IGameController;
import org.poker.api.game.IStrategy;
import org.poker.api.game.Settings;
import org.poker.engine.controller.GameController;
import org.poker.gui.TexasHoldEmView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
public final class MainController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainController.class);
    
    private static final int PLAYERS = 10;

    private MainController() {
    }

    public static void main(String[] args) throws Exception {
        IStrategy strategyMain = new RandomStrategy("0");
        TexasHoldEmView texasHoldEmView = new TexasHoldEmView(strategyMain);
        texasHoldEmView.setVisible(true);
        strategyMain = texasHoldEmView.getStrategy();
        List<IStrategy> strategies = new ArrayList<>();
        strategies.add(strategyMain);
        for (int i = 1; i < PLAYERS; i++) {
            strategies.add(new RandomStrategy(String.valueOf(i)));
        }
        Collections.shuffle(strategies);
        while (true) {
            Settings settings = new Settings();
            settings.setMaxErrors(3);
            settings.setMaxPlayers(PLAYERS);
            settings.setMaxRounds(1000);
            settings.setTime(500);
            settings.setPlayerChip(5000L);
            settings.setRounds4IncrementBlind(20);
            settings.setSmallBlind(settings.getPlayerChip() / 100);
            IGameController controller = new GameController();
            controller.setSettings(settings);
            for (IStrategy strategy : strategies) {
                controller.addStrategy(strategy);
            }
            controller.start();
            controller.waitFinish();
            LOGGER.info("scores: {}", controller.getScores());
            Thread.sleep(1000);
        }
    }
}
