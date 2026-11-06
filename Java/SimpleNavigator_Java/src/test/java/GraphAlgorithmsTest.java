import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import s21_graph.Graph;
import s21_graph_algorithms.GraphAlgorithms;
import s21_graph_algorithms.TsmResult;

/**
 * Модульные тесты для класса GraphAlgorithms.
 */
class GraphAlgorithmsTest {
  private Graph graph;
  private Graph graph11;

  @BeforeEach
  public void setUp() throws IOException {
    graph = new Graph();
    graph.loadGraphFromFile("../data-samples/graph3.txt");

    graph11 = new Graph();
    graph11.loadGraphFromFile("../data-samples/graph11.txt");
  }

  // ===== Тесты DFS =====

  @Test
  public void testDepthFirstSearch_startVertex1() {
    int[] result = GraphAlgorithms.depthFirstSearch(graph, 1);
    assertEquals(3, result.length);
    assertEquals(1, result[0]);
    assertTrue(containsAll(result, 1, 2, 3));
  }

  @Test
  public void testDepthFirstSearch_startVertex2() {
    int[] result = GraphAlgorithms.depthFirstSearch(graph, 2);
    assertEquals(3, result.length);
    assertEquals(2, result[0]);
    assertTrue(containsAll(result, 1, 2, 3));
  }

  @Test
  public void testDepthFirstSearch_startVertex3() {
    int[] result = GraphAlgorithms.depthFirstSearch(graph, 3);
    assertEquals(3, result.length);
    assertEquals(3, result[0]);
    assertTrue(containsAll(result, 1, 2, 3));
  }

  @Test
  public void testDepthFirstSearch_startVertexOutOfRange() {
    assertThrows(IllegalArgumentException.class, () -> GraphAlgorithms.depthFirstSearch(graph, 0));
    assertThrows(IllegalArgumentException.class, () -> GraphAlgorithms.depthFirstSearch(graph, 4));
  }

  @Test
  public void testDepthFirstSearch_startVertexOutOfRange_high() {
    assertThrows(
        IllegalArgumentException.class, () -> GraphAlgorithms.depthFirstSearch(graph, 100));
  }

  // ===== Тесты BFS =====

  @Test
  public void testBreadthFirstSearch_startVertex1() {
    int[] result = GraphAlgorithms.breadthFirstSearch(graph, 1);
    assertEquals(3, result.length);
    assertEquals(1, result[0]);
    assertTrue(containsAll(result, 1, 2, 3));
  }

  @Test
  public void testBreadthFirstSearch_startVertex2() {
    int[] result = GraphAlgorithms.breadthFirstSearch(graph, 2);
    assertEquals(3, result.length);
    assertEquals(2, result[0]);
    assertTrue(containsAll(result, 1, 2, 3));
  }

  @Test
  public void testBreadthFirstSearch_startVertex3() {
    int[] result = GraphAlgorithms.breadthFirstSearch(graph, 3);
    assertEquals(3, result.length);
    assertEquals(3, result[0]);
    assertTrue(containsAll(result, 1, 2, 3));
  }

  @Test
  public void testBreadthFirstSearch_startVertexOutOfRange() {
    assertThrows(
        IllegalArgumentException.class, () -> GraphAlgorithms.breadthFirstSearch(graph, 0));
    assertThrows(
        IllegalArgumentException.class, () -> GraphAlgorithms.breadthFirstSearch(graph, 5));
  }

  @Test
  public void testBreadthFirstSearch_startVertexOutOfRange_high() {
    assertThrows(
        IllegalArgumentException.class, () -> GraphAlgorithms.breadthFirstSearch(graph, 100));
  }

  // ===== Тесты алгоритма Дейкстры =====

  @Test
  public void testGetShortestPathBetweenVertices_sameVertex() {
    int distance = GraphAlgorithms.getShortestPathBetweenVertices(graph, 1, 1);
    assertEquals(0, distance);
  }

