package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(path = "rest")
public class PlayersController {

    @Autowired
    PlayersService playersService;


    //********************************************************************************************
    //***** rest/players (Method GET) : Получение списка игроков, соответствующих параметрам *****
    //********************************************************************************************
    @GetMapping(path = "players")
    public ResponseEntity<List<Player>> getPlayerList(@RequestParam(value = "name", required = false) String name,                      //Имя персонажа (до 12 знаков включительно)
                                                      @RequestParam(value = "title", required = false) String title,                    //Титул персонажа (до 30 знаков включительно)
                                                      @RequestParam(value = "race", required = false) Race race,                        //Расса персонажа
                                                      @RequestParam(value = "profession", required = false) Profession profession,      //Профессия персонажа
                                                      @RequestParam(value = "after", required = false) Long after,
                                                      @RequestParam(value = "before", required = false) Long before,
                                                      @RequestParam(value = "banned", required = false) Boolean banned,
                                                      @RequestParam(value = "minExperience", required = false) Integer minExperience,   //Опыт персонажа. Диапазон значений 0..10,000,000
                                                      @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                                                      @RequestParam(value = "minLevel", required = false) Integer minLevel,             //Уровень персонажа
                                                      @RequestParam(value = "maxLevel", required = false) Integer maxLevel,
                                                      @RequestParam(value = "order", required = false) PlayerOrder order,
                                                      @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                      @RequestParam(value = "pageSize", required = false) Integer pageSize)
    {
        //Устанавливаем значения по умолчанию / граничные значения
        if (order == null) order = PlayerOrder.ID;
        if (pageNumber == null) pageNumber = 0;
        if (pageSize == null) pageSize = 3;
        if (minExperience == null) minExperience = 0;
        if (maxExperience == null) maxExperience = 10000000;
        if (minLevel == null) minLevel = 0;
        if (maxLevel == null) maxLevel = Integer.MAX_VALUE;

        //Получаем список игроков из БД в заданном порядке
        List<Player> list = playersService.findAll(order);

        //Выбираем из полученного списка строки соответствующие опциональным полям URL Params
        List<Player> sortedList = new ArrayList<>();
        for (Player player : list) {
            if ((name == null || player.getName().contains(name)) &&
                (title == null || player.getTitle().contains(title)) &&
                (race == null || player.getRace().equals(race)) &&
                (profession == null || player.getProfession().equals(profession)) &&
                (after == null || player.getBirthday().after(new Date(after))) &&
                (before == null || player.getBirthday().before(new Date(before))) &&
                (banned == null || player.getBanned().equals(banned)) &&
                (minExperience <= player.getExperience() && maxExperience >= player.getExperience()) &&
                (minLevel <= player.getLevel() && maxLevel >= player.getLevel())
                ) {
                    sortedList.add(player);
                }
        }

        //Для постраничной выдачи выбираем номера соответствующие странице
        List<Player> result = new ArrayList<>();
        for (int i = 0; i < sortedList.size(); i++) {
            if ((pageNumber == 0 && i >= 0 && i < pageSize) || (i / pageSize == pageNumber && i % pageNumber >= 0 && i % pageNumber < pageSize)) {
                result.add(sortedList.get(i));
            }
        }
        return ResponseEntity.ok(result);
    }


    //******************************************************************************************************
    //***** rest/players/count (Method GET) : Получение количества игроков, соответствующих параметрам *****
    //******************************************************************************************************
    @GetMapping(path = "players/count")
    public ResponseEntity<Integer> getPlayerCount(@RequestParam(value = "name", required = false) String name,
                                                  @RequestParam(value = "title", required = false) String title,
                                                  @RequestParam(value = "race", required = false) Race race,
                                                  @RequestParam(value = "profession", required = false) Profession profession,
                                                  @RequestParam(value = "after", required = false) Long after,
                                                  @RequestParam(value = "before", required = false) Long before,
                                                  @RequestParam(value = "banned", required = false) Boolean banned,
                                                  @RequestParam(value = "minExperience", required = false) Integer minExperience,
                                                  @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                                                  @RequestParam(value = "minLevel", required = false) Integer minLevel,
                                                  @RequestParam(value = "maxLevel", required = false) Integer maxLevel)
    {
        //Устанавливаем значения по умолчанию / граничные значения
        if (minExperience == null) minExperience = 0;
        if (maxExperience == null) maxExperience = 10000000;
        if (minLevel == null) minLevel = 0;
        if (maxLevel == null) maxLevel = Integer.MAX_VALUE;

        //Получаем список игроков из БД
        List<Player> list = playersService.getAll();

        //Выбираем из полученного списка строки соответствующие опциональным полям URL Params
        List<Player> sortedList = new ArrayList<>();
        for (Player player : list) {
            if ((name == null || player.getName().contains(name)) &&
                (title == null || player.getTitle().contains(title)) &&
                (race == null || player.getRace().equals(race)) &&
                (profession == null || player.getProfession().equals(profession)) &&
                (after == null || player.getBirthday().after(new Date(after))) &&
                (before == null || player.getBirthday().before(new Date(before))) &&
                (banned == null || player.getBanned().equals(banned)) &&
                (minExperience <= player.getExperience() && maxExperience >= player.getExperience()) &&
                (minLevel <= player.getLevel() && maxLevel >= player.getLevel())
            ) {
                    sortedList.add(player);
            }
        }

        //Возвращаем количество строк (игроков)
        return ResponseEntity.ok(sortedList.size());
    }


