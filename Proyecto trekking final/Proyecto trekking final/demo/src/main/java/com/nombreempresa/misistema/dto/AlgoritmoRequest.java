// AlgoritmoRequest.java

package com.nombreempresa.misistema.dto;

import java.util.List;

public class AlgoritmoRequest {

    // PARÁMETROS BÁSICOS DEL GRAFO
    private int numVertices;
    private List<int[]> edges; // Lista de aristas: [origen, destino, peso]
    private int startVertex;
    private int endVertex;

    // PARÁMETROS ESPECÍFICOS DE BACKTRACKING
    private List<Integer> nodosObligatorios;
    private List<Integer> nodosEvitados;

    // --- PARÁMETRO PARA GREEDY ---
    private int limiteCosto;

    // PARÁMETRO PARA PROGRAMACIÓN DINÁMICA
    private int limiteDistancia;

    //Getters y setters
    public int getNumVertices() { return numVertices; }
    public void setNumVertices(int numVertices) { this.numVertices = numVertices; }

    public List<int[]> getEdges() { return edges; }
    public void setEdges(List<int[]> edges) { this.edges = edges; }

    public int getStartVertex() { return startVertex; }
    public void setStartVertex(int startVertex) { this.startVertex = startVertex; }

    public int getEndVertex() { return endVertex; }
    public void setEndVertex(int endVertex) { this.endVertex = endVertex; }

    public List<Integer> getNodosObligatorios() { return nodosObligatorios; }
    public void setNodosObligatorios(List<Integer> nodosObligatorios) { this.nodosObligatorios = nodosObligatorios; }

    public List<Integer> getNodosEvitados() { return nodosEvitados; }
    public void setNodosEvitados(List<Integer> nodosEvitados) { this.nodosEvitados = nodosEvitados; }

    public int getLimiteCosto() { return limiteCosto; }
    public void setLimiteCosto(int limiteCosto) { this.limiteCosto = limiteCosto; }

    public int getLimiteDistancia() { return limiteDistancia; }
    public void setLimiteDistancia(int limiteDistancia) { this.limiteDistancia = limiteDistancia; }
}
