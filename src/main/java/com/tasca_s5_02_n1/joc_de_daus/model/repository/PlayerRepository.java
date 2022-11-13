package com.tasca_s5_02_n1.joc_de_daus.model.repository;

import com.tasca_s5_02_n1.joc_de_daus.model.domain.PlayerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface    PlayerRepository extends CrudRepository<PlayerModel,String> {
//public interface PlayerRepository extends MongoRepository<PlayerModel,String> {
    //@Query("SELECT u FROM player u WHERE lower(u.namePlayer) = ?1")
    PlayerModel findByNamePlayerIgnoreCase(String playerName);
    Boolean existsByNamePlayerIgnoreCase(String playerName);

    List<PlayerModel> findAllByLogged(boolean logValue);


}
