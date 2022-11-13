package com.tasca_s5_02_n1.joc_de_daus.model.service;

import com.tasca_s5_02_n1.joc_de_daus.model.domain.PlayerModel;
import com.tasca_s5_02_n1.joc_de_daus.model.domain.ScoreModel;
import com.tasca_s5_02_n1.joc_de_daus.model.dto.ExtraDTO;
import com.tasca_s5_02_n1.joc_de_daus.model.dto.PlayerDTO;
import com.tasca_s5_02_n1.joc_de_daus.model.exception.AveragePlayersList;
import com.tasca_s5_02_n1.joc_de_daus.model.exception.ErrorDetails;
import com.tasca_s5_02_n1.joc_de_daus.model.exception.Message;
import com.tasca_s5_02_n1.joc_de_daus.model.exception.RequestOk;
import com.tasca_s5_02_n1.joc_de_daus.model.messages.RankingMessage;
import com.tasca_s5_02_n1.joc_de_daus.model.repository.PlayerRepository;
import com.tasca_s5_02_n1.joc_de_daus.model.repository.ScoreRepository;
import com.tasca_s5_02_n1.joc_de_daus.utils.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class PlayerService implements IPlayerService {

    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    ScoreRepository scoreRepository;
    @Autowired
    RestTemplate restTemplate;

    public PlayerService(PlayerRepository playerRepository, ScoreRepository scoreRepository) {
        this.playerRepository = playerRepository;
        this.scoreRepository = scoreRepository;
    }


    public PlayerService() {
    }
    /* *************************
     * 🤩 -> works on Postman
     * 🤖 -> works on @Test
     * ************************/


    /**
     * POST /signup   🤩🤖
     */
    @Override
    public String addingNewPlayer(PlayerDTO playerDTO) {

        //control de null
        if (playerDTO.getNamePlayer().equalsIgnoreCase("anonymous")) {
            playerDTO.setNamePlayer("Anonymous-" + Utilities.randomString());
        }

        PlayerModel playerModelNew = new PlayerModel();

        playerModelNew.setNamePlayer(playerDTO.getNamePlayer());
        playerModelNew.setLogged(true);
        playerRepository.save(playerModelNew);

        return playerModelNew.getId();
    }


    /**
     * GET -> /players/ retorna el llistat de jugadors amb el seu average. 🤩🤖
     **/
    public ResponseEntity<Message> getListPlayerWithAverage() {
        Iterator<PlayerModel> playersList = playerRepository.findAll().iterator();
        Map<String, Double> mapResults = new HashMap<>();

        while (playersList.hasNext()) {
            PlayerDTO playerDTO = getPlayerData(playersList.next().getId(), "");
            mapResults.put(playerDTO.getNamePlayer(), playerDTO.getAverage());
        }

        return new ResponseEntity<>(
                new AveragePlayersList(mapResults, "Request done correctly!"), HttpStatus.OK);
    }


    /**
     * DELETE 2nd function/players/{id}/games 🤩🤖
     */
    @Override
    public void deleteScoreFromIdPlayer(String idScore) {
        // deleting records with idScore linked idPlayer
        scoreRepository.deleteById(idScore);
    }


    /**
     * POST /players/{id}/games/ : un jugador/a específic realitza una tirada del daus. 🤩🤖
     **/
    @Override
    public ResponseEntity<RequestOk> createDiceThrown(ScoreModel scoreModel) {

        try {
            scoreRepository.save(scoreModel);

        } catch (Exception exception) {

            System.out.println(exception.getMessage());
            throw exception;
        }
        return new ResponseEntity<>
                (new RequestOk("Throw has been registered correctly."), HttpStatus.OK);
    }


    /**
     * GET & PUT /players: crea jugador o modifica el nom del jugador/a. 🤩🤖👻
     **/
    public ResponseEntity<? extends Message> isThisNameRegistered(PlayerModel newPlayer,
                                                                  boolean typeAction) {

        PlayerModel playerRename =
                playerRepository.findByNamePlayerIgnoreCase(newPlayer.getNamePlayer().toLowerCase());

        if (playerRename == null) {

            playerRepository.save(newPlayer);

            return new ResponseEntity<>
                    (new RequestOk(newPlayer.getNamePlayer()
                            + " has been " + (typeAction ? "registered " : "changed ") + " correctly with id: " + newPlayer.getId())
                            , HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>
                    (new ErrorDetails(HttpStatus.NOT_ACCEPTABLE.value(),
                            "Name " + newPlayer.getNamePlayer() + " it's taken."), HttpStatus.NOT_ACCEPTABLE);
        }
    }


    /**
     * DELETE /players 🤩🤖
     **/
    public ResponseEntity<RequestOk> deletePlayer(String token, String id, PlayerModel playerFounded)
    {
        /* REST TO REST */
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Bearer " + token);
        MultiValueMap<String, String> parametersMap = new LinkedMultiValueMap<>();
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(parametersMap, headers);
        parametersMap.add("idPlayer", id);

        restTemplate.exchange(
                "http://localhost:9005/jocdedaus/players/" + id + "/games/",
                HttpMethod.DELETE, entity, PlayerModel.class);

        String namePlayer = playerFounded.getNamePlayer();
        playerRepository.deleteById(id);

        return new ResponseEntity<>
                (new RequestOk("The player " + namePlayer + " has been deleted correctly."), HttpStatus.OK);
    }


    /**
     * DELETE /players/{id}/games: elimina la llista les tirades d'un jugador/a. 🤩🤖
     */
    public ResponseEntity<RequestOk> deleteScoreListById(PlayerModel playerFound)
    {
        String namePlayer = playerFound.getNamePlayer();
        List<ScoreModel> listToRemove = scoreRepository.findByIdPlayer(playerFound.getId());

        for (ScoreModel sm : listToRemove) {
            deleteScoreFromIdPlayer(sm.getId());
        }
        return new ResponseEntity<>
                (new RequestOk("Score list from " + namePlayer +
                        " has been deleted correctly."), HttpStatus.OK);
    }


    /**
     * GET /players/ranking/winner: retorna mitjana de percentatge d’èxit. 🤩🤖
     */
    public ResponseEntity<RankingMessage> getRanking()
    {

        List<PlayerModel> players = (List<PlayerModel>) playerRepository.findAll();
        ExtraDTO extraDTO = new ExtraDTO("Average wins/players");
        long games = 0L;

        for (PlayerModel c : players) {

            games += Utilities.getInstance(playerRepository, scoreRepository)
                    .scoreQueryFromIdPlayer(c.getId())
                    .stream()
                    .map(ScoreModel::getWins).filter(x -> x == 1).count();
        }

        double average = (double) games / players.size();
        extraDTO.setWins(average);

        return new ResponseEntity<>
                (new RankingMessage("Ok!", extraDTO.getWins()), HttpStatus.OK);
    }


    /**
     * GET /players/ranking/winner: retorna el jugador amb millor percentatge d’èxit. 🤩🤖
     */
    public HttpEntity<? extends Message> getWinner()
    {
        ExtraDTO extraDTO = new ExtraDTO("The winner");
        Map.Entry<String, Integer> winner = Utilities.getInstance(playerRepository, scoreRepository)
                .getPlayersRanking()
                .entrySet()
                .stream().findFirst().orElse(null);

        return getHttpEntity(extraDTO, winner);
    }


    /**
     * GET /players/ranking/winner: retorna el jugador amb pitjor percentatge d’èxit. 🤩🤖
     */
    public HttpEntity<? extends Message> getLoser()
    {
        ExtraDTO extraDTO = new ExtraDTO("The loser");
        Map<String, Integer> results = Utilities.getInstance(playerRepository, scoreRepository).getPlayersRanking();

        Map.Entry<String, Integer> lastEntry = results
                .entrySet()
                .stream()
                .reduce((one, two) -> two).orElse(null);

        return getHttpEntity(extraDTO, lastEntry);
    }


    /**
     * GET /login /logout
     * player STATUS when is logged 🤩🤖👻
     **/
    public String loggedOk(PlayerDTO playerDTO, boolean mood)
    {
        Optional<PlayerModel> playerLogged =
                (mood ? Optional.ofNullable(playerRepository.findByNamePlayerIgnoreCase(playerDTO.getNamePlayer()))
                        : playerRepository.findById(playerDTO.getIdPlayer()));

        if (playerLogged.isPresent())
        {
            playerLogged.get().setLogged(mood);
            playerRepository.save(playerLogged.get());  //here 'save' update when the player is logged.

            return playerLogged.get().getId();
        }

        throw new RuntimeException("MOVIDA, No se encuentra el id o nombre del player para hacer logout");
    }


    /**
     * GET /player-update/{idPlayer}
     * update data player from DB when dices have been thrown 🤩🤖
     */
    public PlayerDTO getPlayerData(String idPlayer, String token) {

        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setIdPlayer(idPlayer);
        Optional<PlayerModel> isIn = playerRepository.findById(idPlayer);

        if (isIn.isPresent()) {

            playerDTO.setNamePlayer(isIn.get().getNamePlayer());
            playerDTO.setLogged(isIn.get().getLogged());
            playerDTO.setSignUpDate(isIn.get().getSignUpDate());

            playerDTO.setScoreList(Utilities.getInstance(playerRepository, scoreRepository)
                    .scoreQueryFromIdPlayer(idPlayer));

            playerDTO.setGames(playerDTO.getGames());
            playerDTO.setToken(token);
            playerDTO.setRanking(Utilities.getInstance(playerRepository, scoreRepository)
                    .getRankingPlayer(playerDTO.getIdPlayer()));
            playerDTO.setAverage(playerDTO.getAverage());

        } else
            throw new RuntimeException("movida en getPlayerData");

        return playerDTO;
    }



    /* ***********************************************************
     * AUXILIAR METHODS
     *************************************************************
     */

    /**
     * @return Obj and message depending on action it's called
     */
    private HttpEntity<? extends Message> getHttpEntity(ExtraDTO extraDTO, Map.Entry<String, Integer> lastEntry) {
        if (lastEntry != null) {
            extraDTO.setIdPlayer(lastEntry.getKey());
            extraDTO.setWins(lastEntry.getValue());
            extraDTO.setNamePlayer(playerRepository.findById(lastEntry.getKey()).get().getNamePlayer());
            return new ResponseEntity<>(extraDTO, HttpStatus.OK);

        } else return new ResponseEntity<>
                (new ErrorDetails(500, "No elements"), HttpStatus.NOT_FOUND);
    }


    /**
     * Get IdPlayer using namePlayer
     *
     * @returns String
     */
    public String getIdByNamePlayer(String namePlayer) {

        PlayerModel playerModel = this.playerRepository.findByNamePlayerIgnoreCase(namePlayer);
        return playerModel.getId();
    }


    /**
     * Passing PlayerModel PlayerDTO
     *
     * @return List << PlayerDTO>>
     */
    public List<PlayerDTO> getPlayerDTOList() {
        Iterator<PlayerModel> playersList = playerRepository.findAll().iterator();
        List<PlayerDTO> playerListDTO = new ArrayList<>();

        while (playersList.hasNext()) {
            playerListDTO.add(getPlayerData(playersList.next().getId(), ""));
        }
        return playerListDTO;
    }


    public List<PlayerModel> findAllLogged() {

        return playerRepository.findAllByLogged(true);
    }

}