  @Test
  public void testGetShortestPathBetweenVertices_adjacent() {
    int distance = GraphAlgorithms.getShortestPathBetweenVertices(graph, 1, 2);
    assertEquals(1, distance);
  }

  @Test
  public void testGetShortestPathBetweenVertices_allPairs() {
    // Полный граф: все расстояния должны быть равны 1, так как веса ребер равны 1.
    for (int i = 1; i <= 3; i++) {
      for (int j = 1; j <= 3; j++) {
        if (i != j) {
          int distance = GraphAlgorithms.getShortestPathBetweenVertices(graph, i, j);
          assertEquals(1, distance, "Distance between " + i + " and " + j);
        }
      }
    }
  }

  @Test
  public void testGetShortestPathBetweenVertices_weightedGraph() {
    int distance = GraphAlgorithms.getShortestPathBetweenVertices(graph11, 1, 2);
    assertTrue(distance > 0);
    assertTrue(distance <= 29);
  }

  @Test
  public void testGetShortestPathBetweenVertices_weightedGraph_1_to_11() {
    int distance = GraphAlgorithms.getShortestPathBetweenVertices(graph11, 1, 11);
    assertTrue(distance > 0);
  }

  @Test
  public void testGetShortestPathBetweenVertices_outOfRange() {
    assertThrows(IllegalArgumentException.class,
        () -> GraphAlgorithms.getShortestPathBetweenVertices(graph, 0, 1));
    assertThrows(IllegalArgumentException.class,
        () -> GraphAlgorithms.getShortestPathBetweenVertices(graph, 1, 10));
  }

  @Test
  public void testGetShortestPathBetweenVertices_bothOutOfRange() {
    assertThrows(IllegalArgumentException.class,
        () -> GraphAlgorithms.getShortestPathBetweenVertices(graph, -1, 99));
  }

  @Test
  public void testGetShortestPathBetweenVertices_sameVertex_weighted() {
    int distance = GraphAlgorithms.getShortestPathBetweenVertices(graph11, 5, 5);
    assertEquals(0, distance);
  }

  // ===== Тесты алгоритма Флойда-Уоршелла =====

  @Test
  public void testGetShortestPathsBetweenAllVertices_diagonal() {
    int[][] result = GraphAlgorithms.getShortestPathsBetweenAllVertices(graph);
    for (int i = 0; i < graph.getVertexCount(); i++) {
      assertEquals(0, result[i][i]);
    }
  }

  @Test
  public void testGetShortestPathsBetweenAllVertices_symmetric() {
    int[][] result = GraphAlgorithms.getShortestPathsBetweenAllVertices(graph);
    for (int i = 0; i < result.length; i++) {
      for (int j = 0; j < result.length; j++) {
        assertEquals(result[i][j], result[j][i],
            "Matrix should be symmetric for undirected graph at [" + i + "][" + j + "]");
      }
    }
  }

  @Test
  public void testGetShortestPathsBetweenAllVertices_knownValue() {
    // Алгоритм Флойда-Уоршелла должен найти расстояние 1 между вершинами 1 и 2.
    int[][] result = GraphAlgorithms.getShortestPathsBetweenAllVertices(graph);
    assertEquals(1, result[0][1]);
    assertEquals(1, result[1][2]);
    assertEquals(1, result[0][2]);
  }

  @Test
  public void testGetShortestPathsBetweenAllVertices_weighted() {
    int[][] result = GraphAlgorithms.getShortestPathsBetweenAllVertices(graph11);
    // На диагонали должны быть нули.
    for (int i = 0; i < graph11.getVertexCount(); i++) {
      assertEquals(0, result[i][i]);
    }
    // Все расстояния должны быть конечными, так как граф связный.
    for (int i = 0; i < result.length; i++) {
      for (int j = 0; j < result.length; j++) {
        assertTrue(result[i][j] > 0 || i == j, "Distance [" + i + "][" + j + "] should be > 0");
      }
    }
  }

