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

    public ResultadoPD ejecutarPD(int numVertices, List<int[]> edges, int startVertex, int limiteDistancia) {
        Map<Integer, List<int[]>> grafo = new HashMap<>();
        for (int[] e : edges) {
            grafo.computeIfAbsent(e[0], k -> new ArrayList<>()).add(new int[]{e[1], e[2]});
            grafo.computeIfAbsent(e[1], k -> new ArrayList<>()).add(new int[]{e[0], e[2]});
        }

        boolean[] visitado = new boolean[numVertices];
        List<Integer> caminoActual = new ArrayList<>();
        List<Integer> mejorCamino = new ArrayList<>();
        int[] distanciaUsada = new int[]{0};

        dfsPD(startVertex, limiteDistancia, grafo, visitado, caminoActual, 0, mejorCamino, distanciaUsada);

        return new ResultadoPD(mejorCamino.size(), distanciaUsada[0], mejorCamino);
    }

    private void dfsPD(int nodo, int distanciaRestante, Map<Integer, List<int[]>> grafo,
                       boolean[] visitado, List<Integer> caminoActual,
                       int distanciaAcumulada, List<Integer> mejorCamino,
                       int[] distanciaUsada) {

        visitado[nodo] = true;
        caminoActual.add(nodo);

        boolean sePuedeExtender = false;
        for (int[] vecino : grafo.getOrDefault(nodo, Collections.emptyList())) {
            int next = vecino[0];
            int peso = vecino[1];
            if (!visitado[next] && distanciaRestante >= peso) {
                sePuedeExtender = true;
                dfsPD(next, distanciaRestante - peso, grafo, visitado,
                        caminoActual, distanciaAcumulada + peso, mejorCamino, distanciaUsada);
            }
        }


        if (!sePuedeExtender) {
            if (caminoActual.size() > mejorCamino.size()) {
                mejorCamino.clear();
                mejorCamino.addAll(new ArrayList<>(caminoActual));
                distanciaUsada[0] = distanciaAcumulada;
            } else if (caminoActual.size() == mejorCamino.size() && distanciaAcumulada < distanciaUsada[0]) {
                mejorCamino.clear();
                mejorCamino.addAll(new ArrayList<>(caminoActual));
                distanciaUsada[0] = distanciaAcumulada;
            }
        }

        // Backtrack
        visitado[nodo] = false;
        caminoActual.remove(caminoActual.size() - 1);
    }
}
