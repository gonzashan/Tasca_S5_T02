package com.tasca_s5_02_n1.joc_de_daus.tasca_s5_02;


import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.tasca_s5_02_n1.joc_de_daus.model.domain.PlayerModel;
import com.tasca_s5_02_n1.joc_de_daus.model.domain.ScoreModel;
import com.tasca_s5_02_n1.joc_de_daus.model.dto.PlayerDTO;
import com.tasca_s5_02_n1.joc_de_daus.model.exception.AveragePlayersList;
import com.tasca_s5_02_n1.joc_de_daus.model.exception.Message;
import com.tasca_s5_02_n1.joc_de_daus.model.exception.RequestOk;
import com.tasca_s5_02_n1.joc_de_daus.model.repository.PlayerRepository;
import com.tasca_s5_02_n1.joc_de_daus.model.repository.ScoreRepository;
import com.tasca_s5_02_n1.joc_de_daus.model.service.PlayerService;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

@ExtendWith(MockitoExtension.class)

//@WebMvcTest(RestController.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AppTestMock {

    @Mock
    private PlayerRepository playerRepositoryMocked;
    @Mock
    private ScoreRepository scoreRepositoryMocked;


    @Before
    public void setUp() {
       // MockitoAnnotations.initMocks(this); // this is needed for initiation of mocks, if you use @Mock
        //playerService = new PlayerService();

    }


    @Test
    void testFindAllEmptyResponse() {
        when(this.playerRepositoryMocked.findAllByLogged(true)).thenReturn(Collections.emptyList());
        assertThat(this.playerRepositoryMocked.findAllByLogged(true)).hasSize(0);
    }


    @Test
    public void addingNewPlayer() {
        PlayerService playerService = new PlayerService(this.playerRepositoryMocked, this.scoreRepositoryMocked);
        PlayerDTO emp = new PlayerDTO("James Cameron");

        lenient().when(playerRepositoryMocked.save(Mockito.any(PlayerModel.class)))
                .thenReturn(new PlayerModel("new_player", emp.getNamePlayer()));
        assertEquals(playerService.addingNewPlayer(emp), emp.getIdPlayer());
    }



    @Test
    void isThisNameRegistered() {
        PlayerService playerService = new PlayerService(this.playerRepositoryMocked, this.scoreRepositoryMocked);

        PlayerModel playerModel = new PlayerModel("fiction", "Madroño");

        // THE PLAYER IS NEW RECORD
        when(playerRepositoryMocked.findByNamePlayerIgnoreCase(playerModel.getNamePlayer().toLowerCase()))
                .thenReturn(null);
        ResponseEntity<? extends Message> responseIsinDB = playerService.isThisNameRegistered(playerModel,true);

        // THE PLAYER IS ALREADY REGISTRED
        when(playerRepositoryMocked.findByNamePlayerIgnoreCase(playerModel.getNamePlayer().toLowerCase()))
                .thenReturn(playerModel);
        ResponseEntity<? extends Message> responseIsNotinDB = playerService.isThisNameRegistered(playerModel,true);

        assertEquals(responseIsinDB.getStatusCode(), HttpStatus.ACCEPTED);
        assertEquals(responseIsNotinDB.getStatusCode(), HttpStatus.NOT_ACCEPTABLE);
    }


    /**
     * @apiNote playerService.deleteScoreListById(PlayerModel playerFound)
     * @implSpec  findByIdPlayer
     **/
    @Test
    public void deleteScoreListPlayer() {
        PlayerService playerService = new PlayerService(this.playerRepositoryMocked, this.scoreRepositoryMocked);

        PlayerModel playerModel = new PlayerModel("Virtual_Id_2", "Charles Bronson");

        List<ScoreModel> scoreMock2 = new ArrayList<>(List.of(
                new ScoreModel("virtual_Score_Id_1","Virtual_IdPlayer_2", 6, 4, 1, 3),
                new ScoreModel("virtual_Score_Id_2","Virtual_IdPlayer_2", 11, 7, 5, 6),
                new ScoreModel("virtual_Score_Id_3","Virtual_IdPlayer_2", 10, 10, 5, 5),
                new ScoreModel("virtual_Score_Id_4","Virtual_IdPlayer_2", 7, 5, 4, 3)));

        // Mocking to Score Repository 👻
        lenient().when(scoreRepositoryMocked.findByIdPlayer(playerModel.getId()))
                .thenReturn(scoreMock2);


        // Preparing The Response
        ResponseEntity<RequestOk> response = playerService.deleteScoreListById(playerModel);
        HttpStatus newResponse = response.getStatusCode();
        verify(scoreRepositoryMocked, times(4)).deleteById((any()));

        assertEquals(newResponse, HttpStatus.OK);
        assertEquals(scoreMock2.size(),4);
        //verifyNoMoreInteractions(scoreRepositoryMocked);
    }


    /**
     * @apiNote playerService.getListPlayersAverage()
     * @implSpec findById - findAll - findByIdPlayer
     **/
    @Test
    public void getAverage() {
        PlayerService playerService = new PlayerService(this.playerRepositoryMocked, this.scoreRepositoryMocked);

        List<PlayerModel> playerList = new ArrayList<>(
                List.of(new PlayerModel("Virtual_Id_1", "Charles Bronson"),
                        new PlayerModel("Virtual_Id_2", "Aretha Franklin")
                ));

        List<ScoreModel> scoreMock1 = new ArrayList<>(List.of(
                new ScoreModel("Virtual_Id_1", 4, 4, 1, 3),
                new ScoreModel("Virtual_Id_1", 5, 5, 2, 3)));

        List<ScoreModel> scoreMock2 = new ArrayList<>(List.of(
                new ScoreModel("Virtual_Id_2", 6, 4, 1, 3),
                new ScoreModel("Virtual_Id_2", 11, 7, 5, 6),
                new ScoreModel("Virtual_Id_2", 10, 10, 5, 5),
                new ScoreModel("Virtual_Id_2", 7, 5, 4, 3)));

        // Mocking to Player Repository 👻
        lenient().when(playerRepositoryMocked.findAll())
                .thenReturn(playerList);

        lenient().when(playerRepositoryMocked.findById("Virtual_Id_1"))
                .thenReturn(Optional.ofNullable(playerList.get(0)));

        lenient().when(playerRepositoryMocked.findById("Virtual_Id_2"))
                .thenReturn(Optional.ofNullable(playerList.get(1)));

        // Mocking to Score Repository 👻
        lenient().when(scoreRepositoryMocked.findByIdPlayer("Virtual_Id_1"))
                .thenReturn(scoreMock1);
        lenient().when(scoreRepositoryMocked.findByIdPlayer("Virtual_Id_2"))
                .thenReturn(scoreMock2);

        // Preparing The Response
        ResponseEntity<Message> response = playerService.getListPlayerWithAverage();
        AveragePlayersList newResponse = (AveragePlayersList) response.getBody();

//        for (Map.Entry<String, Double> entry : newResponse.getListPlayersAverage().entrySet()) {
//            String key = entry.getKey();
//            Double value = entry.getValue();
//            System.out.println(key + " " +  value);
//        }
//        System.out.println(newResponse.getListPlayersAverage().get("Aretha Franklin"));

        if (newResponse != null) {
            assertEquals(newResponse.getListPlayersAverage().get("Aretha Franklin"), 25);
            assertEquals(newResponse.getListPlayersAverage().get("Charles Bronson"), 100);
            assertEquals(newResponse.getListPlayersAverage().size(), 2);
        }
    }


    @Test
    void getLoggedOk()
    {
        PlayerService playerService = new PlayerService(this.playerRepositoryMocked, this.scoreRepositoryMocked);

        PlayerModel playerModel = new PlayerModel("fiction", "Madroño");
        PlayerDTO virtualDTOPlayer = new PlayerDTO(playerModel.getId(),playerModel.getNamePlayer());

        lenient().when(playerRepositoryMocked.findByNamePlayerIgnoreCase(virtualDTOPlayer.getNamePlayer()))
                .thenReturn(playerModel);
        lenient().when(playerRepositoryMocked.findById(virtualDTOPlayer.getIdPlayer()))
                .thenReturn(Optional.of(playerModel));

        assertEquals(playerService.loggedOk(virtualDTOPlayer,true),playerModel.getId());
        assertEquals(playerService.loggedOk(virtualDTOPlayer,false),playerModel.getId());
    }





}
/*
  @Test
    void isThisNameRegistered() {
        when(playerRepository.findById("find_something")).thenReturn(Optional.of(new PlayerModel("new_player", "Canister")));

        PlayerModel playerModel = playerRepository.findById("find_something").get();
        assertEquals(playerModel.getNamePlayer(),"Canister");
    }
*/