package com.tasca_s5_02_n1.joc_de_daus.model.exception;

import com.tasca_s5_02_n1.joc_de_daus.model.domain.ScoreModel;
import com.tasca_s5_02_n1.joc_de_daus.model.exception.Message;
import org.springframework.util.MultiValueMap;

import java.util.List;

public class GamesList extends Message{

    private List<ScoreModel> scoreModels;

    public GamesList(List<ScoreModel> allScoresFromIdUser,String message) {
        super(message + " Resultados. 200 Ok!");
        this.scoreModels = allScoresFromIdUser;

    }

    public List<ScoreModel> getScoreModels() {
        return scoreModels;
    }
}
