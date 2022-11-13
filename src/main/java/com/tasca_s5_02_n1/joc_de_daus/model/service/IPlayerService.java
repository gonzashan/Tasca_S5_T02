package com.tasca_s5_02_n1.joc_de_daus.model.service;

import com.tasca_s5_02_n1.joc_de_daus.model.domain.PlayerModel;
import com.tasca_s5_02_n1.joc_de_daus.model.domain.ScoreModel;
import com.tasca_s5_02_n1.joc_de_daus.model.dto.PlayerDTO;
import com.tasca_s5_02_n1.joc_de_daus.model.exception.Message;
import com.tasca_s5_02_n1.joc_de_daus.model.exception.RequestOk;
import com.tasca_s5_02_n1.joc_de_daus.model.messages.RankingMessage;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface IPlayerService {

    // Method for saving a record
    String addingNewPlayer(PlayerDTO newPlayer);

    ResponseEntity<Message> getListPlayerWithAverage();

    void deleteScoreFromIdPlayer(String idPlayer);

    ResponseEntity<RequestOk> createDiceThrown(ScoreModel scoreModel);

    // Method returning a record requested to log in
    String loggedOk(PlayerDTO playerDTO, boolean mood);

    PlayerDTO getPlayerData(String idPlayer, String token);

    ResponseEntity<? extends Message> isThisNameRegistered(PlayerModel newPlayer,
                                                           boolean typeAction);
    String getIdByNamePlayer(String namePlayer);

    ResponseEntity<RequestOk> deletePlayer(String token, String id, PlayerModel playerModel);

    ResponseEntity<RequestOk> deleteScoreListById(PlayerModel playerFound);

    ResponseEntity<RankingMessage> getRanking();

    HttpEntity<? extends Message> getWinner();

    HttpEntity<? extends Message> getLoser();

    List<PlayerDTO> getPlayerDTOList();

    List<PlayerModel> findAllLogged();
}
