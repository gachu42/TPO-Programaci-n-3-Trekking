package com.nombreempresa.misistema.service;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BFSService {

    public List<String> ejecutarBFS(int numVertices, List<int[]> edges, int startVertex, int endVertex) {
        List<List<Integer>> adjList = new ArrayList<>();
        for (int i = 0; i < numVertices; i++) {
            adjList.add(new ArrayList<>());
        }

        for (int[] e : edges) {
            int source = e[0];
            int target = e[1];
            adjList.get(source).add(target);
            adjList.get(target).add(source);
        }

        int[] predecesor = new int[numVertices];
        Arrays.fill(predecesor, -1);

        int[] distanciaTramos = new int[numVertices];
        Arrays.fill(distanciaTramos, -1);

        Queue<Integer> cola = new LinkedList<>();

        cola.add(startVertex);
        distanciaTramos[startVertex] = 0;

        while (!cola.isEmpty()) {
            int verticeActual = cola.poll();

            if (verticeActual == endVertex) {
                return construirResultado(distanciaTramos, predecesor, startVertex, endVertex);
            }

            for (int vecino : adjList.get(verticeActual)) {
                if (distanciaTramos[vecino] == -1) {
                    distanciaTramos[vecino] = distanciaTramos[verticeActual] + 1;
                    predecesor[vecino] = verticeActual;
                    cola.add(vecino);
                }
            }
        }

        return List.of("No hay camino disponible desde " + startVertex + " hasta " + endVertex + ".");
    }

    private List<String> construirResultado(int[] distanciaTramos, int[] predecesor, int startVertex, int endVertex) {
        List<String> resultado = new ArrayList<>();

        LinkedList<Integer> path = new LinkedList<>();
        int current = endVertex;
        while (current != -1) {
            path.addFirst(current);
            if (current == startVertex) break;
            current = predecesor[current];
        }

        if (path.isEmpty() || path.get(0) != startVertex) {
            resultado.add("No hay camino disponible hasta el destino.");
            return resultado;
        }

        String pathStr = path.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(" → "));

        int tramos = distanciaTramos[endVertex];

        resultado.add("RUTA MÁS CORTA EN TRAMOS (IDs): " + pathStr);

        resultado.add("Cantidad de tramos: #VAL#" + tramos);

        return resultado;
    }
}