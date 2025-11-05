package com.nombreempresa.misistema.controller;

import com.nombreempresa.misistema.model.Conexion;
import com.nombreempresa.misistema.model.LugarEntity;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.web.bind.annotation.*;
import org.neo4j.driver.types.Node; // Importar la clase nativa Node

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/lugares")
@CrossOrigin(origins = "*")
public class LugarController {

    private final Neo4jClient neo4jClient;

    public LugarController(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    @GetMapping
    public List<LugarEntity> getAllLugares() {

        // La consulta trae el nodo principal (n), sus relaciones (r), y los nodos de destino (m)
        var rawResult = neo4jClient.query("""
            MATCH (n:LUGAR) 
            OPTIONAL MATCH (n)-[r:CAMINO]->(m:LUGAR)
            RETURN n, collect({rel: r, target: m}) as conexiones_raw
            """)
                .fetch()
                .all();


        Map<Long, LugarEntity> entityMap = new HashMap<>();

        for (Map<String, Object> record : rawResult) {


            Node internalNode = (Node) record.get("n");
            Map<String, Object> nodeProperties = internalNode.asMap();


            Long id = internalNode.id();

            if (entityMap.containsKey(id)) {
                continue;
            }

            // 2. Crear la entidad principal
            LugarEntity lugar = new LugarEntity();
            lugar.setId(id);
            lugar.setNombre((String) nodeProperties.get("name"));
            lugar.setConexiones(new ArrayList<>());
            entityMap.put(id, lugar);


            @SuppressWarnings("unchecked")
            List<Map<String, Object>> conexionesRaw = (List<Map<String, Object>>) record.get("conexiones_raw");

            for (Map<String, Object> rawConexion : conexionesRaw) {
                if (rawConexion.get("rel") != null && rawConexion.get("target") != null) {


                    org.neo4j.driver.types.Relationship internalRel = (org.neo4j.driver.types.Relationship) rawConexion.get("rel");
                    Node internalTarget = (Node) rawConexion.get("target");

                    Map<String, Object> relProperties = internalRel.asMap();
                    Map<String, Object> targetProperties = internalTarget.asMap();


                    LugarEntity destino = new LugarEntity();
                    destino.setNombre((String) targetProperties.get("name"));

                    Long distanciaLong = (Long) relProperties.get("distancia");
                    int distancia = (distanciaLong != null) ? distanciaLong.intValue() : 0;


                    Conexion conexion = new Conexion(destino, distancia);
                    lugar.getConexiones().add(conexion);
                }
            }
        }

        System.out.println("---- DEBUG /lugares (Final) ----");
        System.out.println("Cantidad de lugares: " + entityMap.size());

        return new ArrayList<>(entityMap.values());
    }
}
