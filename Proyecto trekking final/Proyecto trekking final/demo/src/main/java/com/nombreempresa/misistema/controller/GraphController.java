package com.nombreempresa.misistema.controller;

import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class GraphController {

    private final Neo4jClient neo4jClient;

    public GraphController(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    @GetMapping("/graph")
    public List<Map<String, Object>> getGraph() {
        var result = neo4jClient.query("MATCH (n)-[r]->(m) RETURN n, r, m").fetch().all();
        return List.copyOf(result); // convierte Collection a List
    }
}
