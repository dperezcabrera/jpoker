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

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
public class FoldStrategy implements IStrategy {

    private final String name;

    public FoldStrategy(String name) {
        this.name = "Fold-" + name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public BetCommand getCommand(GameInfo<PlayerInfo> state) {
        return new BetCommand(TexasHoldEmUtil.BetCommandType.FOLD);
    }

    @Override
    public String toString() {
        return String.join("{Fold-", name, "}");
    }
}
