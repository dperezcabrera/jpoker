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
package org.poker.api.game;

import java.util.List;
import java.util.Map;
import org.poker.api.core.Card;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 * @since 1.0.0
 */
@FunctionalInterface
public interface IStrategy {

    public String getName();

    public default BetCommand getCommand(GameInfo<PlayerInfo> state) {
        return null;
    }

    public default void initHand(GameInfo<PlayerInfo> state) {
    }
    
    public default void endHand(GameInfo<PlayerInfo> state) {
    }
    
    public default void endGame(Map<String, Double> scores) {
    }

    public default void check(List<Card> communityCards) {
    }

    public default void onPlayerCommand(String player, BetCommand betCommand) {
    }
}
