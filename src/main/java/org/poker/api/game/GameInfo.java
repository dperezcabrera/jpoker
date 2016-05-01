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

import java.util.ArrayList;
import java.util.List;
import net.jcip.annotations.NotThreadSafe;
import org.poker.api.core.Card;
import static org.poker.api.game.TexasHoldEmUtil.COMMUNITY_CARDS;
import org.poker.api.game.TexasHoldEmUtil.GameState;

/**
 *
 * @author David Pérez Cabrera <dperezcabrera@gmail.com>
 * @since 1.0.0
 * 
 * @param <P>
 */
@NotThreadSafe
public class GameInfo<P extends PlayerInfo> {

    private int round;
    private int dealer;
    private int playerTurn;
    private GameState gameState;
    private final List<Card> communityCards = new ArrayList<>(COMMUNITY_CARDS);
    private List<P> players;
    private Settings settings;

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getDealer() {
        return dealer;
    }

    public void setDealer(int dealer) {
        this.dealer = dealer;
    }

    public int getPlayerTurn() {
        return playerTurn;
    }

    public void setPlayerTurn(int playerTurn) {
        this.playerTurn = playerTurn;
    }

    public TexasHoldEmUtil.GameState getGameState() {
        return gameState;
    }

    public void setGameState(TexasHoldEmUtil.GameState gameState) {
        this.gameState = gameState;
    }

    public List<Card> getCommunityCards() {
        return new ArrayList<>(communityCards);
    }

    public void setCommunityCards(List<Card> communityCards) {
        this.communityCards.clear();
        this.communityCards.addAll(communityCards);
    }

    public List<P> getPlayers() {
        return new ArrayList<>(players);
    }

    public P getPlayer(int index) {
        return players.get(index);
    }

    public void setPlayers(List<P> players) {
        this.players = new ArrayList<>(players);
    }

    public boolean addPlayer(P player) {
        return this.players.add(player);
    }

    public boolean removePlayer(P player) {
        return this.players.remove(player);
    }

    public int getNumPlayers() {
        return players.size();
    }

    public boolean addCommunityCard(Card card) {
        boolean result = false;
        if (communityCards.size() < COMMUNITY_CARDS) {
            result = communityCards.add(card);
        }
        return result;
    }

    public void clearCommunityCard() {
        this.communityCards.clear();
    }

    @Override
    public String toString() {
        return "{class:'GameInfo', round:" + round + ", dealer:" + dealer + ((playerTurn < 0) ? "" : (", playerTurn:" + playerTurn)) + ", gameState:'" + gameState + "', communityCards:" + communityCards + ", settings:" + settings + ", players:" + players + '}';
    }
}
