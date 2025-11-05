package com.nombreempresa.misistema.service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class DFSService {

    public List<String> ejecutarDFS(int numVertices, List<int[]> edges, int startVertex, int endVertex) {
        Map<Integer, List<int[]>> graph = new HashMap<>();
        for (int i = 0; i < numVertices; i++) {
            graph.put(i, new ArrayList<>());
        }

        for (int[] edge : edges) {
            int from = edge[0];
            int to = edge[1];
            int weight = edge[2];
            graph.get(from).add(new int[]{to, weight});
            graph.get(to).add(new int[]{from, weight});
        }


        boolean[] visited = new boolean[numVertices];
        List<Integer> path = new ArrayList<>();


        dfsRecursive(graph, startVertex, endVertex, visited, path);


        List<String> result = new ArrayList<>();
        if (path.isEmpty()) {
            result.add("No se encontró un camino desde " + startVertex + " hasta " + endVertex);
        } else {
            // Línea 1: Recorrido DFS
            StringBuilder recorrido = new StringBuilder("Recorrido DFS: ");
            for (int i = 0; i < path.size(); i++) {
                recorrido.append(path.get(i));
                if (i < path.size() - 1) recorrido.append(" → ");
            }
            result.add(recorrido.toString());

            // Línea 2: Cantidad de tramos
            int tramos = path.size() > 1 ? path.size() - 1 : 0;
            result.add("Cantidad de tramos: " + tramos);
        }

        return result;
    }

    private boolean dfsRecursive(Map<Integer, List<int[]>> graph, int current, int end,
                                 boolean[] visited, List<Integer> path) {

        visited[current] = true;
        path.add(current);

        if (current == end) {
            return true; // Llegamos al destino
        }

        for (int[] neighbor : graph.get(current)) {
            int next = neighbor[0];
            if (!visited[next]) {
                if (dfsRecursive(graph, next, end, visited, path)) {
                    return true;
                }
            }
        }

        // Backtracking si no hay camino desde este nodo
        path.remove(path.size() - 1);
        return false;
    }
}
