package com.tasca_s5_02_n1.joc_de_daus.controller;


import com.tasca_s5_02_n1.joc_de_daus.model.domain.PlayerModel;
import com.tasca_s5_02_n1.joc_de_daus.model.domain.ScoreModel;
import com.tasca_s5_02_n1.joc_de_daus.model.dto.PlayerDTO;
import com.tasca_s5_02_n1.joc_de_daus.model.exception.*;
import com.tasca_s5_02_n1.joc_de_daus.model.messages.RankingMessage;
import com.tasca_s5_02_n1.joc_de_daus.model.repository.PlayerRepository;
import com.tasca_s5_02_n1.joc_de_daus.model.repository.ScoreRepository;
import com.tasca_s5_02_n1.joc_de_daus.model.service.IPlayerService;
import com.tasca_s5_02_n1.joc_de_daus.model.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@org.springframework.web.bind.annotation.RestController
@RequestMapping("/jocdedaus")
public class RestController {


    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    ScoreRepository scoreRepository;
    @Autowired
    IPlayerService iPlayerService;



    /* *************************
     * 🤩 -> works on Postman
     * 🤖 -> works on @Test
     * ************************/


    /**
     * POST: /players: crea un jugador/a.🤩🤖
     ***/
    @RequestMapping(value = "/players/", method = RequestMethod.POST)
    @ResponseBody
    public HttpEntity<? extends Message> createdNewPlayer(@RequestBody PlayerModel newPlayer) {
        return iPlayerService.isThisNameRegistered(newPlayer, true);

    }


