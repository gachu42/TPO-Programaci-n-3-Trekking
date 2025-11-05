// GreedyService.java (Final)
package com.nombreempresa.misistema.service;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GreedyService {


    public List<String> ejecutarGreedy(int numVertices, List<int[]> edges, int startVertex, int limiteCosto) {
        // Matriz de adyacencia para facilitar la búsqueda de distancias
        int[][] grafo = new int[numVertices][numVertices];
        for (int[] e : edges) {
            grafo[e[0]][e[1]] = e[2];
            grafo[e[1]][e[0]] = e[2];
        }

        boolean[] visitado = new boolean[numVertices];
        List<Integer> camino = new ArrayList<>();
        int distanciaTotal = 0;
        int nodoActual = startVertex;

        camino.add(nodoActual);
        visitado[nodoActual] = true;

        while (true) {
            int proximoNodo = -1;
            int menorDistancia = Integer.MAX_VALUE;

            // 1. Estrategia Greedy: Buscar el vecino no visitado más cercano desde el NODO ACTUAL
            for (int vecino = 0; vecino < numVertices; vecino++) {
                if (!visitado[vecino] && grafo[nodoActual][vecino] > 0 && grafo[nodoActual][vecino] < menorDistancia) {
                    menorDistancia = grafo[nodoActual][vecino];
                    proximoNodo = vecino;
                }
            }

            // 2. Comprobar si se puede mover
            if (proximoNodo != -1 && (limiteCosto == 0 || distanciaTotal + menorDistancia <= limiteCosto)) {
                distanciaTotal += menorDistancia;
                nodoActual = proximoNodo;
                camino.add(nodoActual);
                visitado[nodoActual] = true;
            } else {
                break; // No hay más movimientos posibles
            }
        }

        // 3. Formatear el resultado
        List<String> resultado = new ArrayList<>();
        String pathStr = camino.stream().map(String::valueOf).collect(Collectors.joining(" → "));

        resultado.add("--- RECORRIDO CON ALGORITMO GREEDY (Vecino más cercano) ---");
        resultado.add("Camino Encontrado (IDs): " + pathStr);
        resultado.add("Lugares Visitados: #VAL#" + camino.size());
        resultado.add("Distancia Total Recorrida: #VAL#" + distanciaTotal + " km");

        return resultado;
    }
}
