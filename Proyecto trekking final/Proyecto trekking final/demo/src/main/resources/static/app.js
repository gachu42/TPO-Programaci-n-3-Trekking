// app.js

// --- 1. CONFIGURACIÓN INICIAL Y DATOS GLOBALES ---
const container = document.getElementById('network-map');
const algoritmoSelector = document.getElementById('algoritmo-selector');
const paramFormContainer = document.getElementById('param-form-container');
const resultadoTexto = document.getElementById('resultado-texto');

const nodesDataSet = new vis.DataSet([]);
const edgesDataSet = new vis.DataSet([]);
const data = { nodes: nodesDataSet, edges: edgesDataSet };

let network;
let LUGAR_MAPA = {};
let LUGAR_MAPA_INVERSO = {};
let NUM_VERTICES = 0;
let EDGES_INPUT = [];

const BASE_URL = 'http://localhost:8080/algoritmos';

const options = {
    edges: {
        arrows: '',
        font: { align: 'top', size: 10, color: '#333' },
        smooth: { type: 'dynamic' },
        width: 3,
        scaling: { min: 3, max: 3 }
    },
    physics: { enabled: true, solver: 'repulsion' },
    interaction: { zoomView: true }
};

// --- 2. CARGA INICIAL DEL GRAFO ---
async function loadGraphData() {
    resultadoTexto.textContent = 'Cargando mapa desde el backend (/lugares)...';
    try {
        const response = await fetch('http://localhost:8080/lugares');
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        const lugares = await response.json();
        let nodeIdCounter = 0;
        nodesDataSet.clear();
        edgesDataSet.clear();
        LUGAR_MAPA = {};
        LUGAR_MAPA_INVERSO = {};
        EDGES_INPUT = [];
        lugares.forEach(lugar => {
            if (!lugar.nombre) return;
            if (!LUGAR_MAPA[lugar.nombre]) {
                LUGAR_MAPA[lugar.nombre] = nodeIdCounter;
                LUGAR_MAPA_INVERSO[nodeIdCounter] = lugar.nombre;
                nodesDataSet.add({
                    id: nodeIdCounter,
                    label: lugar.nombre,
                    title: `${lugar.nombre} (${lugar.tipo || 'LUGAR'})`,
                    color: { background: '#007bff', border: '#0056b3' }
                });
                nodeIdCounter++;
            }
        });
        NUM_VERTICES = nodeIdCounter;
        lugares.forEach(lugar => {
            if (!lugar.nombre) return;
            const fromId = LUGAR_MAPA[lugar.nombre];
            if (!lugar.conexiones || !Array.isArray(lugar.conexiones)) return;
            lugar.conexiones.forEach(conexion => {
                if (!conexion.destino || !conexion.destino.nombre) return;
                const toId = LUGAR_MAPA[conexion.destino.nombre];
                const weight = conexion.distancia;
                if (fromId < toId) EDGES_INPUT.push([fromId, toId, weight]);
                const edgeId = fromId < toId ? `${fromId}-${toId}` : `${toId}-${fromId}`;
                if (!edgesDataSet.get(edgeId)) {
                    edgesDataSet.add({
                        id: edgeId,
                        from: fromId,
                        to: toId,
                        label: weight + ' km',
                        value: weight,
                        color: { color: '#6c757d' },
                        width: 3
                    });
                }
            });
        });
        if (!network) network = new vis.Network(container, data, options);
        resultadoTexto.textContent = `Mapa cargado. Total de nodos: ${NUM_VERTICES}. Total de aristas: ${edgesDataSet.length}. Selecciona un algoritmo.`;
        network.fit();
    } catch (error) {
        resultadoTexto.textContent = `ERROR al cargar el mapa. Verifica la conexión con el backend en http://localhost:8080.`;
        console.error("Error al cargar el grafo:", error);
    }
}
window.onload = loadGraphData;

// --- 3. INTERACCIÓN Y FORMULARIO ---
function createNodeSelector(id, label, isMultiple = false) {
    const selector = document.createElement('select');
    selector.id = id;
    selector.name = id;
    selector.multiple = isMultiple;
    selector.className = 'w-full p-2 border border-gray-300 rounded-lg shadow-sm mb-2';
    if (isMultiple) selector.size = 5;
    const defaultOption = document.createElement('option');
    defaultOption.value = "";
    defaultOption.textContent = label;
    if (!isMultiple) selector.appendChild(defaultOption);
    Object.keys(LUGAR_MAPA).sort().forEach(nombre => {
        const option = document.createElement('option');
        option.value = LUGAR_MAPA[nombre];
        option.textContent = nombre;
        selector.appendChild(option);
    });
    return selector;
}

