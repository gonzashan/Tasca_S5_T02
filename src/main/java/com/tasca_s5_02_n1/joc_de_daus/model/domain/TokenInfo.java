package com.tasca_s5_02_n1.joc_de_daus.model.domain;

public class TokenInfo {

    private String idPlayer;
    private Long issuedAt;
    private Long expiresAt;

    public TokenInfo(String idPlayer, Long issuedAt, Long expiresAt) {
        this.idPlayer = idPlayer;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
    }

    public String getIdPlayer() {
        return idPlayer;
    }

    public void setIdPlayer(String idPlayer) {
        this.idPlayer = idPlayer;
    }

    public Long getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Long issuedAt) {
        this.issuedAt = issuedAt;
    }

    public Long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Long expiresAt) {
        this.expiresAt = expiresAt;
    }
}
