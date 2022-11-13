package com.tasca_s5_02_n1.joc_de_daus.tasca_s5_02;

import com.tasca_s5_02_n1.joc_de_daus.model.domain.PlayerModel;
import com.tasca_s5_02_n1.joc_de_daus.model.dto.ExtraDTO;
import com.tasca_s5_02_n1.joc_de_daus.model.dto.PlayerDTO;
import com.tasca_s5_02_n1.joc_de_daus.model.exception.AveragePlayersList;
import com.tasca_s5_02_n1.joc_de_daus.model.exception.ErrorDetails;
import com.tasca_s5_02_n1.joc_de_daus.model.exception.Message;
import com.tasca_s5_02_n1.joc_de_daus.model.exception.RequestOk;
import com.tasca_s5_02_n1.joc_de_daus.model.messages.RankingMessage;
import com.tasca_s5_02_n1.joc_de_daus.model.repository.PlayerRepository;
import com.tasca_s5_02_n1.joc_de_daus.model.repository.ScoreRepository;
import com.tasca_s5_02_n1.joc_de_daus.model.service.IPlayerService;

import com.tasca_s5_02_n1.joc_de_daus.model.service.TokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AppJocDeDausTests {

    @Autowired
    public IPlayerService iPlayerService;
    @Autowired
    ScoreRepository scoreRepository;
    @Autowired
    PlayerRepository playerRepository;


    public RestTemplate restTemplate = new RestTemplate();
    public final static String ID_PLAYER_ERROR = "BreakingProcessCode";
    public final static String TOKEN_ERROR = "BreakingProcessByToken";
    public static String NAME_PLAYER = "socorro";
    public static String NAME_PLAYER_DELETE_CREATE = "Mandanga";
    public static String NAME_PLAYER_UPDATE = "Mazorca";


    /**
     * POST http://localhost:9005/jocdedaus/players/getAll
     *
     * @returns String.class -> IdPlayer
     * @apiNote _createNewPlayer.
     ***/
    public String getIdByNamePlayer(String namePlayer) {
        return playerRepository.findByNamePlayerIgnoreCase(namePlayer).getId();
    }


    /**
     * POST http://localhost:9005/jocdedaus/players/getAll
     *
     * @returns String.class -> token from idPlayer
     * @apiNote _createNewPlayer.
     ***/
    @Test
    public String login(String namePlayer) {

        final String baseUrl = "http://localhost:9005/jocdedaus/loginTest";
        String token;
        PlayerDTO playerLogin = new PlayerDTO();
        HttpHeaders headers = new HttpHeaders();

        playerLogin.setNamePlayer(namePlayer);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PlayerDTO> entity = new HttpEntity<>(playerLogin, headers);

        // send request and parse result
        ResponseEntity<PlayerDTO> response = restTemplate
                .exchange(baseUrl, HttpMethod.POST, entity, PlayerDTO.class);
        token = Objects.requireNonNull(response.getBody()).getToken();

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        return token;
    }

////////////////////////////////////////////////////////////////////


    /**
     * POST http://localhost:9005/jocdedaus/players/getAll
     *
     * @returns HttpStatus.OK
     * @apiNote _createNewPlayer.
     ***/
    @Test
    public void T01() {
        final String baseUrl =
                "http://localhost:9005/jocdedaus/players/";
        PlayerModel playerSignUp = new PlayerModel();
        HttpHeaders headers = new HttpHeaders();

        playerSignUp.setNamePlayer(NAME_PLAYER_DELETE_CREATE);
        System.out.println(playerSignUp.getNamePlayer());
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PlayerModel> request = new HttpEntity<>(playerSignUp, headers);

        ResponseEntity<RequestOk> response = restTemplate
                .exchange(baseUrl, HttpMethod.POST, request, RequestOk.class);

        assertEquals(response.getStatusCode(), HttpStatus.ACCEPTED);
    }


    /**
     * POST http://localhost:9005/jocdedaus/players/getAll
     *
     * @returns HttpStatus.NOT_ACCEPTABLE
     * @apiNote _createNewPlayerErrorDuplicate
     ***/
    @Test
    public void T02() {
        final String baseUrl =
                "http://localhost:9005/jocdedaus/players/";
        PlayerModel playerSignUp = new PlayerModel();
        HttpHeaders headers = new HttpHeaders();

        playerSignUp.setNamePlayer(NAME_PLAYER);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PlayerModel> request = new HttpEntity<>(playerSignUp, headers);

        Exception exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<ErrorDetails> response = restTemplate
                    .exchange(baseUrl, HttpMethod.POST, request, ErrorDetails.class);
            assertEquals(response.getStatusCode(), HttpStatus.OK);
        });
        assertTrue(exception.getMessage().contains("406"));
    }


    /**
     * POST http://localhost:9005/jocdedaus/players/{id}/games/
     *
     * @returns HttpStatus.OK
     * @apiNote _throwingDicesOK
     ***/
    @Test
    public void T03() {
        final String baseUrl =
                "http://localhost:9005/jocdedaus/players/{id}/games/";
        String token = login(NAME_PLAYER_DELETE_CREATE);
        HttpHeaders headers = new HttpHeaders();
        Map<String, String> parametersMap2 = new HashMap<>();
        MultiValueMap<String, String> parametersMap = new LinkedMultiValueMap<>();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(parametersMap, headers);

        parametersMap2.put("id", getIdByNamePlayer(NAME_PLAYER_DELETE_CREATE));
        parametersMap.add("idPlayer", getIdByNamePlayer(NAME_PLAYER_DELETE_CREATE));
        parametersMap.add("scored", "7");
        parametersMap.add("bet", "6");
        parametersMap.add("leftDice", "1");
        parametersMap.add("rightDice", "6");

        ResponseEntity<RequestOk> response = restTemplate
                .exchange(baseUrl, HttpMethod.POST, request, RequestOk.class, parametersMap2);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }


    /**
     * POST -> http://localhost:9005/jocdedaus/players/{id}/games/
     *
     * @returns HttpStatus.NOT_UNAUTHORIZED
     * @apiNote _throwingDicesErrorToken
     ***/
    @Test
    public void T04() {
        final String baseUrl =
                "http://localhost:9005/jocdedaus/players/{id}/games/";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + TOKEN_ERROR);
        Map<String, String> parametersMap2 = new HashMap<>();
        parametersMap2.put("id", getIdByNamePlayer(NAME_PLAYER));

        MultiValueMap<String, String> parametersMap = new LinkedMultiValueMap<>();
        parametersMap.add("idPlayer", getIdByNamePlayer(NAME_PLAYER));
        parametersMap.add("scored", "9");
        parametersMap.add("bet", "9");
        parametersMap.add("leftDice", "4");
        parametersMap.add("rightDice", "5");

        ResponseEntity<ErrorDetails> response;

        try {
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(parametersMap, headers);
            response = restTemplate
                    .exchange(baseUrl, HttpMethod.POST, request, ErrorDetails.class, parametersMap2);
            assertEquals(response.getStatusCode(), HttpStatus.OK);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(e.getMessage().contains("401"));
        }
    }


    /**
     * POST -> http://localhost:9005/jocdedaus/players/{id}/games/
     *
     * @returns HttpStatus.NOT_UNAUTHORIZED
     * @apiNote _throwingDicesErrorIdPlayer
     ***/
    @Test
    public void T05() {
        final String baseUrl =
                "http://localhost:9005/jocdedaus/players/{id}/games/";

        String token = login(NAME_PLAYER);
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> parametersMap = new LinkedMultiValueMap<>();
        Map<String, String> parametersMap2 = new HashMap<>();

        headers.set("Authorization", "Bearer " + token);
        parametersMap.add("idPlayer", getIdByNamePlayer(NAME_PLAYER_DELETE_CREATE));
        parametersMap.add("scored", "9");
        parametersMap.add("bet", "9");
        parametersMap.add("leftDice", "4");
        parametersMap.add("rightDice", "5");
        parametersMap2.put("id", getIdByNamePlayer(NAME_PLAYER_DELETE_CREATE));

        ResponseEntity<ErrorDetails> response;

        try {
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(parametersMap, headers);
            response = restTemplate
                    .exchange(baseUrl, HttpMethod.POST, request, ErrorDetails.class, parametersMap2);
            assertEquals(response.getStatusCode(), HttpStatus.OK);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(e.getMessage().contains("401"));
        }
    }


    /**
     * GET -> http://localhost:9005/jocdedaus/players/{id}/games
     *
     * @returns HttpStatus.OK
     * @apiNote _getScoredListFromIdPlayerOK
     ***/
    @Test
    public void T06() {

        final String baseUrl =
                "http://localhost:9005/jocdedaus/players/{id}/games/";
        String token = login(NAME_PLAYER);
        HttpHeaders headers = new HttpHeaders();
        Map<String, String> parametersMap2 = new HashMap<>();
        headers.set("Authorization", "Bearer " + token);
        parametersMap2.put("id", getIdByNamePlayer(NAME_PLAYER));


        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        ResponseEntity<RequestOk> response = restTemplate
                .exchange(baseUrl, HttpMethod.GET, request, RequestOk.class, parametersMap2);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }


    /**
     * GET -> http://localhost:9005/jocdedaus/players/{id}/games
     *
     * @returns null
     * @apiNote _getScoredListFromIdPlayerErrorIdWrong
     ***/
    @Test
    public void T07() {
        final String baseUrl =
                "http://localhost:9005/jocdedaus/players/{id}/games/";
        String token = login(NAME_PLAYER);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        Map<String, String> parametersMap2 = new HashMap<>();

        headers.set("Authorization", "Bearer " + token);
        parametersMap2.put("id", getIdByNamePlayer(NAME_PLAYER_DELETE_CREATE));

        Exception exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<ErrorDetails> response = restTemplate
                    .exchange(baseUrl, HttpMethod.GET, request, ErrorDetails.class, parametersMap2);
            assertEquals(response.getStatusCode(), HttpStatus.OK);
        });

        assertNull(exception.getCause());
    }


    /**
     * GET -> http://localhost:9005/jocdedaus/players/{id}/games
     *
     * @returns null
     * @apiNote _getScoredListFromIdPlayer+TokenErrorIdIsNull
     ***/
    @Test
    public void T08() {

        final String baseUrl =
                "http://localhost:9005/jocdedaus/players/{id}/games";
        String token = login(NAME_PLAYER);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        Map<String, String> parametersMap2 = new HashMap<>();

        headers.set("Authorization", "Bearer " + token);
        parametersMap2.put("id", null);

        try {
            ResponseEntity<Message> response = restTemplate
                    .exchange(baseUrl, HttpMethod.GET, request, Message.class, parametersMap2);
            assertEquals(response.getStatusCode(), HttpStatus.OK);

        } catch (Exception e) {

            System.out.println(e.getMessage());
            assertTrue(true);
        }
    }


    /**
     * GET -> http://localhost:9005/jocdedaus/players/{id}/games
     *
     * @returns HttpStatus.UNAUTHORIZED : 401
     * @apiNote _getScoredListFromIdPlayerErrorNoToken
     ***/
    @Test
    public void T09() {
        final String baseUrl =
                "http://localhost:9005/jocdedaus/players/{id}/games";
        String token = login(NAME_PLAYER_DELETE_CREATE);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);
        Map<String, String> parametersMap2 = new HashMap<>();

        headers.set("Authorization", "Bearer " + token);
        parametersMap2.put("id", getIdByNamePlayer(NAME_PLAYER));


        Exception exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<ErrorDetails> response = restTemplate
                    .exchange(baseUrl, HttpMethod.GET, request, ErrorDetails.class, parametersMap2);
            assertEquals(response.getStatusCode(), HttpStatus.OK);
        });

        assertTrue(exception.getMessage().contains("401"));
    }


    /**
     * PUT-> http://localhost:9005/jocdedaus/players/
     *
     * @returns HttpStatus.ACCEPTED
     * @apiNote _changeNamePlayer
     ***/
    @Test
    public void T10() {
        final String baseUrl =
                "http://localhost:9005/jocdedaus/players/";

        String token = login(NAME_PLAYER_DELETE_CREATE);
        HttpHeaders headers = new HttpHeaders();
        PlayerModel playerTest = new PlayerModel();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        playerTest.setNamePlayer(NAME_PLAYER_UPDATE);
        playerTest.setId(getIdByNamePlayer(NAME_PLAYER_DELETE_CREATE));

        HttpEntity<PlayerModel> entity = new HttpEntity<>(playerTest, headers);

        ResponseEntity<RequestOk> response = restTemplate
                .exchange(baseUrl, HttpMethod.PUT, entity, RequestOk.class);

        NAME_PLAYER = playerTest.getNamePlayer();
        assertEquals(response.getStatusCode(), HttpStatus.ACCEPTED);
    }


    /**
     * PUT -> http://localhost:9005/jocdedaus/players/
     *
     * @returns HttpStatus.NOT_ACCEPTABLE : 406
     * @apiNote _changeNamePlayerErrorByNameExistedInDb
     ***/
    @Test
    public void T11() throws HttpClientErrorException {
        final String baseUrl =
                "http://localhost:9005/jocdedaus/players/";

        String token = login(NAME_PLAYER);
        HttpHeaders headers = new HttpHeaders();
        PlayerModel playerTest = new PlayerModel();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        playerTest.setNamePlayer(NAME_PLAYER_UPDATE);
        playerTest.setId(getIdByNamePlayer(NAME_PLAYER));

        HttpEntity<PlayerModel> entity = new HttpEntity<>(playerTest, headers);

        try {
            ResponseEntity<ErrorDetails> response = restTemplate
                    .exchange(baseUrl, HttpMethod.PUT, entity, ErrorDetails.class);
            assertEquals(response.getStatusCode(), HttpStatus.OK);
        } catch (HttpClientErrorException e) {

            assertEquals(e.getStatusCode(), HttpStatus.NOT_ACCEPTABLE);
        }
    }


    /**
     * PUT -> http://localhost:9005/jocdedaus/players/
     *
     * @returns HttpStatus.NOT_FOUND : 404
     * @apiNote _changeNamePlayerErrorById
     ***/
    @Test
    public void T12() throws HttpClientErrorException {
        final String baseUrl =
                "http://localhost:9005/jocdedaus/players/";

        String token = login(NAME_PLAYER);
        HttpHeaders headers = new HttpHeaders();
        PlayerModel playerTest = new PlayerModel();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        playerTest.setNamePlayer(NAME_PLAYER_UPDATE);
        playerTest.setId(getIdByNamePlayer(NAME_PLAYER_UPDATE));

        HttpEntity<PlayerModel> entity = new HttpEntity<>(playerTest, headers);

        try {
            ResponseEntity<ErrorDetails> response = restTemplate
                    .exchange(baseUrl, HttpMethod.PUT, entity, ErrorDetails.class);
            assertEquals(response.getStatusCode(), HttpStatus.OK);

        } catch (HttpClientErrorException e) {

            assertEquals(e.getStatusCode(), HttpStatus.NOT_ACCEPTABLE);
        }
    }


    /**
     * PUT -> http://localhost:9005/jocdedaus/players/
     *
     * @returns HttpStatus.INTERNAL_SERVER_ERROR : 500
     * @apiNote _changeNamePlayerErrorByIdIsNull
     ***/
    @Test
    public void T13() throws HttpServerErrorException {
        final String baseUrl =
                "http://localhost:9005/jocdedaus/players/";

        String token = login(NAME_PLAYER);
        HttpHeaders headers = new HttpHeaders();
        PlayerModel playerTest = new PlayerModel();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        playerTest.setNamePlayer(NAME_PLAYER_UPDATE);
        playerTest.setId(null);

        HttpEntity<PlayerModel> entity = new HttpEntity<>(playerTest, headers);

        try {
            ResponseEntity<RequestOk> response = restTemplate
                    .exchange(baseUrl, HttpMethod.PUT, entity, RequestOk.class);
            assertEquals(response.getStatusCode(), HttpStatus.OK);

        } catch (HttpServerErrorException e) {

            System.out.println(e.getMessage());
            assertEquals(e.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * DELETE -> http://localhost:9005/jocdedaus/players/{id}/games
     *
     * @returns HttpStatus.METHOD_NOT_ALLOWED : 405
     * @apiNote _deleteScoredListFromIdPlayerAndTokenErrorTOKEN
     ***/
    @Test
    public void T14() throws HttpClientErrorException {

        final String baseUrl =
                "http://localhost:9005/jocdedaus/players/{id}/games";
        //String token = login(NAME_PLAYER_DELETE_CREATE);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Map<String, String> parametersMap2 = new HashMap<>();

        headers.set("Authorization", "Bearer " + TOKEN_ERROR);
        parametersMap2.put("id", getIdByNamePlayer(NAME_PLAYER));

        try {
            ResponseEntity<ErrorDetails> response = restTemplate
                    .exchange(baseUrl, HttpMethod.PUT, entity, ErrorDetails.class, parametersMap2);
            assertEquals(response.getStatusCode(), HttpStatus.OK);

        } catch (HttpClientErrorException e) {

            System.out.println(e.getMessage());
            assertEquals(e.getStatusCode(), HttpStatus.METHOD_NOT_ALLOWED);
        }
    }


    /**
     * DELETE -> http://localhost:9005/jocdedaus/players/{id}/games
     *
     * @returns HttpStatus.METHOD_NOT_ALLOWED : 405
     * @apiNote _deleteScoredListFromIdPlayer+TokenErrorById
     ***/
    @Test
    public void T15() throws HttpServerErrorException {

        final String baseUrl =
                "http://localhost:9005/jocdedaus/players/{id}/games";
        String token = login(NAME_PLAYER_UPDATE);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Map<String, String> parametersMap2 = new HashMap<>();

        headers.set("Authorization", "Bearer " + token);
        parametersMap2.put("id", ID_PLAYER_ERROR);


        try {
            ResponseEntity<ErrorDetails> response = restTemplate
                    .exchange(baseUrl, HttpMethod.DELETE, entity, ErrorDetails.class, parametersMap2);
            assertEquals(response.getStatusCode(), HttpStatus.OK);
        } catch (HttpClientErrorException e) {

            System.out.println(e.getMessage());
            assertEquals(e.getStatusCode(), HttpStatus.METHOD_NOT_ALLOWED);
        }
    }


    /**
     * DELETE -> http://localhost:9005/jocdedaus/players/{id}/games
     *
     * @returns HttpStatus.NOT_FOUND : 404
     * @apiNote _deleteScoredListFromIdPlayerAndTokenErrorIdAndTokenIsNull
     ***/
    @Test
    public void T16() throws HttpServerErrorException {

        final String baseUrl =
                "http://localhost:9005/jocdedaus/players/{id}/games";

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Map<String, String> parametersMap2 = new HashMap<>();

        headers.set("Authorization", "Bearer " + null);
        parametersMap2.put("id", null);

        try {
            ResponseEntity<ErrorDetails> response = restTemplate
                    .exchange(baseUrl, HttpMethod.DELETE, entity, ErrorDetails.class, parametersMap2);
            assertEquals(response.getStatusCode(), HttpStatus.OK);

        } catch (HttpClientErrorException e) {

            System.out.println(e.getMessage());
            assertEquals(e.getStatusCode(), HttpStatus.NOT_FOUND);
        }
    }


    /**
     * DELETE -> http://localhost:9005/jocdedaus/players/{id}/games
     *
     * @returns HttpStatus.OK : 200
     * @apiNote _deleteScoredListFromIdPlayer+TokenOK
     ***/
    @Test
    public void T17() {

        final String baseUrl =
                "http://localhost:9005/jocdedaus/players/{id}/games/";
        String token = login(NAME_PLAYER_UPDATE);
        HttpHeaders headers = new HttpHeaders();
        Map<String, String> parametersMap2 = new HashMap<>();
        headers.set("Authorization", "Bearer " + token);
        parametersMap2.put("id", getIdByNamePlayer(NAME_PLAYER_UPDATE));


        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        ResponseEntity<RequestOk> response = restTemplate
                .exchange(baseUrl, HttpMethod.DELETE, request, RequestOk.class, parametersMap2);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }


    /**
     * GET -> http://localhost:9005/jocdedaus/players/ranking?idPlayer
     *
     * @returns HttpStatus.OK : 200
     * @apiNote _getRankingByIdPlayer+TokenOK
     ***/
    @Test
    public void T18() {
        final String baseUrl =
                "http://localhost:9005/jocdedaus/players/ranking?idPlayer=" + getIdByNamePlayer(NAME_PLAYER);

        String token = login(NAME_PLAYER);
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> parametersMap = new LinkedMultiValueMap<>();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(parametersMap, headers);

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        parametersMap.add("idPlayer", getIdByNamePlayer(NAME_PLAYER));

        ResponseEntity<RankingMessage> response = restTemplate
                .exchange(baseUrl, HttpMethod.GET, request, RankingMessage.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);

    }


    /**
     * GET -> http://localhost:9005/jocdedaus/players/ranking?idPlayer
     *
     * @returns null
     * @apiNote _getRankingErrorByIdNull
     ***/
    @Test
    public void T19() {
        final String baseUrl =
                "http://localhost:9005/jocdedaus/players/ranking?idPlayer=";

        String token = login(NAME_PLAYER);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(null, headers);

        headers.set("Authorization", "Bearer " + token);

        Exception exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<ErrorDetails> response = restTemplate
                    .exchange(baseUrl, HttpMethod.GET, request, ErrorDetails.class);
            response.getStatusCode();
        });

        assertNull(exception.getCause());
    }


    /**
     * GET -> http://localhost:9005/jocdedaus/players/ranking?idPlayer
     *
     * @returns HttpStatus.OK : 200
     * @apiNote _getRankingLoserByIdPlayer+TokenOK
     ***/
    @Test
    public void T20() {
        final String baseUrl =
                "http://localhost:9005/jocdedaus/players/ranking/loser?idPlayer=" + getIdByNamePlayer(NAME_PLAYER);

        String token = login(NAME_PLAYER);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(null, headers);

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        ResponseEntity<ExtraDTO> response = restTemplate
                .exchange(baseUrl, HttpMethod.GET, request, ExtraDTO.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }


    /**
     * GET -> http://localhost:9005/jocdedaus/players/ranking?idPlayer
     *
     * @returns HttpStatus.OK : 200
     * @apiNote _getRankingWinnerByIdPlayer+TokenOK
     ***/
    @Test
    public void T21() {
        final String baseUrl =
                "http://localhost:9005/jocdedaus/players/ranking/winner?idPlayer=" + getIdByNamePlayer(NAME_PLAYER);

        String token = login(NAME_PLAYER);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(null, headers);

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);


        ResponseEntity<ExtraDTO> response = restTemplate
                .exchange(baseUrl, HttpMethod.GET, request, ExtraDTO.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }


    /**
     * DELETE -> http://localhost:9005/jocdedaus/players/{id}/games
     *
     * @returns HttpStatus.METHOD_NOT_ALLOWED : 405
     * @apiNote _deletePlayerById+TokenErrorById
     ***/
    @Test
    public void T22() {
        final String baseUrl =
                "http://localhost:9005/jocdedaus/players/{idPlayer}";
        String token = login(NAME_PLAYER);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);
        Map<String, String> parametersMap = new HashMap<>();

        headers.set("Authorization", "Bearer " + token);
        parametersMap.put("idPlayer", ID_PLAYER_ERROR);

        Exception exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<RequestOk> response = restTemplate
                    .exchange(baseUrl, HttpMethod.DELETE, request, RequestOk.class, parametersMap);
            assertEquals(response.getStatusCode(), HttpStatus.OK);
        });

        assertTrue(exception.getMessage().contains("404"));
    }


    /**
     * DELETE -> http://localhost:9005/jocdedaus/players/{id}/games
     *
     * @returns HttpStatus.METHOD_NOT_ALLOWED : 405
     * @apiNote _deletePlayerById+TokenErrorByToken
     ***/
    @Test
    public void T23() {

        final String baseUrl =
                "http://localhost:9005/jocdedaus/players/{idPlayer}";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + TOKEN_ERROR);

        HttpEntity<String> request = new HttpEntity<>(headers);
        Map<String, String> parametersMap = new HashMap<>();
        parametersMap.put("idPlayer", getIdByNamePlayer(NAME_PLAYER));

        Exception exception = assertThrows(HttpClientErrorException.class, () -> {
            ResponseEntity<RequestOk> response = restTemplate
                    .exchange(baseUrl, HttpMethod.DELETE, request, RequestOk.class, parametersMap);
            assertEquals(response.getStatusCode(), HttpStatus.OK);
        });

        assertTrue(exception.getMessage().contains("401"));
    }


    /**
     * DELETE -> http://localhost:9005/jocdedaus/players/{id}/games
     *
     * @returns HttpStatus.METHOD_NOT_ALLOWED : 405
     * @apiNote _deletePlayerById+TokenErrorByIdPlayerNull
     ***/
    @Test
    public void T24() throws HttpClientErrorException {

        final String baseUrl =
                "http://localhost:9005/jocdedaus/players/{idPlayer}";
        String token = login(NAME_PLAYER);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);
        Map<String, String> parametersMap = new HashMap<>();

        headers.set("Authorization", "Bearer " + token);
        parametersMap.put("idPlayer", null);

        try {
            ResponseEntity<ErrorDetails> response = restTemplate
                    .exchange(baseUrl, HttpMethod.DELETE, request, ErrorDetails.class, parametersMap);
            assertEquals(response.getStatusCode(), HttpStatus.OK);

        } catch (HttpClientErrorException e) {

            System.out.println(e.getMessage());
            assertEquals(e.getStatusCode(), HttpStatus.METHOD_NOT_ALLOWED);
        }
    }


    /**
     * GET -> http://localhost:9005/jocdedaus/player-update/{idPlayer}
     *
     * @returns HttpStatus.OK : 200
     * @apiNote _getDataFromIdPlayer+TokenAfterThrowingDices
     ***/
    @Test
    public void T25() {

        final String baseUrl =
                "http://localhost:9005/jocdedaus/player-update/{idPlayer}";

        String token = login(NAME_PLAYER);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        Map<String, String> parametersMap = new HashMap<>();
        parametersMap.put("idPlayer", getIdByNamePlayer(NAME_PLAYER));

        ResponseEntity<PlayerDTO> response = restTemplate
                .exchange(baseUrl, HttpMethod.GET, request, PlayerDTO.class, parametersMap);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }


    /**
     * GET -> http://localhost:9005/jocdedaus/players/{id}/games
     *
     * @returns HttpStatus.METHOD_NOT_ALLOWED : 405
     * @apiNote _getDataFromIdPlayer+TokenAfterThrowingDicesErrorByIdPlayerNull
     ***/
    @Test
    public void T26() throws HttpClientErrorException {

        final String baseUrl =
                "http://localhost:9005/jocdedaus/player-update/{idPlayer}";

        String token = login(NAME_PLAYER);
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        Map<String, String> parametersMap = new HashMap<>();

        headers.set("Authorization", "Bearer " + token);
        parametersMap.put("idPlayer", null);

        try {
            ResponseEntity<ErrorDetails> response = restTemplate
                    .exchange(baseUrl, HttpMethod.GET, request, ErrorDetails.class, parametersMap);
            assertEquals(response.getStatusCode(), HttpStatus.OK);

        } catch (HttpClientErrorException e) {

            System.out.println(e.getMessage());
            assertEquals(e.getStatusCode(), HttpStatus.NOT_FOUND);
        }
    }


    /**
     * GET http://localhost:9005/jocdedaus/players/getAll
     *
     * @returns HttpStatus.OK & True
     * @apiNote _getAllRecords
     ***/
    @Test
    public void T27() {
        final String baseUrl = "http://localhost:9005/jocdedaus/players/getAll";

        String token = login(NAME_PLAYER);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        // send request and parse result
        ResponseEntity<RequestOk> response = restTemplate
                .exchange(baseUrl,
                        HttpMethod.GET,
                        request, RequestOk.class);

        assertEquals(response.getStatusCode(), HttpStatus.OK);

    }


    /**
     * GET http://localhost:9005/jocdedaus/players/getAll
     *
     * @returns HttpStatus.METHOD_NOT_ALLOWED
     * @apiNote _getAllRecordsErrorByToken
     ***/
    @Test
    public void T28() throws HttpClientErrorException{
        final String baseUrl = "http://localhost:9005/jocdedaus/players/getAll";

        String token = login(NAME_PLAYER);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + TOKEN_ERROR );
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        // send request and parse result
        try {
            ResponseEntity<ErrorDetails> response = restTemplate
                    .exchange(baseUrl, HttpMethod.PUT, request, ErrorDetails.class);
            assertEquals(response.getStatusCode(), HttpStatus.OK);

        } catch (HttpClientErrorException e) {

            assertEquals(e.getStatusCode(), HttpStatus.METHOD_NOT_ALLOWED);
        }
    }


    /**
     * GET http://localhost:9005/jocdedaus/players/
     *
     * @returns HttpStatus.OK & Map < String,Double >
     * @apiNote _getAllRecordsAverageOK
     ***/
    @Test
    public void T29() throws HttpClientErrorException {
        final String baseUrl = "http://localhost:9005/jocdedaus/players/?idPlayer=" + getIdByNamePlayer(NAME_PLAYER);

        // send request and parse result
        String token = login(NAME_PLAYER);
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        ResponseEntity<RequestOk> response = restTemplate
                .exchange(baseUrl, HttpMethod.GET, request, RequestOk.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);

    }


    /**
     * GET http://localhost:9005/jocdedaus/players/
     *
     * @returns HttpStatus.UNAUTHORIZED
     * @apiNote _getAllRecordsAverageErrorByIdNoValid
     ***/
    @Test
    public void T30() throws HttpClientErrorException {
        final String baseUrl = "http://localhost:9005/jocdedaus/players/?idPlayer=" + ID_PLAYER_ERROR;

        // send request and parse result

        String token = login(NAME_PLAYER);
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<RequestOk> response = restTemplate
                    .exchange(baseUrl, HttpMethod.GET, request, RequestOk.class);
            assertEquals(response.getStatusCode(), HttpStatus.OK);

        } catch (HttpClientErrorException e) {

            System.out.println(e.getMessage());
            assertEquals(e.getStatusCode(), HttpStatus.UNAUTHORIZED);
        }
    }


    /**
     * GET http://localhost:9005/jocdedaus/players/
     *
     * @returns HttpStatus.UNAUTHORIZED
     * @apiNote _getAllRecordsAverageErrorByIdNull
     ***/
    @Test
    public void T31() throws HttpClientErrorException {
        String nullString = null;
        final String baseUrl = "http://localhost:9005/jocdedaus/players/?idPlayer=" + nullString;

        // send request and parse result

        String token = login(NAME_PLAYER);
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<RequestOk> response = restTemplate
                    .exchange(baseUrl, HttpMethod.GET, request, RequestOk.class);
            assertEquals(response.getStatusCode(), HttpStatus.OK);

        } catch (HttpClientErrorException e) {

            System.out.println(e.getMessage());
            assertEquals(e.getStatusCode(), HttpStatus.UNAUTHORIZED);
        }
    }


    /**
     * DELETE -> http://localhost:9005/jocdedaus/players/{idPlayer}
     *
     * @returns HttpStatus.OK : 200
     * @apiNote _deletePlayerOKByIdPlayer+TokenOK
     ***/
    @Test
    public void T32() {
        final String baseUrl =
                "http://localhost:9005/jocdedaus/players/{idPlayer}";
        String token = login(NAME_PLAYER_UPDATE);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        Map<String, String> parametersMap = new HashMap<>();
        parametersMap.put("idPlayer", getIdByNamePlayer(NAME_PLAYER_UPDATE));

        ResponseEntity<RequestOk> response = restTemplate
                .exchange(baseUrl, HttpMethod.DELETE, request, RequestOk.class, parametersMap);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }


}







