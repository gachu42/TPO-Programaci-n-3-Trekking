package com.nombreempresa.misistema.model;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.GeneratedValue; // <-- NECESARIO
import java.util.List;
import java.util.Map;

@Node("LUGAR")
public class LugarEntity {

    @Id
    @GeneratedValue // Permite que SDN mapee IDs auto-generados o internos
    private Long id;

    private String tipo;
    private Map<String, Object> propiedades;

    @Property("name")
    private String nombre;

    @Relationship(type = "CAMINO", direction = Relationship.Direction.OUTGOING)
    private List<Conexion> conexiones;


    public LugarEntity() {}
    public LugarEntity(Long id, String tipo, Map<String, Object> propiedades, String nombre) {
        this.id = id;
        this.tipo = tipo;
        this.propiedades = propiedades;
        this.nombre = nombre;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Map<String, Object> getPropiedades() { return propiedades; }
    public void setPropiedades(Map<String, Object> propiedades) { this.propiedades = propiedades; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public List<Conexion> getConexiones() { return conexiones; }
    public void setConexiones(List<Conexion> conexiones) { this.conexiones = conexiones; }
}