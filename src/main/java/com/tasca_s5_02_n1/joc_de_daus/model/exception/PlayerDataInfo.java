package com.tasca_s5_02_n1.joc_de_daus.model.exception;

import com.tasca_s5_02_n1.joc_de_daus.model.dto.PlayerDTO;

import java.util.List;

public class PlayerDataInfo extends Message{

    private List<PlayerDTO> playerDTOList;

    public PlayerDataInfo(List<PlayerDTO> allData, String message) {
        super(message);
        this.playerDTOList = allData;

    }

    public List<PlayerDTO> getPlayerDTOList() {
        return playerDTOList;
    }
}
