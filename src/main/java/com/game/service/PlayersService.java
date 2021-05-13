package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;

import java.util.List;

public interface PlayersService {
    Player findById(long id);
    List<Player> getAll();
    List<Player> findAll(PlayerOrder order);
    void deleteById(long id);
    Player putIntoDB(Player player);
}
