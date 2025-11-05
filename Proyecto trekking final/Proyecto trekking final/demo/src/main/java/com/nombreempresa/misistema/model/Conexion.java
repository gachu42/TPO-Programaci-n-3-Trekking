package com.nombreempresa.misistema.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
public class Conexion {

    @Id
    @GeneratedValue
    private Long id;

    private int distancia;

    @TargetNode
    private LugarEntity destino;

    public Conexion() {}

    public Conexion(LugarEntity destino, int distancia) {
        this.destino = destino;
        this.distancia = distancia;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getDistancia() { return distancia; }
    public void setDistancia(int distancia) { this.distancia = distancia; }

    public LugarEntity getDestino() { return destino; }
    // Asegúrate de que el setter no tenga problemas de recursión
    public void setDestino(LugarEntity destino) { this.destino = destino; }
}