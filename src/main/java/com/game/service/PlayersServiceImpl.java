package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional()
public class PlayersServiceImpl implements PlayersService {

    @Autowired
    private PlayerRepository playerRepository;

    @Override
    public Player findById(long id) {
        return playerRepository.findById(id);
    }

    @Override
    public List<Player> getAll() {
        List<Player> list = playerRepository.findAll();
        return list;
    }

    @Override
    public List<Player> findAll(PlayerOrder order) {
        switch (order) {
            case NAME:
                return playerRepository.findAllByOrderByNameAsc();
            case EXPERIENCE:
                return playerRepository.findAllByOrderByExperienceAsc();
            case BIRTHDAY:
                return playerRepository.findAllByOrderByBirthdayAsc();
            case LEVEL:
                return playerRepository.findAllByOrderByLevelAsc();
        }
        // ID:
        return playerRepository.findAllByOrderByIdAsc();
    }

    @Override
    public void deleteById(long id) {
        playerRepository.deleteById(id);
    }

    @Override
    public Player putIntoDB(Player player) {
        return playerRepository.save(player);
    }

}