  // ===== Тесты алгоритма Прима =====

  @Test
  public void testGetLeastSpanningTree_validGraph() {
    int[][] mst = GraphAlgorithms.getLeastSpanningTree(graph);
    int edgeCount = countEdges(mst);
    assertEquals(graph.getVertexCount() - 1, edgeCount);
  }

  @Test
  public void testGetLeastSpanningTree_weightedGraph() {
    int[][] mst = GraphAlgorithms.getLeastSpanningTree(graph11);
    int edgeCount = countEdges(mst);
    assertEquals(graph11.getVertexCount() - 1, edgeCount);
  }

  @Test
  public void testGetLeastSpanningTree_directedGraph_throws(@TempDir Path tempDir)
      throws IOException {
    Path directedFile = tempDir.resolve("directed.txt");
    Files.writeString(directedFile, "2\n0 5\n3 0");
    Graph directedGraph = new Graph();
    directedGraph.loadGraphFromFile(directedFile.toString());

    assertThrows(
        IllegalArgumentException.class, () -> GraphAlgorithms.getLeastSpanningTree(directedGraph));
  }

  @Test
  public void testGetLeastSpanningTree_mstProperties() {
    // Проверяем свойства остова: симметричность и корректное число ребер.
    int[][] mst = GraphAlgorithms.getLeastSpanningTree(graph11);
    int n = graph11.getVertexCount();

    // Остов должен быть симметричным.
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        assertEquals(mst[i][j], mst[j][i]);
      }
    }

    // Количество ребер должно быть V - 1.
    int edgeCount = 0;
    for (int i = 0; i < n; i++) {
      for (int j = i + 1; j < n; j++) {
        if (mst[i][j] != 0) {
          edgeCount++;
        }
      }
    }
    assertEquals(n - 1, edgeCount);
  }

  // ===== Тесты TSP =====

  @Test
  public void testSolveTravelingSalesmanProblem_validGraph() {
    TsmResult result = GraphAlgorithms.solveTravelingSalesmanProblem(graph11);
    assertNotNull(result);
    assertNotNull(result.vertices());
    assertTrue(result.distance() > 0);

    int[] route = result.vertices();
    assertEquals(route[0], route[route.length - 1]);
  }

  @Test
  public void testSolveTravelingSalesmanProblem_allVerticesVisited() {
    TsmResult result = GraphAlgorithms.solveTravelingSalesmanProblem(graph11);
    int[] route = result.vertices();

    boolean[] visited = new boolean[graph11.getVertexCount()];
    for (int i = 0; i < route.length - 1; i++) {
      int v = route[i] - 1;
      assertFalse(visited[v], "Vertex should not be visited twice: " + (v + 1));
      visited[v] = true;
    }
    for (boolean v : visited) {
      assertTrue(v);
    }
  }

  @Test
  public void testSolveTravelingSalesmanProblem_smallGraph() {
    TsmResult result = GraphAlgorithms.solveTravelingSalesmanProblem(graph);
    assertNotNull(result);
    assertTrue(result.distance() > 0);

    int[] route = result.vertices();
    assertEquals(route[0], route[route.length - 1]);
  }

  @Test
  public void testSolveTravelingSalesmanProblem_smallGraph_visitsAll() {
    TsmResult result = GraphAlgorithms.solveTravelingSalesmanProblem(graph);
    int[] route = result.vertices();

    // В маленьком графе 3 вершины, маршрут должен иметь длину 4
    // (3 вершины и возврат в стартовую).
    assertEquals(4, route.length);
    assertEquals(route[0], route[3]);

    boolean[] visited = new boolean[3];
    for (int i = 0; i < route.length - 1; i++) {
      int v = route[i] - 1;
      assertFalse(visited[v]);
      visited[v] = true;
    }
    for (boolean v : visited) {
      assertTrue(v);
    }
  }

  @Test
  public void testSolveTravelingSalesmanProblem_routeIsValid() {
    TsmResult result = GraphAlgorithms.solveTravelingSalesmanProblem(graph);
    int[] route = result.vertices();
    int[][] matrix = graph.getAdjacencyMatrix();

    // Все соседние вершины в маршруте должны быть соединены ребром.
    for (int i = 0; i < route.length - 1; i++) {
      int from = route[i] - 1;
      int to = route[i + 1] - 1;
      assertTrue(matrix[from][to] != 0, "No edge between " + route[i] + " and " + route[i + 1]);
    }
  }

  @Test
  public void testSolveTravelingSalesmanProblem_dataSampleT2_solutionExists() throws IOException {
    Graph g = new Graph();
    g.loadGraphFromFile("../data-samples/t2.txt");

    TsmResult result = GraphAlgorithms.solveTravelingSalesmanProblem(g);
    int[] route = result.vertices();

    assertNotNull(result);
    assertEquals(g.getVertexCount() + 1, route.length);
    assertEquals(route[0], route[route.length - 1]);
    assertEquals(30.0, result.distance(), 1e-9);
  }

  @Test
  public void testSolveTravelingSalesmanProblem_dataSampleT1_noHamiltonianCycle()
      throws IOException {
    Graph g = new Graph();
    g.loadGraphFromFile("../data-samples/t1.txt");

    IllegalStateException exception = assertThrows(
        IllegalStateException.class, () -> GraphAlgorithms.solveTravelingSalesmanProblem(g));
    assertTrue(exception.getMessage().contains("not contain a Hamiltonian cycle"));
  }

  // ===== Граничные случаи: граф из одной вершины через временный файл =====

  @Test
  public void testTspSingleVertex_throws(@TempDir Path tempDir) throws IOException {
    Path singleFile = tempDir.resolve("single.txt");
    Files.writeString(singleFile, "1\n0");
    Graph singleGraph = new Graph();
    singleGraph.loadGraphFromFile(singleFile.toString());

    assertThrows(IllegalStateException.class,
        () -> GraphAlgorithms.solveTravelingSalesmanProblem(singleGraph));
  }

  // ===== Граничные случаи: getLeastSpanningTree на graph11 =====

  @Test
  public void testGetLeastSpanningTree_graph11_edgeCount() {
    int[][] mst = GraphAlgorithms.getLeastSpanningTree(graph11);
    int edgeCount = 0;
    for (int i = 0; i < mst.length; i++) {
      for (int j = i + 1; j < mst.length; j++) {
        if (mst[i][j] != 0) {
          edgeCount++;
        }
      }
    }
    assertEquals(graph11.getVertexCount() - 1, edgeCount);
  }

  // ===== Граничные случаи: поведение Stack и Queue =====

  @Test
  public void testStackOperations() {
    data_structures.Stack<Integer> stack = new data_structures.Stack<>();
    assertTrue(stack.isEmpty());
    assertEquals(0, stack.size());
    stack.push(1);
    stack.push(2);
    assertEquals(2, (int) stack.top());
    assertEquals(2, (int) stack.pop());
    assertEquals(1, (int) stack.pop());
    assertTrue(stack.isEmpty());

    // Проверяем автоматическое расширение.
    for (int i = 0; i < 20; i++) {
      stack.push(i);
    }
    assertEquals(20, stack.size());
    assertEquals(19, (int) stack.top());
  }

  @Test
  public void testQueueOperations() {
    data_structures.Queue<Integer> queue = new data_structures.Queue<>();
    assertTrue(queue.isEmpty());
    assertEquals(0, queue.size());
    queue.push(1);
    queue.push(2);
    assertEquals(1, (int) queue.front());
    assertEquals(2, (int) queue.back());
    assertEquals(1, (int) queue.pop());
    assertEquals(2, (int) queue.pop());
    assertTrue(queue.isEmpty());

    // Проверяем автоматическое расширение на большом количестве элементов.
    for (int i = 0; i < 20; i++) {
      queue.push(i);
    }
    assertEquals(20, queue.size());
    assertEquals(0, (int) queue.front());
    assertEquals(19, (int) queue.back());
  }

  @Test
  public void testStackPop_empty_throws() {
    data_structures.Stack<Integer> stack = new data_structures.Stack<>();
    assertThrows(java.util.EmptyStackException.class, stack::pop);
  }

  @Test
  public void testStackTop_empty_throws() {
    data_structures.Stack<Integer> stack = new data_structures.Stack<>();
    assertThrows(java.util.EmptyStackException.class, stack::top);
  }

  @Test
  public void testQueuePop_empty_throws() {
    data_structures.Queue<Integer> queue = new data_structures.Queue<>();
    assertThrows(java.util.NoSuchElementException.class, queue::pop);
  }

  @Test
  public void testQueueFront_empty_throws() {
    data_structures.Queue<Integer> queue = new data_structures.Queue<>();
    assertThrows(java.util.NoSuchElementException.class, queue::front);
  }

  @Test
  public void testQueueBack_empty_throws() {
    data_structures.Queue<Integer> queue = new data_structures.Queue<>();
    assertThrows(java.util.NoSuchElementException.class, queue::back);
  }

  // ===== Тесты DFS для ориентированного графа =====

  @Test
  public void testDepthFirstSearch_directedGraph(@TempDir Path tempDir) throws IOException {
    Path directedFile = tempDir.resolve("directed_dfs.txt");
    Files.writeString(directedFile, "3\n0 1 0\n0 0 1\n0 0 0");
    Graph directedGraph = new Graph();
    directedGraph.loadGraphFromFile(directedFile.toString());

    int[] result = GraphAlgorithms.depthFirstSearch(directedGraph, 1);
    assertEquals(3, result.length);
    assertEquals(1, result[0]);
    assertTrue(containsAll(result, 1, 2, 3));
  }

  @Test
  public void testBreadthFirstSearch_directedGraph(@TempDir Path tempDir) throws IOException {
    Path directedFile = tempDir.resolve("directed_bfs.txt");
    Files.writeString(directedFile, "3\n1 1 0\n0 0 1\n1 0 0");
    Graph directedGraph = new Graph();
    directedGraph.loadGraphFromFile(directedFile.toString());

    int[] result = GraphAlgorithms.breadthFirstSearch(directedGraph, 1);
    assertEquals(3, result.length);
  }

  @Test
  public void testT1_loadGraph() throws IOException {
    // t1.txt - связный граф из 7 вершин.
    Graph g = new Graph();
    g.loadGraphFromFile("../data-samples/t1.txt");
    assertEquals(7, g.getVertexCount());
    assertFalse(g.isDirected());
    assertTrue(g.isWeighted());
  }

  @Test
  public void testT2_dfs() throws IOException {
    Graph g = new Graph();
    g.loadGraphFromFile("../data-samples/t2.txt");
    int[] result = GraphAlgorithms.depthFirstSearch(g, 1);
    assertEquals(4, result.length);
    assertTrue(containsAll(result, 1, 2, 3, 4));
  }

  @Test
  public void testT2_bfs() throws IOException {
    Graph g = new Graph();
    g.loadGraphFromFile("../data-samples/t2.txt");
    int[] result = GraphAlgorithms.breadthFirstSearch(g, 1);
    assertEquals(4, result.length);
    assertTrue(containsAll(result, 1, 2, 3, 4));
  }

  @Test
  public void testT2_dijkstra() throws IOException {
    Graph g = new Graph();
    g.loadGraphFromFile("../data-samples/t2.txt");
    // Веса t2.txt: 1-2=7, 1-3=15, 1-4=3, 2-3=12, 2-4=19, 3-4=8.
    // Кратчайший путь 1->3: min(15, 7+12=19, 3+8=11) = 11 через 1-4-3.
    assertEquals(7, GraphAlgorithms.getShortestPathBetweenVertices(g, 1, 2));
    assertEquals(3, GraphAlgorithms.getShortestPathBetweenVertices(g, 1, 4));
    assertEquals(11, GraphAlgorithms.getShortestPathBetweenVertices(g, 1, 3));
  }

  @Test
  public void testT2_floyd() throws IOException {
    Graph g = new Graph();
    g.loadGraphFromFile("../data-samples/t2.txt");
    int[][] result = GraphAlgorithms.getShortestPathsBetweenAllVertices(g);
    assertEquals(4, result.length);
    for (int i = 0; i < 4; i++) {
      assertEquals(0, result[i][i]);
    }
  }

  @Test
  public void testT2_prim() throws IOException {
    Graph g = new Graph();
    g.loadGraphFromFile("../data-samples/t2.txt");
    int[][] mst = GraphAlgorithms.getLeastSpanningTree(g);
    assertEquals(3, countEdges(mst));
  }

  @Test
  public void testT2_tsp() throws IOException {
    Graph g = new Graph();
    g.loadGraphFromFile("../data-samples/t2.txt");
    TsmResult result = GraphAlgorithms.solveTravelingSalesmanProblem(g);
    assertNotNull(result);
    int[] route = result.vertices();
    assertTrue(route.length > 0);
    assertEquals(route[0], route[route.length - 1]);
  }

  @Test
  public void testT3_connectedGraph() throws IOException {
    Graph g = new Graph();
    g.loadGraphFromFile("../data-samples/t3.txt");
    assertEquals(7, g.getVertexCount());
    assertFalse(g.isDirected());
    assertTrue(g.isWeighted());
  }

  @Test
  public void testT3_dfs() throws IOException {
    Graph g = new Graph();
    g.loadGraphFromFile("../data-samples/t3.txt");
    int[] result = GraphAlgorithms.depthFirstSearch(g, 1);
    assertEquals(7, result.length);
    assertTrue(containsAll(result, 1, 2, 3, 4, 5, 6, 7));
  }

  @Test
  public void testT3_bfs() throws IOException {
    Graph g = new Graph();
    g.loadGraphFromFile("../data-samples/t3.txt");
    int[] result = GraphAlgorithms.breadthFirstSearch(g, 1);
    assertEquals(7, result.length);
    assertTrue(containsAll(result, 1, 2, 3, 4, 5, 6, 7));
  }

  @Test
  public void testT3_dijkstra() throws IOException {
    Graph g = new Graph();
    g.loadGraphFromFile("../data-samples/t3.txt");
    // t3: путь 1-3-7 (12+4=16), прямого ребра 1-7 нет => 16.
    assertEquals(16, GraphAlgorithms.getShortestPathBetweenVertices(g, 1, 7));
    // Путь 3-5(8), 5-6(25) = 33; или 3-7(4),7-5(3)=7 => кратчайший 3-5 = 7.
    assertEquals(7, GraphAlgorithms.getShortestPathBetweenVertices(g, 3, 5));
  }

  @Test
  public void testT3_floyd() throws IOException {
    Graph g = new Graph();
    g.loadGraphFromFile("../data-samples/t3.txt");
    int[][] result = GraphAlgorithms.getShortestPathsBetweenAllVertices(g);
    for (int i = 0; i < 7; i++) {
      assertEquals(0, result[i][i]);
      for (int j = 0; j < 7; j++) {
        assertTrue(result[i][j] >= 0);
      }
    }
  }

  @Test
  public void testT3_prim() throws IOException {
    Graph g = new Graph();
    g.loadGraphFromFile("../data-samples/t3.txt");
    int[][] mst = GraphAlgorithms.getLeastSpanningTree(g);
    assertEquals(6, countEdges(mst));
  }

  @Test
  public void testT4_completeGraph() throws IOException {
    Graph g = new Graph();
    g.loadGraphFromFile("../data-samples/t4.txt");
    assertEquals(7, g.getVertexCount());
    assertFalse(g.isDirected());
    assertTrue(g.isWeighted());
  }

  @Test
  public void testT4_dfs() throws IOException {
    Graph g = new Graph();
    g.loadGraphFromFile("../data-samples/t4.txt");
    int[] result = GraphAlgorithms.depthFirstSearch(g, 1);
    assertEquals(7, result.length);
    assertTrue(containsAll(result, 1, 2, 3, 4, 5, 6, 7));
  }

  @Test
  public void testT4_bfs() throws IOException {
    Graph g = new Graph();
    g.loadGraphFromFile("../data-samples/t4.txt");
    int[] result = GraphAlgorithms.breadthFirstSearch(g, 1);
    assertEquals(7, result.length);
    assertTrue(containsAll(result, 1, 2, 3, 4, 5, 6, 7));
  }

  @Test
  public void testT4_dijkstra() throws IOException {
    Graph g = new Graph();
    g.loadGraphFromFile("../data-samples/t4.txt");
    // Прямое ребро 1-2 = 34.
    assertEquals(34, GraphAlgorithms.getShortestPathBetweenVertices(g, 1, 2));
    // Кратчайший путь 1->7: прямой=29, или 1-2-7=34+58=92,
    // 1-4-7=23+42=65, 1-5-7=38+22=60.
    assertEquals(29, GraphAlgorithms.getShortestPathBetweenVertices(g, 1, 7));
  }

  @Test
  public void testT4_floyd() throws IOException {
    Graph g = new Graph();
    g.loadGraphFromFile("../data-samples/t4.txt");
    int[][] result = GraphAlgorithms.getShortestPathsBetweenAllVertices(g);
    for (int i = 0; i < 7; i++) {
      assertEquals(0, result[i][i]);
    }
  }

  @Test
  public void testT4_prim() throws IOException {
    Graph g = new Graph();
    g.loadGraphFromFile("../data-samples/t4.txt");
    int[][] mst = GraphAlgorithms.getLeastSpanningTree(g);
    assertEquals(6, countEdges(mst));
  }

  @Test
  public void testT4_tsp() throws IOException {
    Graph g = new Graph();
    g.loadGraphFromFile("../data-samples/t4.txt");
    TsmResult result = GraphAlgorithms.solveTravelingSalesmanProblem(g);
    assertNotNull(result);
    int[] route = result.vertices();
    assertEquals(route[0], route[route.length - 1]);
    assertTrue(result.distance() > 0);
  }

  @Test
  public void testT5_directedGraph() throws IOException {
    Graph g = new Graph();
    g.loadGraphFromFile("../data-samples/t5.txt");
    assertEquals(7, g.getVertexCount());
    assertTrue(g.isDirected());
    assertTrue(g.isWeighted());
  }

  @Test
  public void testT5_dfs() throws IOException {
    Graph g = new Graph();
    g.loadGraphFromFile("../data-samples/t5.txt");
    int[] result = GraphAlgorithms.depthFirstSearch(g, 7);
    // DFS из 7: из 7 есть исходящие ребра в 1, 2, 5.
    assertTrue(result.length >= 1);
    assertTrue(containsAll(result, 7, 1, 2, 5));
  }

  @Test
  public void testT5_bfs() throws IOException {
    Graph g = new Graph();
    g.loadGraphFromFile("../data-samples/t5.txt");
    int[] result = GraphAlgorithms.breadthFirstSearch(g, 7);
    assertTrue(result.length >= 1);
    assertTrue(containsAll(result, 7, 1, 2, 5));
  }

  @Test
  public void testT5_dijkstra() throws IOException {
    Graph g = new Graph();
    g.loadGraphFromFile("../data-samples/t5.txt");
    // Прямое ребро 7->1 = 29.
    assertEquals(29, GraphAlgorithms.getShortestPathBetweenVertices(g, 7, 1));
    // Прямой путь 7->2 = 58, путь 7->1->2 = 29+34=63.
    assertEquals(58, GraphAlgorithms.getShortestPathBetweenVertices(g, 7, 2));
  }

  @Test
  public void testT5_floyd() throws IOException {
    Graph g = new Graph();
    g.loadGraphFromFile("../data-samples/t5.txt");
    int[][] result = GraphAlgorithms.getShortestPathsBetweenAllVertices(g);
    for (int i = 0; i < 7; i++) {
      assertEquals(0, result[i][i]);
    }
  }

  @Test
  public void testT5_prim_throws() throws IOException {
    Graph g = new Graph();
    g.loadGraphFromFile("../data-samples/t5.txt");
    // Алгоритм Прима требует неориентированный граф.
    assertThrows(IllegalArgumentException.class, () -> GraphAlgorithms.getLeastSpanningTree(g));
  }

  // ===== Вспомогательные методы =====

  private boolean containsAll(int[] array, int... values) {
    for (int value : values) {
      boolean found = false;
      for (int v : array) {
        if (v == value) {
          found = true;
          break;
        }
      }
      if (!found) {
        return false;
      }
    }
    return true;
  }

  private int countEdges(int[][] matrix) {
    int count = 0;
    for (int i = 0; i < matrix.length; i++) {
      for (int j = i + 1; j < matrix.length; j++) {
        if (matrix[i][j] != 0) {
          count++;
        }
      }
    }
    return count;
  }

  // ===== Тесты пакетных вспомогательных методов для покрытия =====

  @Test
  public void testTrimResult_actualSizeLessThanLength() {
    int[] input = {1, 2, 3, 4, 5};
    int[] trimmed = GraphAlgorithms.trimResult(input, 3);
    assertEquals(3, trimmed.length);
    assertEquals(1, trimmed[0]);
    assertEquals(3, trimmed[2]);
  }

  @Test
  public void testTrimResult_actualSizeEqualsLength() {
    int[] input = {1, 2, 3};
    int[] result = GraphAlgorithms.trimResult(input, 3);
    assertSame(input, result);
  }

  @Test
  public void testTrimResult_zeroActualSize() {
    int[] input = {1, 2, 3};
    int[] trimmed = GraphAlgorithms.trimResult(input, 0);
    assertEquals(0, trimmed.length);
  }

  @Test
  public void testSelectNextCity_noAvailableCity_returnsMinusOne() {
    boolean[] visited = new boolean[] {false, true};
    double[][] pheromone = new double[][] {{0, 0}, {0, 0}};
    int[][] distances = new int[][] {{0, 0}, {0, 0}};
    int next = GraphAlgorithms.selectNextCity(0, visited, pheromone, distances, 1.0, 2.0);
    assertEquals(-1, next);
  }

  @Test
  public void testSelectNextCity_singleAvailableCity_choosesIt() {
    boolean[] visited = new boolean[] {true, false, false};
    double[][] pheromone = new double[][] {{0, 1.0, 0}, {1.0, 0, 0}, {0, 0, 0}};
    int[][] distances = new int[][] {{0, 5, 0}, {5, 0, 0}, {0, 0, 0}};
    int next = GraphAlgorithms.selectNextCity(0, visited, pheromone, distances, 1.0, 2.0);
    assertEquals(1, next);
  }

  @Test
  public void testSelectNextCity_multipleAvailableCities_returnsValidIndex() {
    boolean[] visited = new boolean[] {true, false, false};
    double[][] pheromone = new double[][] {{0, 1.0, 1.0}, {1.0, 0, 0}, {1.0, 0, 0}};
    int[][] distances = new int[][] {{0, 1, 1}, {1, 0, 0}, {1, 0, 0}};
    int next = GraphAlgorithms.selectNextCity(0, visited, pheromone, distances, 1.0, 2.0);
    assertTrue(next == 1 || next == 2);
  }
}
