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
package org.poker.sample.strategies;

import org.poker.api.game.BetCommand;
import org.poker.api.game.GameInfo;
import org.poker.api.game.IStrategy;
import org.poker.api.game.PlayerInfo;
import org.poker.api.game.TexasHoldEmUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
public class SlowlyStrategy implements IStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(SlowlyStrategy.class);
    private final String name;

    public SlowlyStrategy(String name) {
        this.name = "Slowly-" + name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public BetCommand getCommand(GameInfo<PlayerInfo> state) {
        try {
            Thread.sleep(state.getSettings().getTime() + 500L);
        } catch (Exception ex) {
            LOGGER.error("getCommand-Interrupted", ex);
        }
        return new BetCommand(TexasHoldEmUtil.BetCommandType.FOLD);
    }
    
    @Override
    public String toString() {
        return String.join("{SlowlyStrategy-", name , "}");
    }
}
