package com.nombreempresa.misistema.service;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BacktrackingService {

    public List<String> encontrarCaminos(int numVertices, List<int[]> edges, int inicio, int destino,
                                         List<Integer> nodosObligatorios, List<Integer> nodosEvitados) {

        List<List<Integer>> adjList = new ArrayList<>();
        for (int i = 0; i < numVertices; i++) {
            adjList.add(new ArrayList<>());
        }
        for (int[] e : edges) {
            adjList.get(e[0]).add(e[1]);
            adjList.get(e[1]).add(e[0]);
        }

        boolean[] visitado = new boolean[numVertices];
        List<Integer> caminoActual = new ArrayList<>();
        List<String> caminosEncontrados = new ArrayList<>();

        backtrack(inicio, destino, adjList, visitado, caminoActual, caminosEncontrados,
                nodosObligatorios, nodosEvitados);

        if (caminosEncontrados.isEmpty()) {
            return List.of("No se encontraron caminos que cumplan con todas las condiciones.");
        }


        caminosEncontrados.add(0, "Se encontraron #VAL#" + caminosEncontrados.size() + " caminos posibles:");

        return caminosEncontrados;
    }

    private void backtrack(int actual, int destino, List<List<Integer>> adjList,
                           boolean[] visitado, List<Integer> caminoActual, List<String> caminos,
                           List<Integer> nodosObligatorios, List<Integer> nodosEvitados) {

        // Poda por nodos evitados (no se puede visitar un nodo evitado, excepto el inicio si es el mismo)
        if (nodosEvitados != null && nodosEvitados.contains(actual) && !caminoActual.isEmpty()) {
            return;
        }

        visitado[actual] = true;
        caminoActual.add(actual);

        // Condición de éxito
        if (actual == destino) {
            // Verificamos si todos los nodos obligatorios están en el camino
            boolean cumpleObligatorios = true;
            if (nodosObligatorios != null && !nodosObligatorios.isEmpty()) {
                for (Integer nodo : nodosObligatorios) {
                    if (!caminoActual.contains(nodo)) {
                        cumpleObligatorios = false;
                        break;
                    }
                }
            }

            if (cumpleObligatorios) {
                // El formato de salida debe ser consistente para que el frontend lo parsee
                String pathStr = caminoActual.stream().map(String::valueOf).collect(Collectors.joining(" → "));
                caminos.add("Camino (IDs): " + pathStr);
            }
        } else {
            // Exploración recursiva
            for (int vecino : adjList.get(actual)) {
                if (!visitado[vecino]) {
                    backtrack(vecino, destino, adjList, visitado, caminoActual, caminos,
                            nodosObligatorios, nodosEvitados);
                }
            }
        }


        visitado[actual] = false;
        caminoActual.remove(caminoActual.size() - 1);
    }
}