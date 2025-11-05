package com.nombreempresa.misistema.repo;

import com.nombreempresa.misistema.model.LugarEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LugarRepository extends Neo4jRepository<LugarEntity, Long> {

    @Query("MATCH (n:LUGAR) RETURN n")
    @Override
    List<LugarEntity> findAll();
}