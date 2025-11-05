package com.nombreempresa.misistema.service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class PrimService {
    private static final int INF = Integer.MAX_VALUE;

    public List<String> primFromEdges(int numVertices, List<int[]> edges, int startVertex) {
        if (startVertex < 0 || startVertex >= numVertices) {
            return Collections.singletonList("ERROR: El nodo inicial (" + startVertex + ") está fuera de rango.");
        }

        List<List<int[]>> graph = buildGraph(numVertices, edges);
        return primMST(numVertices, graph, startVertex);
    }

    private List<List<int[]>> buildGraph(int numVertices, List<int[]> edges) {
        List<List<int[]>> graph = new ArrayList<>(numVertices);
        for (int i = 0; i < numVertices; i++) graph.add(new ArrayList<>());
        for (int[] edge : edges) {
            graph.get(edge[0]).add(new int[]{edge[1], edge[2]});
            graph.get(edge[1]).add(new int[]{edge[0], edge[2]});
        }
        return graph;
    }

    public List<String> primMST(int numVertices, List<List<int[]>> graph, int startVertex) {
        int[] parent = new int[numVertices];
        int[] key = new int[numVertices];
        boolean[] inMST = new boolean[numVertices];

        Arrays.fill(key, INF);
        Arrays.fill(parent, -1);
        key[startVertex] = 0;

        for (int count = 0; count < numVertices - 1; count++) {
            int u = minKey(numVertices, key, inMST);
            if (u == -1) break;
            inMST[u] = true;

            for (int[] neighbor : graph.get(u)) {
                int v = neighbor[0];
                int weight = neighbor[1];
                if (!inMST[v] && weight < key[v]) {
                    parent[v] = u;
                    key[v] = weight;
                }
            }
        }

        return buildMSTResult(parent, key, numVertices);
    }

    private int minKey(int numVertices, int[] key, boolean[] inMST) {
        int min = INF, minIndex = -1;
        for (int v = 0; v < numVertices; v++) {
            if (!inMST[v] && key[v] < min) {
                min = key[v];
                minIndex = v;
            }
        }
        return minIndex;
    }

    private List<String> buildMSTResult(int[] parent, int[] key, int numVertices) {
        List<String> resultado = new ArrayList<>();
        int totalWeight = 0;

        for (int i = 0; i < numVertices; i++) {
            if (parent[i] != -1) {
                int u = parent[i];
                int v = i;
                int weight = key[i];

                int id1 = Math.min(u, v);
                int id2 = Math.max(u, v);

                resultado.add(id1 + "--" + id2 + "--#VAL#" + weight);
                totalWeight += weight;
            }
        }

        resultado.add(0, "Peso Total del MST (Prim): #VAL#" + totalWeight + " km");
        return resultado;
    }
}