    /**
     * GET /players/ retorna el llistat de jugadors amb el seu average.🤩🤖
     **/
    @GetMapping("/players/")
    public HttpEntity<? extends Message> getAllPlayersWithAverage(@RequestParam String idPlayer,
                                                                  @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        token = token.split(" ")[1];

        if (!idPlayer.equalsIgnoreCase("") && token != null) {

            if (TokenService.checkAuthorized(token, idPlayer)) {
                return iPlayerService.getListPlayerWithAverage();

            } else {
                return new ResponseEntity<>
                        (new ErrorDetails(401, "Unauthorized!"), HttpStatus.UNAUTHORIZED);
            }

        } else {
            return new ResponseEntity<>
                    (new ErrorDetails(500, "Id or Token could be null."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * GET /players/{id}/games: retorna el llistat de jugades per un jugador/a.🤩🤖
     **/
    @RequestMapping(value = "/players/{id}/games", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<? extends Message> getGamesFromPlayer(@PathVariable String id,
                                                            @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        if (id != null) {
            token = token.split(" ")[1];

            Optional<PlayerModel> playerModel = playerRepository.findById(id);

            if (playerModel.isPresent()) {

                if (TokenService.checkAuthorized(token, id)) {
                    return new ResponseEntity<Message>(
                            new GamesList(scoreRepository.findByIdPlayer(playerModel.get().getId()),
                                    playerModel.get().getNamePlayer()), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>
                            (new ErrorDetails(401, "Unauthorized!"), HttpStatus.UNAUTHORIZED);
                }

            } else {
                return new ResponseEntity<>
                        (new ErrorDetails(500, " didn't found."), HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>
                    (new ErrorDetails(500, "ID null value."), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * POST /players/{id}/games/ : un jugador/a específic realitza una tirada dels daus.🤩🤖
     ***/
    @RequestMapping(value = "/players/{id}/games/", method = RequestMethod.POST)
    @ResponseBody
    public HttpEntity<? extends Message> throwsExecuted(@PathVariable String id,
                                                        @RequestParam String idPlayer,
                                                        @RequestParam int scored,
                                                        @RequestParam int bet,
                                                        @RequestParam int leftDice,
                                                        @RequestParam int rightDice,
                                                        @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        token = token.split(" ")[1];

        Optional<PlayerModel> playerModel = playerRepository.findById(id);

        if (playerModel.isPresent()) {

            if (TokenService.checkAuthorized(token, idPlayer)) {

                ScoreModel newScore = new ScoreModel(idPlayer, scored, bet, leftDice, rightDice);
                return iPlayerService.createDiceThrown(newScore);

            } else {
                return new ResponseEntity<>
                        (new ErrorDetails(401, "Unauthorized!"), HttpStatus.UNAUTHORIZED);
            }

        } else {
            return new ResponseEntity<>
                    (new ErrorDetails(500, "Id not found."), HttpStatus.NOT_FOUND);
        }

    }


    /**
     * PUT /players: modifica el nom del jugador/a.🤩🤖
     **/
    @RequestMapping(value = "/players/", method = RequestMethod.PUT)
    @ResponseBody
    public HttpEntity<? extends Message> updateNamePlayer(@RequestBody PlayerModel newNamePlayer,
                                                          @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        token = token.split(" ")[1];
        Optional<PlayerModel> playerOptional = playerRepository.findById(newNamePlayer.getId());

        if (playerOptional.isPresent()) {

            if (TokenService.checkAuthorized(token, playerOptional.get().getId())) {
                return iPlayerService.isThisNameRegistered(newNamePlayer, false);

            } else {
                return new ResponseEntity<>
                        (new ErrorDetails(401, "Unauthorized!"), HttpStatus.UNAUTHORIZED);
            }

        } else {
            return new ResponseEntity<>
                    (new ErrorDetails(500, "Id: " + newNamePlayer.getId() + " not found."), HttpStatus.NOT_FOUND);
        }

    }

    /**
     * DELETE /players elimina el jugador🤩🤖
     **/
    @RequestMapping(value = "/players/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public HttpEntity<? extends Message> deletePlayer(@RequestHeader(HttpHeaders.AUTHORIZATION) String token,
                                                      @PathVariable String id)
    {   String tokenReceived = token.split(" ")[1];
        Optional<PlayerModel> playerFound = playerRepository.findById(id);

        if (playerFound.isPresent()) {

            if (TokenService.checkAuthorized(tokenReceived, playerFound.get().getId())) {

                //PlayerModel playerFounded = new PlayerModel();
                return iPlayerService.deletePlayer(tokenReceived, id, playerFound.get());

            } else {
                return new ResponseEntity<>
                        (new ErrorDetails(HttpStatus.UNAUTHORIZED.value(), "Unauthorized!"), HttpStatus.UNAUTHORIZED);
            }

        } else { //EntityNotFoundException
            return new ResponseEntity<>
                    (new ErrorDetails(HttpStatus.NOT_FOUND.value(), id +" didn't found."), HttpStatus.NOT_FOUND);
        }

    }


    /**
     * DELETE /players/{id}/games  elimina les tirades del jugador/a.🤩🤖
     **/
    @RequestMapping(value = "/players/{id}/games/", method = RequestMethod.DELETE)
    @ResponseBody
    public HttpEntity<? extends Message> deleteScoreFromIdPlayer(@PathVariable String id,
                                                                 @RequestHeader(value = "Authorization") String token) {

        String tokenReceived = token;
        if (token.contains("Bearer"))
            tokenReceived = token.split(" ")[1];

        PlayerModel playerFound = playerRepository.findById(id).orElse(null);

        if (playerFound != null) {

            if (TokenService.checkAuthorized(tokenReceived, playerFound.getId())) {
                return iPlayerService.deleteScoreListById(playerFound);

            } else {
                return new ResponseEntity<>
                        (new ErrorDetails(401, "Unauthorized!"), HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<>
                    (new ErrorDetails(500, "Id: " + " not found."), HttpStatus.NOT_FOUND);
        }
    }


    /**
     * GET /players/ranking: retorna el ranking mig de tots els jugadors/es. Percentatge mitjà d’èxits.🤩
     **/
    @GetMapping("/players/ranking")
    public HttpEntity<? extends Message> rankingAverage(@RequestParam String idPlayer,
                                                        @RequestHeader(HttpHeaders.AUTHORIZATION) String token)
    {   token = token.split(" ")[1];

        if (idPlayer != null) {
            if (TokenService.checkAuthorized(token, idPlayer)) {
                return iPlayerService.getRanking();

            } else {
                return new ResponseEntity<>
                        (new ErrorDetails(401, "Unauthorized!"), HttpStatus.UNAUTHORIZED);
            }

        } else {
            return new ResponseEntity<>
                    (new ErrorDetails(500, "IdPlayer is null."), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    /**
     * GET /players/ranking/winner: retorna el jugador amb millor percentatge d’èxit.🤩
     */
    @GetMapping("/players/ranking/winner")
    public HttpEntity<? extends Message> rankingWinner(@RequestParam String idPlayer,
                                                       @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        token = token.split(" ")[1];

        if (idPlayer != null) {
            if (TokenService.checkAuthorized(token, idPlayer)) {
                return iPlayerService.getWinner();

            } else {
                return new ResponseEntity<>
                        (new ErrorDetails(401, "Unauthorized!"), HttpStatus.UNAUTHORIZED);
            }

        } else return new ResponseEntity<>
                (new ErrorDetails(404, "No elements"), HttpStatus.NOT_FOUND);

    }


    /**
     * GET /players/ranking/winner: retorna el jugador amb pitjor percentatge d’èxit. 🤩
     */
    @GetMapping("/players/ranking/loser")
    public HttpEntity<? extends Message> rankingLoser(@RequestParam String idPlayer,
                                                      @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        token = token.split(" ")[1];
        if (TokenService.checkAuthorized(token, idPlayer)) {
            return iPlayerService.getLoser();

        } else {
            return new ResponseEntity<>
                    (new ErrorDetails(401, "Unauthorized!"), HttpStatus.UNAUTHORIZED);
        }
    }


    ///////////////////////  END OF REQUIREMENTS FROM EXERCISE  ////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////  NEW FEATURES ////////////////////////////////////////////////////////

    /**
     * GET update data player from DB when dices have been thrown 🤩
     */
    @GetMapping("/player-update/{idPlayer}")
    @ResponseBody
    public HttpEntity<? extends Message> updateDataPlayer(@PathVariable String idPlayer,
                                                          @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        if (idPlayer != null) {
            token = token.split(" ")[1];

            if (TokenService.checkAuthorized(token, idPlayer)) {

                Optional<PlayerModel> playerModel = playerRepository.findById(idPlayer);
                if (playerModel.isPresent())
                    return new ResponseEntity<>
                            (iPlayerService.getPlayerData(idPlayer, token), HttpStatus.OK);

                else return new ResponseEntity<>
                        (new ErrorDetails(HttpStatus.NOT_FOUND.value(), "Id not found."), HttpStatus.NOT_FOUND);

            } else
                return new ResponseEntity<>
                        (new ErrorDetails(HttpStatus.UNAUTHORIZED.value(), "Unauthorized!"), HttpStatus.UNAUTHORIZED);

        } else return new ResponseEntity<>
                (new ErrorDetails(HttpStatus.NOT_FOUND.value(), "No elements"), HttpStatus.NOT_FOUND);

    }


    /**
     * GET boolean to know if the player is into DB
     **/
    @GetMapping("/players/{namePlayer}")
    public boolean isInListPlayers(@PathVariable String namePlayer) {
        return playerRepository.findByNamePlayerIgnoreCase(namePlayer) != null;
    }


/** GET -> get all data player info to send to frontend
 *
 *
 * **/
    @GetMapping("/players/getAll")
    public HttpEntity<? extends Message> getAllPlayers(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {

        token = token.split(" ")[1];
        if (TokenService.checkAuthorized(token, "-1")) {

            List<PlayerDTO> playerListDTO = iPlayerService.getPlayerDTOList();

            return new ResponseEntity<>
                    (new PlayerDataInfo( playerListDTO, "200"), HttpStatus.OK);
        } else
            return new ResponseEntity<>
                    (new ErrorDetails(401, "Unauthorized!"), HttpStatus.UNAUTHORIZED);
    }



}

