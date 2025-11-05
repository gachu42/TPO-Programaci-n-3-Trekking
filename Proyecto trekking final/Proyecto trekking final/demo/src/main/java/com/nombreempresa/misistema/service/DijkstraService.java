package com.nombreempresa.misistema.service;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DijkstraService {

    public List<String> ejecutarDijkstra(int numVertices, List<int[]> edges, int startVertex, int endVertex) {
        List<List<Edge>> adjList = new ArrayList<>();
        for (int i = 0; i < numVertices; i++) {
            adjList.add(new ArrayList<>());
        }

        // Construir grafo no dirigido
        for (int[] e : edges) {
            int source = e[0];
            int target = e[1];
            int weight = e[2];
            adjList.get(source).add(new Edge(target, weight));
            adjList.get(target).add(new Edge(source, weight));
        }

        int[] distances = new int[numVertices];
        int[] predecessors = new int[numVertices];
        Arrays.fill(distances, Integer.MAX_VALUE);
        Arrays.fill(predecessors, -1);

        distances[startVertex] = 0;

        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(e -> e.weight));
        pq.add(new Edge(startVertex, 0));

        boolean[] visited = new boolean[numVertices];

        while (!pq.isEmpty()) {
            int u = pq.poll().target;

            if (visited[u]) continue;
            visited[u] = true;

            if (u == endVertex) break;

            for (Edge edge : adjList.get(u)) {
                int v = edge.target;
                int weight = edge.weight;
                if (distances[u] != Integer.MAX_VALUE && distances[u] + weight < distances[v]) {
                    distances[v] = distances[u] + weight;
                    predecessors[v] = u;
                    pq.add(new Edge(v, distances[v]));
                }
            }
        }

        return construirResultado(distances, predecessors, startVertex, endVertex);
    }

    private List<String> construirResultado(int[] distances, int[] predecessors, int startVertex, int endVertex) {
        List<String> resultado = new ArrayList<>();

        if (distances[endVertex] == Integer.MAX_VALUE) {
            resultado.add("No hay camino disponible desde " + startVertex + " hasta " + endVertex + ".");
            return resultado;
        }

        LinkedList<Integer> path = new LinkedList<>();
        int current = endVertex;
        while (current != -1) {
            path.addFirst(current);
            if (current == startVertex && path.size() > 0) break;
            current = predecessors[current];
        }

        String pathStr = path.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(" → "));

        resultado.add("RUTA MÁS CORTA (IDs): " + pathStr);


        resultado.add("Distancia total: #VAL#" + distances[endVertex] + " km");

        return resultado;
    }

    private static class Edge implements Comparable<Edge> {
        int target;
        int weight;

        public Edge(int target, int weight) {
            this.target = target;
            this.weight = weight;
        }

        @Override
        public int compareTo(Edge other) {
            return Integer.compare(this.weight, other.weight);
        }
    }
}