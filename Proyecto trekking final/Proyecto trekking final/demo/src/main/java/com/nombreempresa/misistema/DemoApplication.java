package com.nombreempresa.misistema;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication
// Quitamos el parametro 'enabled' que da error.
// El Repositorio será ignorado de todas formas ya que el controlador usa Neo4jClient.
@EnableNeo4jRepositories(basePackages = "com.nombreempresa.misistema.repo")
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}