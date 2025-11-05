package com.nombreempresa.misistema.service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class PDService {


    public static class ResultadoPD {
        private int nodosVisitados;
        private int distanciaTotal;
        private List<Integer> camino;

        public ResultadoPD(int nodosVisitados, int distanciaTotal, List<Integer> camino) {
            this.nodosVisitados = nodosVisitados;
            this.distanciaTotal = distanciaTotal;
            this.camino = camino;
        }

        public int getNodosVisitados() { return nodosVisitados; }
        public int getDistanciaTotal() { return distanciaTotal; }
        public List<Integer> getCamino() { return camino; }
    }

    private int[][] grafo;
    private int[][] dp;
    private int[][] predecesor;
    private int numVertices;
    private static final int INF = Integer.MAX_VALUE;

    public ResultadoPD ejecutarPD(int numVertices, List<int[]> edges, int startVertex, int limiteDistancia) {
        this.numVertices = numVertices;

        construirGrafo(numVertices, edges);

        int numMasks = 1 << numVertices;
        this.dp = new int[numMasks][numVertices];
        this.predecesor = new int[numMasks][numVertices];

        for (int[] row : dp) Arrays.fill(row, INF);
        for (int[] row : predecesor) Arrays.fill(row, -1);

        // Caso Base: Empezar en startVertex
        int startMask = 1 << startVertex;
        dp[startMask][startVertex] = 0;

        // Llenar la tabla
        for (int mask = 1; mask < numMasks; mask++) {
            for (int u = 0; u < numVertices; u++) {

                if ((mask & (1 << u)) != 0 && dp[mask][u] != INF) {
                    for (int v = 0; v < numVertices; v++) {
                        if ((mask & (1 << v)) == 0 && grafo[u][v] > 0) {

                            int nuevaMask = mask | (1 << v);
                            int nuevaDistancia = dp[mask][u] + grafo[u][v];


                            if (nuevaDistancia < dp[nuevaMask][v]) {
                                dp[nuevaMask][v] = nuevaDistancia;
                                predecesor[nuevaMask][v] = u;
                            }
                        }
                    }
                }
            }
        }

        // Encontrar la mejor solución en la tabla
        return buscarMejorResultado(limiteDistancia);
    }

    private void construirGrafo(int numVertices, List<int[]> edges) {
        this.grafo = new int[numVertices][numVertices];
        for (int[] e : edges) {
            grafo[e[0]][e[1]] = e[2];
            grafo[e[1]][e[0]] = e[2];
        }
    }

    private ResultadoPD buscarMejorResultado(int limiteDistancia) {
        int mejorCantidadNodos = 0;
        int mejorMask = -1;
        int ultimoNodo = -1;
        int mejorDistancia = INF;

        // Recorremos toda la tabla 'dp'
        for (int mask = 1; mask < (1 << numVertices); mask++) {
            for (int u = 0; u < numVertices; u++) {


                if (dp[mask][u] != INF && dp[mask][u] <= limiteDistancia) {
                    int cantidadNodos = Integer.bitCount(mask);

                    if (cantidadNodos > mejorCantidadNodos) {
                        mejorCantidadNodos = cantidadNodos;
                        mejorDistancia = dp[mask][u];
                        mejorMask = mask;
                        ultimoNodo = u;
                    }

                    else if (cantidadNodos == mejorCantidadNodos && dp[mask][u] < mejorDistancia) {
                        mejorDistancia = dp[mask][u];
                        mejorMask = mask;
                        ultimoNodo = u;
                    }
                }
            }
        }

        if (mejorMask == -1) {
            return new ResultadoPD(0, 0, new ArrayList<>());
        }

        // Reconstruir el camino óptimo
        List<Integer> camino = reconstruirCamino(mejorMask, ultimoNodo);
        return new ResultadoPD(mejorCantidadNodos, mejorDistancia, camino);
    }

    private List<Integer> reconstruirCamino(int mask, int u) {
        LinkedList<Integer> camino = new LinkedList<>();
        while (u != -1) {
            camino.addFirst(u);
            int prevMask = mask ^ (1 << u);
            int prevU = predecesor[mask][u];
            u = prevU;
            mask = prevMask;
        }
        return camino;
    }
}

