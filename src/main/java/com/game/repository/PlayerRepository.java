package com.game.repository;

import com.game.entity.Player;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {  //extends CrudRepository
    Player findById(long id);

    List<Player> findAllByOrderByIdAsc();
    List<Player> findAllByOrderByNameAsc();
    List<Player> findAllByOrderByExperienceAsc();
    List<Player> findAllByOrderByBirthdayAsc();
    List<Player> findAllByOrderByLevelAsc();

    List<Player> findAll();

    void deleteById(long id);

}
