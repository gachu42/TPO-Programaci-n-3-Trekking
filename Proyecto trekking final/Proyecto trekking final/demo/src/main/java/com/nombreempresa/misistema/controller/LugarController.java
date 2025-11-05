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

        // Mapeo manual de RAW a List<LugarEntity>
        Map<Long, LugarEntity> entityMap = new HashMap<>();

        for (Map<String, Object> record : rawResult) {

            // 1. Obtener el nodo de origen (n) y sus propiedades
            Node internalNode = (Node) record.get("n");
            Map<String, Object> nodeProperties = internalNode.asMap();

            // Usamos el ID interno de Neo4j, ya que estamos usando Neo4jClient
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

            // 3. Procesar las conexiones
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> conexionesRaw = (List<Map<String, Object>>) record.get("conexiones_raw");

            for (Map<String, Object> rawConexion : conexionesRaw) {
                if (rawConexion.get("rel") != null && rawConexion.get("target") != null) {

                    // Acceder a las propiedades de la relación y del destino.
                    org.neo4j.driver.types.Relationship internalRel = (org.neo4j.driver.types.Relationship) rawConexion.get("rel");
                    Node internalTarget = (Node) rawConexion.get("target");

                    Map<String, Object> relProperties = internalRel.asMap();
                    Map<String, Object> targetProperties = internalTarget.asMap();

                    // Crear la entidad de destino mínima
                    LugarEntity destino = new LugarEntity();
                    destino.setNombre((String) targetProperties.get("name"));

                    // Recuperar distancia (se asume que es un Long o Integer en la base de datos)
                    Long distanciaLong = (Long) relProperties.get("distancia");
                    int distancia = (distanciaLong != null) ? distanciaLong.intValue() : 0;

                    // Crear la conexión Java
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