    //********************************************************
    //***** rest/players (Method POST) : Создание игрока *****
    //********************************************************
    @PostMapping(path = "players")
    public ResponseEntity<Player> createPlayer(@RequestBody Player newPlayer) {

        //Опциональное поле banned, по умолчанию равно "false"
        if (newPlayer.getBanned() == null) newPlayer.setBanned(false);

        //Возвращаем ошибку 400 если не все поля заполнены, длина name выше 12 символов или пустая трока, длина title выше 30 символов,
        //опыт вне заданных пределов 0 - 10.000.000, “birthday”:[Long] < 0, дата регистрации находятся вне заданных пределов 2000 - 3000 гг.
        if (newPlayer.getName() == null || newPlayer.getName().length() < 1 || newPlayer.getName().length() > 12 ||
            newPlayer.getTitle() == null || newPlayer.getTitle().length() > 30 ||
            newPlayer.getRace() == null || newPlayer.getProfession() == null ||
            newPlayer.getExperience() == null || newPlayer.getExperience() < 0 || newPlayer.getExperience() > 10000000 ||
            newPlayer.getBirthday() == null || newPlayer.getBirthday().getTime() < 0L ||
            newPlayer.getBirthday().getTime() < 946663200000L ||   //меньше 2000 года
            newPlayer.getBirthday().getTime() > 32535194399000L)   //больше 3000 года
        {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        //Рассчитываем поля level и untilNextLevel
        Integer level = (int) ((Math.sqrt(2500 + 200 * ((double) newPlayer.getExperience())) - 50) / 100);
        Integer untilNextLevel = 50 * (level + 1) * (level + 2) - newPlayer.getExperience();

        //Заполняем поля level и untilNextLevel нового игрока
        newPlayer.setLevel(level);
        newPlayer.setUntilNextLevel(untilNextLevel);
        //игнорируем поле id из тела запроса
        newPlayer.setId(null);

        return ResponseEntity.ok(playersService.putIntoDB(newPlayer));
    }


    //*******************************************************************
    //***** rest/players/{id} (Method GET) : Получение игрока по ID *****
    //*******************************************************************
    @GetMapping(path = "players/{id}")
    public ResponseEntity<Player>  getPlayer(@PathVariable("id") Long id) {
        //Если значение id не валидное, отвечаем ошибкой с кодом 400
        if (id == null || id < 1)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        //Находим игрока
        Player requestedPlayer = playersService.findById(id);
        //Если игрок не найден в БД, отвечаем ошибкой с кодом 404
        if (requestedPlayer == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        //Возвращаем игрока
        return ResponseEntity.ok(requestedPlayer);
    }


    //****************************************************************
    //***** rest/players/{id} (Method POST) : Обновление игрока  *****
    //****************************************************************
    @PostMapping(path = "players/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable("id") Long id, @RequestBody Player newFields) {
        //Если значение id, birthday и experience не валидные, отвечаем ошибкой с кодом 400
        if (id == null || id < 1 ||
            (newFields.getExperience() != null && (newFields.getExperience() < 0 || newFields.getExperience() > 10000000)) ||
            (newFields.getBirthday() != null && newFields.getBirthday().getTime() < 0L))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        //Находим игрока для апгрейда
        Player updatingPlayer = playersService.findById(id);
        //Если игрок не найден в БД, отвечаем ошибкой с кодом 404
        if (updatingPlayer == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        //Обновляем игрока
        if (newFields.getName() != null) {
            updatingPlayer.setName(newFields.getName());
        }
        if (newFields.getTitle() != null) {
            updatingPlayer.setTitle(newFields.getTitle());
        }
        if (newFields.getRace() != null) {
            updatingPlayer.setRace(newFields.getRace());
        }
        if (newFields.getProfession() != null) {
            updatingPlayer.setProfession(newFields.getProfession());
        }
        if (newFields.getBirthday() != null) {
            updatingPlayer.setBirthday(newFields.getBirthday());
        }
        if (newFields.getBanned() != null) {
            updatingPlayer.setBanned(newFields.getBanned());
        }
        if (newFields.getExperience() != null) {
            updatingPlayer.setExperience(newFields.getExperience());
            //Рассчитываем поля level и untilNextLevel
            Integer level = (int) ((Math.sqrt(2500 + 200 * ((double) newFields.getExperience())) - 50) / 100);
            Integer untilNextLevel = 50 * (level + 1) * (level + 2) - newFields.getExperience();
            //Заполняем поля level и untilNextLevel
            updatingPlayer.setLevel(level);
            updatingPlayer.setUntilNextLevel(untilNextLevel);
        }

        return ResponseEntity.ok(playersService.putIntoDB(updatingPlayer));
    }


    //***************************************************************
    //***** rest/players/{id} (Method DELETE) : Удаление игрока *****
    //***************************************************************
    @DeleteMapping(path = "players/{id}")
    public ResponseEntity deletePlayer(@PathVariable("id") Long id) {
        //Если и значение id не валидное, отвечаем ошибкой с кодом 400
        if (id == null || id < 1)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        //Если игрок не найден в БД, отвечаем ошибкой с кодом 404
        if (playersService.findById(id) == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        //Удаляем игрока
        playersService.deleteById(id);
        return ResponseEntity.ok(null);
    }

}
