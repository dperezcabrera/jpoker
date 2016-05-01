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

import java.util.EnumMap;
import java.util.Map;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 * @since 1.0.0
 */
public final class TexasHoldEmUtil {

    public static final int MIN_PLAYERS = 2;
    public static final int PLAYER_CARDS = 2;
    public static final int MAX_PLAYERS = 10;
    public static final int COMMUNITY_CARDS = 5;
    private static final Map<BetCommandType, PlayerState> PLAYER_STATE_CONVERSOR = buildPlayerStateConversor();

    public enum BetCommandType {

        ERROR,
        TIMEOUT,
        FOLD,
        CHECK,
        CALL,
        RAISE,
        ALL_IN
    }

    public enum GameState {

        PRE_FLOP,
        FLOP,
        TURN,
        RIVER,
        SHOWDOWN,
        END,
    }

    public enum PlayerState {

        READY(true),
        OUT(false),
        FOLD(false),
        CHECK(true),
        CALL(true),
        RAISE(true),
        ALL_IN(false);

        private final boolean active;

        private PlayerState(boolean isActive) {
            this.active = isActive;
        }

        public boolean isActive() {
            return active;
        }
    }

    private TexasHoldEmUtil() {
    }

    private static Map<BetCommandType, PlayerState> buildPlayerStateConversor() {
        Map<BetCommandType, PlayerState> result = new EnumMap<>(BetCommandType.class);
        result.put(BetCommandType.FOLD, PlayerState.FOLD);
        result.put(BetCommandType.ALL_IN, PlayerState.ALL_IN);
        result.put(BetCommandType.CALL, PlayerState.CALL);
        result.put(BetCommandType.CHECK, PlayerState.CHECK);
        result.put(BetCommandType.RAISE, PlayerState.RAISE);
        result.put(BetCommandType.ERROR, PlayerState.FOLD);
        result.put(BetCommandType.TIMEOUT, PlayerState.FOLD);
        return result;
    }

    public static PlayerState convert(BetCommandType betCommand) {
        return PLAYER_STATE_CONVERSOR.get(betCommand);
    }
}
