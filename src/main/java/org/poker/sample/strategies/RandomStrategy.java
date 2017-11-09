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

import java.util.List;
import java.util.Random;
import org.poker.api.core.Card;
import org.poker.api.game.BetCommand;
import org.poker.api.game.GameInfo;
import org.poker.api.game.IStrategy;
import org.poker.api.game.PlayerInfo;
import org.poker.api.game.TexasHoldEmUtil;
import org.poker.api.game.TexasHoldEmUtil.BetCommandType;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 */
public class RandomStrategy implements IStrategy {

    private static final Random RAND = new Random();
    private final String name;
    private double aggressivity = 0.5 + RAND.nextDouble() / 2;
    private BetCommand lastBet = null;

    public RandomStrategy(String name) {
        this.name = "Random-" + name;
    }

    @Override
    public String getName() {
        return name;
    }

    private long getMaxBet(GameInfo<PlayerInfo> state) {
        if (aggressivity > 1.d) {
            return Long.MAX_VALUE;
        }
        long players = state.getPlayers().stream().filter(p -> p.isActive() || p.getState() == TexasHoldEmUtil.PlayerState.ALL_IN).count();
        double probability = 1.0D / players;
        long pot = state.getPlayers().stream().mapToLong(p -> p.getBet()).sum();
        return Math.round((probability * pot / (1 - probability)) * aggressivity);
    }

    @Override
    public BetCommand getCommand(GameInfo<PlayerInfo> state) {
        PlayerInfo ownInfo = state.getPlayer(state.getPlayerTurn());
        calcAggressivity(state, ownInfo);
        long otherPlayerMaxBet = state.getPlayers().stream().max((p0, p1) -> Long.compare(p0.getBet(), p1.getBet())).get().getBet();

        long minBet = Math.max(otherPlayerMaxBet - ownInfo.getBet(), state.getSettings().getBigBlind());
        long maxBet = getMaxBet(state);
        long chips = ownInfo.getChips();
        BetCommand result;
        maxBet = Math.min(maxBet, state.getPlayers().stream().mapToLong(p -> p.getBet()).sum());
        if (minBet > maxBet) {
            result = new BetCommand(BetCommandType.FOLD);
        } else if (maxBet >= chips) {
            result = new BetCommand(BetCommandType.ALL_IN);
        } else if (maxBet > minBet && (lastBet == null || lastBet.getType() != BetCommandType.RAISE)) {
            result = new BetCommand(BetCommandType.RAISE, maxBet);
        } else if (minBet == 0 || otherPlayerMaxBet == state.getSettings().getBigBlind()) {
            result = new BetCommand(BetCommandType.CHECK);
        } else {
            result = new BetCommand(BetCommandType.CALL);
        }
        lastBet = result;
        return result;
    }

    @Override
    public String toString() {
        return String.join("{RandomStrategy-", name, "}");
    }

    @Override
    public void check(List<Card> communityCards) {
        lastBet = null;
    }

    private void calcAggressivity(GameInfo<PlayerInfo> state, PlayerInfo player) {
        long allChips = state.getPlayers().stream().filter(p -> p.isActive() || p.getState() == TexasHoldEmUtil.PlayerState.ALL_IN).mapToLong(p -> p.getChips()).sum();
        long players = state.getPlayers().stream().filter(p -> p.isActive() || p.getState() == TexasHoldEmUtil.PlayerState.ALL_IN && p.getChips() > 0).count();
        long myChips = player.getChips();

        double proportion = (allChips - myChips) / players;
        aggressivity = (myChips / (proportion + myChips)) / 2 + 0.70d;
        if (myChips > (allChips - myChips)) {
            aggressivity = 1.1;
        }
    }
}
