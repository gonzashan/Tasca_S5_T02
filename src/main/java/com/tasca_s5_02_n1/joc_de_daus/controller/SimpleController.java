package com.tasca_s5_02_n1.joc_de_daus.controller;

import com.nimbusds.jose.JOSEException;
import com.tasca_s5_02_n1.joc_de_daus.model.dto.PlayerDTO;
import com.tasca_s5_02_n1.joc_de_daus.model.service.IPlayerService;
import com.tasca_s5_02_n1.joc_de_daus.model.service.TokenService;
import com.tasca_s5_02_n1.joc_de_daus.utils.Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/jocdedaus")
public class SimpleController {
    @Value("${spring.application.name}")
    String appName;

    @Autowired
    private IPlayerService iPlayerService;


    /**
     * GET -> To index/home/
     *
     * @param model
     * @return home.html
     */
    @GetMapping("/home")
    public String homePage(@PathVariable Long id, Model model) {
        model.addAttribute("appName", appName);
        return "home";
    }


    /**
     * GET -> FORM FOR NEW PLAYER
     *
     * @param model
     * @return signup.html
     */
    @GetMapping("/signup")
    public String signUpForm(Model model) {
        model.addAttribute("playerModel", new PlayerDTO());
        return "signup";
    }


    /**
     * POST  -> REGISTER NEW PLAYER
     *
     * @param playerDTO
     * @return "redirect:/jocdedaus/dashboard/{idPlayer}
     */
    @PostMapping("/signup")
    public String signUpNewPlayer(@ModelAttribute("playerModel") PlayerDTO playerDTO) {
        String token = "";
        String idPlayer = iPlayerService.addingNewPlayer(playerDTO);

        System.out.println(idPlayer);
        try {
            token = TokenService.generateToken(idPlayer);
            System.out.println(token);
            playerDTO.setToken(token);

        } catch (JOSEException e) {

            e.printStackTrace();
        }
        return "redirect:/jocdedaus/dashboard/" + idPlayer + "?token=" + token;
    }


    /**
     * GET  -> LOGIN FORM
     *
     * @param model
     * @return login.html
     */
    @GetMapping("/login")
    public String logInPlayer(Model model) {
        model.addAttribute("playerModel", new PlayerDTO());
        return "login";
    }


    /**
     * POST -> LOGIN PLAYER REGISTRED 🤩
     *
     * @param playerDTO
     * @return redirect:/jocdedaus/dashboard/{idPlayer}
     */
    @PostMapping("/login")
    public String logInOk(@ModelAttribute("playerModel") PlayerDTO playerDTO) {

        String idPlayer = iPlayerService.loggedOk(playerDTO, true);
        String token = "";

        try {
            token = TokenService.generateToken(idPlayer);
            System.out.println("1º token: " + token);
            playerDTO.setToken(token);

        } catch (JOSEException e) {
            e.printStackTrace();
        }
        return "redirect:/jocdedaus/dashboard/" + iPlayerService.loggedOk(playerDTO, true) + "?token=" + token;
    }


    /**
     * Function for login into the test
     *
     * @return HttpStatus.OK
     */
    @PostMapping("/loginTest")
    public ResponseEntity<PlayerDTO> logInTest(@RequestBody PlayerDTO playerDTO) {

        System.out.println("login for: " + playerDTO.getNamePlayer());
        String idPlayer = iPlayerService.getIdByNamePlayer(playerDTO.getNamePlayer());
        String token;
        try {
            token = TokenService.generateToken(idPlayer);
            playerDTO.setToken(token);

        } catch (JOSEException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(playerDTO, HttpStatus.OK);
    }


    /**
     * GET  -> Statistic view
     *
     * @return statistics.html
     */
    @GetMapping("/statistics/{idPlayer}")
    public String getTablePage(@PathVariable String idPlayer, @RequestParam String token, Model model) {

        model.addAttribute("playerData", iPlayerService.getPlayerData(idPlayer, token));
        return "statistics";
    }


    /**
     * GET    -> DASHBOARD 🤩
     *
     * @param idPlayer
     * @return dashboard.html
     */
    @GetMapping("/dashboard/{idPlayer}")
    public String signupPlayer(@PathVariable String idPlayer, @RequestParam String token, Model model) {

        System.out.println("2º token: " + token);
        model.addAttribute("playerData", iPlayerService.getPlayerData(idPlayer, token));
        return "dashboard";
    }


    /**
     * POST -> LOGOUT PLAYER
     *
     * @param playerDTO
     * @return {idPlayer}
     */
    @GetMapping("/logout")
    public String logOut(@ModelAttribute PlayerDTO playerDTO) {
        System.out.println("saliendo... " + playerDTO.getNamePlayer());
        iPlayerService.loggedOk(playerDTO, false);
        return "redirect:/jocdedaus/login";

    }


}