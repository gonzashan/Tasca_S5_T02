package com.tasca_s5_02_n1.joc_de_daus.model.repository;

import com.tasca_s5_02_n1.joc_de_daus.model.domain.ScoreModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ScoreRepository extends JpaRepository<ScoreModel,String> {

    @Query("SELECT u FROM score u WHERE u.idPlayer = ?1")
    List<ScoreModel> findByIdPlayer(String idPlayer);


}
