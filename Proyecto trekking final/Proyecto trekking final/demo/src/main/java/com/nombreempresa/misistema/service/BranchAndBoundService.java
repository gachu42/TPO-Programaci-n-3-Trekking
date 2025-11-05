package com.nombreempresa.misistema.service;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BranchAndBoundService {

    private static class Edge {
        int target;
        int weight;
        public Edge(int target, int weight) { this.target = target; this.weight = weight; }
    }

    private static class State implements Comparable<State> {
        List<Integer> path;
        int cost;
        int lastNode;

        public State(List<Integer> path, int cost, int lastNode) {
            this.path = path;
            this.cost = cost;
            this.lastNode = lastNode;
        }

        @Override
        public int compareTo(State other) {
            return Integer.compare(this.cost, other.cost);
        }
    }

    public List<String> ejecutarBranchAndBound(int numVertices, List<int[]> edges, int start, int end,
                                               List<Integer> nodosObligatorios, List<Integer> nodosEvitados) {

        List<List<Edge>> adjList = new ArrayList<>();
        for (int i = 0; i < numVertices; i++) {
            adjList.add(new ArrayList<>());
        }
        for (int[] e : edges) {
            adjList.get(e[0]).add(new Edge(e[1], e[2]));
            adjList.get(e[1]).add(new Edge(e[0], e[2])); // Grafo no dirigido
        }

        // Usamos una cola de prioridad para explorar el mejor estado (menor costo) primero
        PriorityQueue<State> pq = new PriorityQueue<>();


        List<Integer> bestPath = null;
        int bestCost = Integer.MAX_VALUE;

        // Inicialización
        pq.add(new State(List.of(start), 0, start));

        while (!pq.isEmpty()) {
            State current = pq.poll();
            int u = current.lastNode;

            // 1. Poda (Branch)
            if (current.cost >= bestCost) {
                continue;
            }
            if (nodosEvitados != null && nodosEvitados.contains(u) && u != start) {
                continue;
            }

            // 2. Condición de Éxito
            if (u == end) {
                // Verificamos si todos los nodos obligatorios están en el camino
                boolean cumpleObligatorios = true;
                if (nodosObligatorios != null && !nodosObligatorios.isEmpty()) {
                    for (Integer nodo : nodosObligatorios) {
                        if (!current.path.contains(nodo)) {
                            cumpleObligatorios = false;
                            break;
                        }
                    }
                }

                if (cumpleObligatorios) {
                    // Actualizamos el mejor 'Bound'
                    bestCost = current.cost;
                    bestPath = current.path;
                }
                continue; // No exploramos más allá del destino
            }

            // 3. Exploración (Branch)
            for (Edge edge : adjList.get(u)) {
                int v = edge.target;
                int newCost = current.cost + edge.weight;

                // Evitar ciclos en el camino actual (ya visitado en la ruta actual)
                if (current.path.contains(v)) {
                    continue;
                }

                if (newCost < bestCost) {
                    List<Integer> newPath = new ArrayList<>(current.path);
                    newPath.add(v);
                    pq.add(new State(newPath, newCost, v));
                }
            }
        }


        List<String> resultado = new ArrayList<>();
        if (bestPath != null) {
            String pathStr = bestPath.stream().map(String::valueOf).collect(Collectors.joining(" → "));
            resultado.add("RUTA MÁS CORTA (IDs) (Branch & Bound): " + pathStr);
            resultado.add("Distancia total: #VAL#" + bestCost + " km");
        } else {
            resultado.add("No se encontró una ruta que cumpla las restricciones.");
        }
        return resultado;
    }
}
