package com.nombreempresa.misistema.service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class KruskalService {

    public List<String> ejecutarKruskal(int numVertices, List<int[]> edges) {
        List<Arista> aristas = new ArrayList<>();
        for (int[] e : edges) {
            aristas.add(new Arista(e[0], e[1], e[2]));
        }

        Collections.sort(aristas);
        UnionFind uf = new UnionFind(numVertices);

        List<Arista> resultadoMST = new ArrayList<>();
        int pesoTotal = 0;

        for (Arista arista : aristas) {
            int origenRaiz = uf.encontrar(arista.origen);
            int destinoRaiz = uf.encontrar(arista.destino);

            if (origenRaiz != destinoRaiz) {
                resultadoMST.add(arista);
                uf.unir(origenRaiz, destinoRaiz);
                pesoTotal += arista.peso;
            }

            if (resultadoMST.size() == numVertices - 1) {
                break;
            }
        }

        return construirResultado(resultadoMST, pesoTotal);
    }

    private List<String> construirResultado(List<Arista> mst, int pesoTotal) {
        List<String> resultadoFinal = new ArrayList<>();

        // 1. Añadir el peso total al inicio (Protegemos el número)
        resultadoFinal.add("Peso Total del MST (Kruskal): #VAL#" + pesoTotal + " km");

        // 2. Añadir las aristas en formato ID1--ID2--#VAL#PESO
        for (Arista a : mst) {
            int id1 = Math.min(a.origen, a.destino);
            int id2 = Math.max(a.origen, a.destino);
            resultadoFinal.add(id1 + "--" + id2 + "--#VAL#" + a.peso);
        }

        return resultadoFinal;
    }

    // Clases auxiliares internas (Arista y UnionFind) sin cambios.
    private static class Arista implements Comparable<Arista> {
        int origen, destino, peso;
        public Arista(int o, int d, int p) { origen = o; destino = d; peso = p; }
        @Override
        public int compareTo(Arista o) { return this.peso - o.peso; }
    }

    private static class UnionFind {
        private int[] padre;
        public UnionFind(int n) {
            padre = new int[n];
            for (int i = 0; i < n; i++) padre[i] = i;
        }
        public int encontrar(int u) {
            if (padre[u] != u) padre[u] = encontrar(padre[u]);
            return padre[u];
        }
        public void unir(int u, int v) {
            int raizU = encontrar(u);
            int raizV = encontrar(v);
            if (raizU != raizV) padre[raizV] = raizU;
        }
    }
}
