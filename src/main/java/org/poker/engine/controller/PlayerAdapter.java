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
package org.poker.engine.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.poker.api.game.PlayerInfo;
import org.poker.api.game.Settings;
import org.poker.api.game.GameInfo;
import org.poker.engine.model.ModelContext;
import org.poker.engine.model.PlayerEntity;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 * @since 1.0.0
 */
public final class PlayerAdapter {

    private PlayerAdapter() {
    }

    public static List<PlayerInfo> toPlayerInfo(Collection<PlayerEntity> players, String name) {
        List<PlayerInfo> result = Collections.emptyList();
        if (players != null) {
            result = new ArrayList<>(players.size());
            for (PlayerEntity pe : players) {
                result.add(copy(pe, pe.showCards() || pe.getName().equals(name)));
            }
        }
        return result;
    }

    public static GameInfo<PlayerInfo> toTableState(ModelContext model, String name) {
        GameInfo<PlayerInfo> result = new GameInfo<>();
        result.setCommunityCards(model.getCommunityCards());
        result.setDealer(model.getDealer());
        result.setGameState(model.getGameState());
        result.setPlayerTurn(model.getPlayerTurn());
        result.setRound(model.getRound());
        if (model.getSettings() != null) {
            result.setSettings(new Settings(model.getSettings()));
        }
        result.setPlayers(toPlayerInfo(model.getPlayers(), name));
        return result;
    }

    public static PlayerInfo copy(PlayerEntity p, boolean copyCards) {
        PlayerInfo result = new PlayerInfo();
        result.setName(p.getName());
        result.setChips(p.getChips());
        result.setBet(p.getBet());
        if (copyCards) {
            result.setCards(p.getCard(0), p.getCard(1));
        }
        result.setState(p.getState());
        result.setErrors(p.getErrors());
        return result;
    }
}
