// AlgoritmoController.java

package com.nombreempresa.misistema.controller;

import com.nombreempresa.misistema.dto.AlgoritmoRequest;
import com.nombreempresa.misistema.service.*;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.List;

@RestController
@RequestMapping("/algoritmos")
@CrossOrigin(origins = "http://localhost:8080")
public class AlgoritmoController {

    private final DijkstraService dijkstraService;
    private final BFSService bfsService;
    private final DFSService dfsService;
    private final KruskalService kruskalService;
    private final BacktrackingService backtrackingService;
    private final PrimService primService;
    private final GreedyService greedyService;
    private final PDService pdService;
    private final BranchAndBoundService branchAndBoundService;

    // Constructor con inyección de dependencias para todos los servicios
    public AlgoritmoController(DijkstraService d, BFSService b, DFSService dfs, KruskalService k, BacktrackingService bt, PrimService p, GreedyService g, PDService pd, BranchAndBoundService bnb) {
        this.dijkstraService = d;
        this.bfsService = b;
        this.dfsService = dfs;
        this.kruskalService = k;
        this.backtrackingService = bt;
        this.primService = p;
        this.greedyService = g;
        this.pdService = pd;
        this.branchAndBoundService = bnb;
    }

    @PostMapping("/dijkstra")
    public Mono<List<String>> calcularDijkstra(@RequestBody AlgoritmoRequest req) {
        return Mono.fromCallable(() -> dijkstraService.ejecutarDijkstra(req.getNumVertices(), req.getEdges(), req.getStartVertex(), req.getEndVertex()));
    }

    @PostMapping("/bfs")
    public Mono<List<String>> calcularBFS(@RequestBody AlgoritmoRequest req) {
        return Mono.fromCallable(() -> bfsService.ejecutarBFS(req.getNumVertices(), req.getEdges(), req.getStartVertex(), req.getEndVertex()));
    }

    @PostMapping("/dfs")
    public Mono<List<String>> calcularDFS(@RequestBody AlgoritmoRequest req) {
        return Mono.fromCallable(() -> dfsService.ejecutarDFS(req.getNumVertices(), req.getEdges(), req.getStartVertex(), req.getEndVertex()));
    }

    @PostMapping("/backtracking")
    public Mono<List<String>> encontrarCaminos(@RequestBody AlgoritmoRequest req) {
        return Mono.fromCallable(() ->
                backtrackingService.encontrarCaminos(
                        req.getNumVertices(),
                        req.getEdges(),
                        req.getStartVertex(),
                        req.getEndVertex(),
                        req.getNodosObligatorios(),
                        req.getNodosEvitados()
                )
        );
    }

    @PostMapping("/branchandbound")
    public Mono<List<String>> ejecutarBranchAndBound(@RequestBody AlgoritmoRequest req) {
        return Mono.fromCallable(() ->
                branchAndBoundService.ejecutarBranchAndBound(
                        req.getNumVertices(),
                        req.getEdges(),
                        req.getStartVertex(),
                        req.getEndVertex(),
                        req.getNodosObligatorios(),
                        req.getNodosEvitados()
                )
        );
    }

    @PostMapping("/kruskal")
    public Mono<List<String>> calcularKruskal(@RequestBody AlgoritmoRequest req) {
        return Mono.fromCallable(() -> kruskalService.ejecutarKruskal(req.getNumVertices(), req.getEdges()));
    }

    @PostMapping("/prim")
    public Mono<List<String>> calcularPrim(@RequestBody AlgoritmoRequest req) {
        return Mono.fromCallable(() -> primService.primFromEdges(req.getNumVertices(), req.getEdges(), req.getStartVertex()));
    }


    @PostMapping("/greedy")
    public Mono<List<String>> ejecutarGreedy(@RequestBody AlgoritmoRequest req) {
        return Mono.fromCallable(() -> greedyService.ejecutarGreedy(req.getNumVertices(), req.getEdges(), req.getStartVertex(), req.getLimiteCosto()));
    }

    @PostMapping("/pd")
    public Mono<PDService.ResultadoPD> ejecutarPD(@RequestBody AlgoritmoRequest req) {
        return Mono.fromCallable(() ->
                pdService.ejecutarPD(
                        req.getNumVertices(),
                        req.getEdges(),
                        req.getStartVertex(),
                        req.getLimiteDistancia()
                )
        );
    }
}