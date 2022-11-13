package com.tasca_s5_02_n1.joc_de_daus.utils;

import com.tasca_s5_02_n1.joc_de_daus.model.domain.PlayerModel;
import com.tasca_s5_02_n1.joc_de_daus.model.domain.ScoreModel;
import com.tasca_s5_02_n1.joc_de_daus.model.dto.PlayerDTO;
import com.tasca_s5_02_n1.joc_de_daus.model.repository.PlayerRepository;
import com.tasca_s5_02_n1.joc_de_daus.model.repository.ScoreRepository;

import java.util.UUID;


import java.util.*;
import java.util.stream.Collectors;

public class Utilities {

    private static Utilities utilities;

    PlayerRepository playerRepository;
    ScoreRepository scoreRepository;


    /**
     * BONUS TRACK DEPENDING THE DICE FACE REPETITION
     *
     * @apiNote bonusTrack.put(diceFaceValue, extraPoints)
     */
    public static Map<Integer, Integer> bonusTrack;

    static {
        bonusTrack = new HashMap<>();
        bonusTrack.put(2, 2);
        bonusTrack.put(3, 1);
        bonusTrack.put(12, 2);
        bonusTrack.put(11, 1);
    }

    public Utilities(PlayerRepository playerRepository, ScoreRepository scoreRepository) {
        this.playerRepository = playerRepository;
        this.scoreRepository = scoreRepository;
    }


    /**
     * SINGLETON
     *
     * @param playerRepository
     * @param scoreRepository
     * @return Obj
     */
    public static Utilities getInstance(PlayerRepository playerRepository,
                                        ScoreRepository scoreRepository) {

        if (utilities == null) {
            utilities = new Utilities(playerRepository, scoreRepository);
        }
        return utilities;
    }


    /**
     * @param idPlayer
     * @return int which is the position into the ranking.
     */
    public int getRankingPlayer(String idPlayer) {

        Map<String, Integer> positions = getPlayersRanking();

        // Return the player's position into the Map<> +1 because the index.
        return (new ArrayList<>(positions.keySet()).indexOf(idPlayer) + 1);
    }


    /**
     * Method to get ranking from all records
     *
     * @return Map</>String, Integer> from players and their scores
     */
    public Map<String, Integer> getPlayersRanking() {

        Iterable<PlayerModel> players = playerRepository.findAll();
        Map<String, Integer> listPlayerScores = new HashMap<>();

        for (PlayerModel c : players) {
            PlayerDTO playerDTO = new PlayerDTO();

            playerDTO.setScoreList(scoreQueryFromIdPlayer(c.getId()));

            if (playerDTO.getScoreList().size() > 0) {
                playerDTO.setGames(playerDTO.getGames());
                listPlayerScores.put(c.getId(), playerDTO.getGames());
            }

        }

        // return from the high score to low score
        return listPlayerScores
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }


    /**
     * Method to get the final score after thrown dices
     *
     * @return int
     */
    public static int getPointsFromSideDiceThrow(int bet, int scored, int leftDice, int rightDice) {

        int result;

        if (bet == scored) {
            if (leftDice == rightDice) {
                if (bonusTrack.get(leftDice + rightDice) != null)

                    result = bonusTrack.get(leftDice + rightDice) + 2;
                else
                    result = 2;
            } else
                result = 1;
        } else
            result = 0;

        return result;

    }


    /**
     * QUERY
     *
     * @param idPlayer
     * @return List</> from table 'score' with idPlayer scores
     */
    public List<ScoreModel> scoreQueryFromIdPlayer(String idPlayer) {

        return scoreRepository.findByIdPlayer(idPlayer);
    }


    /**
     *
     * Function to create randomString to add for Anonymous players
    * */
    public static String randomString() {
        return UUID.randomUUID().toString().substring(0,4);
    }

} //END CLASS