algoritmoSelector.addEventListener('change', (e) => {
    const selectedAlg = e.target.value;
    paramFormContainer.innerHTML = '';
    resultadoTexto.textContent = '';
    if (selectedAlg) {
        const form = document.createElement('form');
        form.id = 'algoritmo-form';
        let buttonText = 'Calcular';

        if (['dijkstra', 'bfs', 'dfs'].includes(selectedAlg)) {
            form.appendChild(createNodeSelector('origen', 'Selecciona el Origen'));
            form.appendChild(createNodeSelector('destino', 'Selecciona el Destino'));
            buttonText = 'Calcular Ruta';


        } else if (['backtracking', 'branchandbound'].includes(selectedAlg)) {
            form.appendChild(createNodeSelector('origen', 'Selecciona el Origen'));
            form.appendChild(createNodeSelector('destino', 'Selecciona el Destino'));
            form.appendChild(document.createElement('br'));

            const labelObligatorios = document.createElement('label');
            labelObligatorios.textContent = 'Nodos Obligatorios (Ctrl + Click):';
            form.appendChild(labelObligatorios);
            form.appendChild(createNodeSelector('nodosObligatorios', '', true));

            const labelEvitados = document.createElement('label');
            labelEvitados.textContent = 'Nodos a Evitar (Ctrl + Click):';
            form.appendChild(labelEvitados);
            form.appendChild(createNodeSelector('nodosEvitados', '', true));


            if (selectedAlg === 'branchandbound') {
                 buttonText = 'Calcular Mejor Ruta (Branch and Bound)';
            } else { // 'backtracking'
                 buttonText = 'Encontrar Caminos';
            }


        } else if (selectedAlg === 'prim') {
            form.appendChild(createNodeSelector('origen', 'Selecciona el Nodo Inicial'));
            buttonText = 'Calcular MST';

        } else if (selectedAlg === 'greedy') {
            form.appendChild(createNodeSelector('origen', 'Selecciona el Nodo Inicial'));
            const costInput = document.createElement('input');
            costInput.type = 'number';
            costInput.id = 'limiteCosto'; // ID CORREGIDO
            costInput.name = 'limiteCosto'; // NAME CORREGIDO
            costInput.placeholder = 'Límite de Costo (0 = sin límite)';
            costInput.className = 'w-full p-2 border border-gray-300 rounded-lg shadow-sm mb-2';
            costInput.value = ""; // Valor por defecto, el usuario lo puede cambiar
            form.appendChild(costInput);
            buttonText = 'Ejecutar Recorrido Greedy';

        } else if (selectedAlg === 'kruskal') {
            buttonText = 'Calcular MST Global';
        } else if (selectedAlg === 'pd') {
            form.appendChild(createNodeSelector('origen', 'Selecciona el Nodo Inicial'));
            const limiteInput = document.createElement('input');
            limiteInput.type = 'number';
            limiteInput.id = 'limiteDistancia';
            limiteInput.name = 'limiteDistancia';
            limiteInput.placeholder = 'Límite de Distancia';
            limiteInput.className = 'w-full p-2 border border-gray-300 rounded-lg shadow-sm mb-2';
            limiteInput.value = ""; // Valor por defecto, el usuario lo puede cambiar
            form.appendChild(limiteInput);
            buttonText = 'Ejecutar PD';
        }

        const submitButton = document.createElement('button');
        submitButton.type = 'submit';
        submitButton.className = 'w-full mt-4 p-3 bg-emerald-500 text-white font-semibold rounded-lg shadow-md hover:bg-emerald-600';
        submitButton.textContent = buttonText;
        form.appendChild(submitButton);
        paramFormContainer.appendChild(form);
        form.addEventListener('submit', (event) => {
            event.preventDefault();
            ejecutarAlgoritmo(selectedAlg, form);
        });
    }
});

// --- 4. EJECUCIÓN DE ALGORITMOS ---
async function ejecutarAlgoritmo(algoritmo, form) {
    resultadoTexto.textContent = 'Calculando...';
    actualizarVisualizacion('reset', {});

    const origenId = form.origen ? parseInt(form.origen.value) : null;
    const destinoId = form.destino ? parseInt(form.destino.value) : null;

    const limiteCosto = form.limiteCosto ? parseInt(form.limiteCosto.value) || 0 : 0;
    const limiteDistancia = form.limiteDistancia ? parseInt(form.limiteDistancia.value) || 0 : 0;

    const nodosObligatorios = form.nodosObligatorios ? Array.from(form.nodosObligatorios.selectedOptions).map(o => parseInt(o.value)) : [];
    const nodosEvitados = form.nodosEvitados ? Array.from(form.nodosEvitados.selectedOptions).map(o => parseInt(o.value)) : [];

    const requestBody = {
        numVertices: NUM_VERTICES,
        edges: EDGES_INPUT,
        startVertex: origenId,
        endVertex: destinoId,
        nodosObligatorios,
        nodosEvitados,
        limiteCosto,
        limiteDistancia
    };

    try {
        const url = `${BASE_URL}/${algoritmo}`;
        const response = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(requestBody)
        });
        if (!response.ok) {
            let errorText = await response.text();
            throw new Error(`Error en el backend. Código: ${response.status}. Detalle: ${errorText.substring(0, 100)}...`);
        }

        const result = await response.json();
        let finalDisplay = [];
        let pathData = {};

        if (algoritmo === 'pd') {
            finalDisplay = [
                `Mejor Camino (PD): ${result.camino.map(id => LUGAR_MAPA_INVERSO[id]).join(' → ')}`,
                `Lugares Visitados: #VAL#${result.nodosVisitados}`,
                `Distancia Total: #VAL#${result.distanciaTotal} km`
            ];
            pathData = { nodes: result.camino, edges: caminoToEdges(result.camino) };
        } else if (['kruskal', 'prim'].includes(algoritmo)) {
            const parsed = parseMSTOutput(result);
            finalDisplay = parsed.finalDisplay;
            pathData = parsed.pathData;
        } else { // Algoritmos de ruta (incluye Branch and Bound y Backtracking)
            finalDisplay = translateRouteOutput(result);
            pathData = extractPathData(algoritmo, result);
        }

        resultadoTexto.textContent = finalDisplay.map(line => String(line).replace(/#VAL#/g, '')).join('\n');
        actualizarVisualizacion(algoritmo, pathData);

    } catch (error) {
        resultadoTexto.textContent = `ERROR al ejecutar el algoritmo. ${error.message}`;
        console.error("Error de fetch o backend:", error);
    }
}

// --- 5. FUNCIONES AUXILIARES ---
function translateRouteOutput(resultTextArray) {
    return resultTextArray.map(line => {
        const lineStr = String(line).trim();
        // Solo traducir líneas que contengan patrones de ruta explícitos
        if (lineStr.includes("→") || lineStr.startsWith("Camino") || lineStr.includes("[")) {
            return lineStr.replace(/(\d+)/g, match => LUGAR_MAPA_INVERSO[parseInt(match)] || match);
        }
        return lineStr; // Devuelve otras líneas (resúmenes) sin traducir
    });
}

function parseMSTOutput(resultTextArray) {
    let finalDisplay = [], edgesToHighlight = [], nodeSet = new Set();
    resultTextArray.forEach(line => {
        const lineStr = String(line);
        if (lineStr.startsWith("Peso Total")) {
            finalDisplay.unshift(lineStr);
        } else {
            const match = lineStr.match(/(\d+)--(\d+)--#VAL#(\d+)/);
            if (match) {
                const [id1, id2, weight] = [parseInt(match[1]), parseInt(match[2]), match[3]];
                finalDisplay.push(`${LUGAR_MAPA_INVERSO[id1]} -- ${LUGAR_MAPA_INVERSO[id2]} (Peso: ${weight})`);
                edgesToHighlight.push([id1, id2]);
                nodeSet.add(id1);
                nodeSet.add(id2);
            }
        }
    });
    return { finalDisplay, pathData: { nodes: Array.from(nodeSet), edges: edgesToHighlight } };
}

function extractPathData(algoritmo, resultTextArray) {
    let pathData = { nodes: [], edges: [] };
    // Buscamos líneas que contengan "RUTA MÁS CORTA" o "Camino" (Backtracking)
    const pathLine = resultTextArray.find(line => String(line).includes("→") || String(line).includes("RUTA MÁS CORTA") || String(line).includes("["));
    if (pathLine) {
        const numbers = String(pathLine).match(/(\d+)/g);
        if (numbers) {
            pathData.nodes = numbers.map(s => parseInt(s));
            pathData.edges = caminoToEdges(pathData.nodes);
        }
    }
    return pathData;
}

function caminoToEdges(camino) {
    let edges = [];
    for (let i = 0; i < camino.length - 1; i++) {
        edges.push([camino[i], camino[i + 1]]);
    }
    return edges;
}

// --- 6. VISUALIZACIÓN ---
function actualizarVisualizacion(algoritmo, pathData) {
    nodesDataSet.forEach(node => nodesDataSet.update({ id: node.id, color: { background: '#007bff', border: '#0056b3' }, size: 18 }));
    edgesDataSet.forEach(edge => edgesDataSet.update({ id: edge.id, color: { color: '#6c757d' }, width: 3 }));

    if (pathData && pathData.nodes && pathData.nodes.length > 0) {
        pathData.nodes.forEach(nodeId => nodesDataSet.update({ id: nodeId, color: { background: '#ffc107', border: '#e0a800' }, size: 25 }));
    }

    if (pathData && pathData.edges && pathData.edges.length > 0) {
        const isMST = ['kruskal', 'prim'].includes(algoritmo);
        const edgeColor = isMST ? '#28a745' : '#dc3545'; // Verde para MST, Rojo para rutas

        pathData.edges.forEach(([from, to]) => {
            const edgeId = from < to ? `${from}-${to}` : `${to}-${from}`;
            if (edgesDataSet.get(edgeId)) {
                edgesDataSet.update({ id: edgeId, color: { color: edgeColor }, width: 4 });
            }
        });
    }
    network.fit();
}