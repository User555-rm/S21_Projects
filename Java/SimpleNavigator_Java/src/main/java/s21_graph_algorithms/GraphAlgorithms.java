package s21_graph_algorithms;

import data_structures.Queue;
import data_structures.Stack;
import s21_graph.Graph;

/**
 * Реализует алгоритмы на графах.
 */
public class GraphAlgorithms {
  /**
   * Выполняет нерекурсивный поиск в глубину (DFS) из заданной вершины.
   *
   * @param graph       граф для обхода
   * @param startVertex стартовая вершина (нумерация с 1)
   * @return массив вершин в порядке посещения
   * @throws IllegalArgumentException если стартовая вершина вне диапазона
   */
  public static int[] depthFirstSearch(Graph graph, int startVertex) {
    int vertexCount = graph.getVertexCount();
    validateVertex(startVertex, vertexCount);

    int[] result = new int[vertexCount];
    int resultIndex = 0;
    boolean[] visited = new boolean[vertexCount];
    int[][] matrix = graph.getAdjacencyMatrix();

    Stack<Integer> stack = new Stack<>();
    stack.push(startVertex - 1); // Переход к нумерации с 0.

    while (!stack.isEmpty()) {
      int v = stack.pop();
      if (!visited[v]) {
        visited[v] = true;
        result[resultIndex++] = v + 1; // Возврат к нумерации с 1.

        // Добавляем соседей в обратном порядке, чтобы вершины с меньшими индексами
        // обрабатывались первыми.
        for (int u = vertexCount - 1; u >= 0; u--) {
          if (matrix[v][u] != 0 && !visited[u]) {
            stack.push(u);
          }
        }
      }
    }

    // Если посещены не все вершины, обрезаем результат до фактического размера.
    return trimResult(result, resultIndex);
  }

  /**
   * Выполняет поиск в ширину (BFS) из заданной вершины.
   *
   * @param graph       граф для обхода
   * @param startVertex стартовая вершина (нумерация с 1)
   * @return массив вершин в порядке посещения
   * @throws IllegalArgumentException если стартовая вершина вне диапазона
   */
  public static int[] breadthFirstSearch(Graph graph, int startVertex) {
    int vertexCount = graph.getVertexCount();
    validateVertex(startVertex, vertexCount);

    int[] result = new int[vertexCount];
    int resultIndex = 0;
    boolean[] visited = new boolean[vertexCount];
    int[][] matrix = graph.getAdjacencyMatrix();

    Queue<Integer> queue = new Queue<>();
    queue.push(startVertex - 1); // Переход к нумерации с 0.
    visited[startVertex - 1] = true;

    while (!queue.isEmpty()) {
      int v = queue.pop();
      result[resultIndex++] = v + 1; // Возврат к нумерации с 1.

      for (int u = 0; u < vertexCount; u++) {
        if (matrix[v][u] != 0 && !visited[u]) {
          visited[u] = true;
          queue.push(u);
        }
      }
    }

    return trimResult(result, resultIndex);
  }

  /**
   * Находит кратчайший путь между двумя вершинами алгоритмом Дийкстры.
   *
   * @param graph   граф
   * @param vertex1 начальная вершина (нумерация с 1)
   * @param vertex2 конечная вершина (нумерация с 1)
   * @return кратчайшее расстояние между vertex1 и vertex2
   * @throws IllegalArgumentException если хотя бы одна вершина вне диапазона
   */
  public static int getShortestPathBetweenVertices(Graph graph, int vertex1, int vertex2) {
    int vertexCount = graph.getVertexCount();
    validateVertex(vertex1, vertexCount);
    validateVertex(vertex2, vertexCount);

    if (vertex1 == vertex2) {
      return 0;
    }

    int start = vertex1 - 1;
    int end = vertex2 - 1;
    int[][] matrix = graph.getAdjacencyMatrix();

    int[] distances = new int[vertexCount];
    boolean[] visited = new boolean[vertexCount];

    // Инициализируем расстояния: INF для всех вершин, 0 для стартовой.
    for (int i = 0; i < vertexCount; i++) {
      distances[i] = Integer.MAX_VALUE;
    }
    distances[start] = 0;

    for (int i = 0; i < vertexCount; i++) {
      // Находим непосещенную вершину с минимальным расстоянием.
      int minDist = Integer.MAX_VALUE;
      int current = -1;
      for (int v = 0; v < vertexCount; v++) {
        if (!visited[v] && distances[v] < minDist) {
          minDist = distances[v];
          current = v;
        }
      }

      // Выходим если оставшиеся вершины недостижимы или конечная вершина уже найдена.
      if (current == -1 || current == end) {
        break;
      }

      visited[current] = true;

      // Релаксация (улучшение) расстояний до соседей
      for (int v = 0; v < vertexCount; v++) {
        if (matrix[current][v] != 0 && !visited[v]) {
          int newDist = distances[current] + matrix[current][v];
          // Проверяем переполнение.
          if (distances[current] != Integer.MAX_VALUE && newDist > 0 && newDist < distances[v]) {
            distances[v] = newDist;
          }
        }
      }
    }

    return distances[end];
  }

  /**
   * Находит кратчайшие пути между всеми парами вершин алгоритмом Флойда-Уоршелла.
   *
   * @param graph граф
   * @return матрица кратчайших расстояний между всеми вершинами
   */
  public static int[][] getShortestPathsBetweenAllVertices(Graph graph) {
    int vertexCount = graph.getVertexCount();
    int[][] matrix = graph.getAdjacencyMatrix();

    // Инициализируем матрицу расстояний.
    int[][] dist = new int[vertexCount][vertexCount];
    for (int i = 0; i < vertexCount; i++) {
      for (int j = 0; j < vertexCount; j++) {
        if (i == j) {
          dist[i][j] = 0;
        } else if (matrix[i][j] != 0) {
          dist[i][j] = matrix[i][j];
        } else {
          dist[i][j] = Integer.MAX_VALUE / 2; // "бесконечность"
        }
      }
    }

    // Алгоритм Флойда-Уоршелла.
    for (int k = 0; k < vertexCount; k++) {
      for (int i = 0; i < vertexCount; i++) {
        for (int j = 0; j < vertexCount; j++) {
          if (dist[i][k] != Integer.MAX_VALUE / 2 && dist[k][j] != Integer.MAX_VALUE / 2) {
            int newDist = dist[i][k] + dist[k][j];
            if (newDist < dist[i][j]) {
              dist[i][j] = newDist;
            }
          }
        }
      }
    }

    return dist;
  }

  /**
   * Находит минимальное остовное дерево алгоритмом Прима.
   *
   * @param graph граф (должен быть связным и неориентированным)
   * @return матрица смежности минимального остовного дерева
   * @throws IllegalArgumentException если граф ориентированный
   */
  public static int[][] getLeastSpanningTree(Graph graph) {
    int vertexCount = graph.getVertexCount();
    int[][] matrix = graph.getAdjacencyMatrix();

    if (graph.isDirected()) {
      throw new IllegalArgumentException("Prim's algorithm requires an undirected graph");
    }

    int[][] mst = new int[vertexCount][vertexCount];

    boolean[] inMst = new boolean[vertexCount];
    int[] key = new int[vertexCount];
    int[] parent = new int[vertexCount];

    for (int i = 0; i < vertexCount; i++) {
      key[i] = Integer.MAX_VALUE;
      parent[i] = -1;
    }

    key[0] = 0;

    for (int count = 0; count < vertexCount - 1; count++) {
      // Находим вершину с минимальным ключом, еще не включенную в остов.
      int minKey = Integer.MAX_VALUE;
      int u = -1;
      for (int v = 0; v < vertexCount; v++) {
        if (!inMst[v] && key[v] < minKey) {
          minKey = key[v];
          u = v;
        }
      }

      if (u == -1) {
        break; // Граф может быть несвязным.
      }

      inMst[u] = true;

      // Обновляем ключи соседних вершин.
      for (int v = 0; v < vertexCount; v++) {
        if (matrix[u][v] != 0 && !inMst[v] && matrix[u][v] < key[v]) {
          parent[v] = u;
          key[v] = matrix[u][v];
        }
      }
    }

    // Строим матрицу смежности остова по массиву родителей.
    for (int v = 1; v < vertexCount; v++) {
      if (parent[v] != -1) {
        int u = parent[v];
        int weight = matrix[u][v];
        mst[u][v] = weight;
        mst[v][u] = weight; // Неориентированное ребро.
      }
    }

    return mst;
  }

  /**
   * Решает задачу коммивояжера с помощью муравьиного алгоритма.
   *
   * @param graph граф
   * @return TsmResult с найденным маршрутом и его длиной
   * @throws IllegalStateException если решение не удалось найти
   */
  public static TsmResult solveTravelingSalesmanProblem(Graph graph) {
    int vertexCount = graph.getVertexCount();
    int[][] matrix = graph.getAdjacencyMatrix();

    if (vertexCount < 2) {
      throw new IllegalStateException("Graph must have at least 2 vertices for TSP");
    }

    // Параметры муравьиного алгоритма.
    int antCount = Math.max(vertexCount, 10);
    int iterations = 100;
    double alpha = 1.0; // Влияние феромона.
    double beta = 2.0; // Влияние расстояния (эвристика).
    double evaporationRate = 0.5;
    double initialPheromone = 1.0 / vertexCount;

    // Инициализируем матрицу феромонов.
    double[][] pheromone = new double[vertexCount][vertexCount];
    for (int i = 0; i < vertexCount; i++) {
      for (int j = 0; j < vertexCount; j++) {
        if (i != j && matrix[i][j] != 0) {
          pheromone[i][j] = initialPheromone;
        }
      }
    }

    int[] bestTour = null;
    double bestDistance = Double.MAX_VALUE;
    int noImprovementCount = 0;
    int maxNoImprovement = 20;

    for (int iter = 0; iter < iterations; iter++) {
      // Каждый муравей строит маршрут.
      int[][] antTours = new int[antCount][vertexCount];
      double[] antDistances = new double[antCount];

      for (int ant = 0; ant < antCount; ant++) {
        boolean[] visited = new boolean[vertexCount];
        int startCity = ant % vertexCount; // Распределяем муравьев по стартовым городам.
        antTours[ant][0] = startCity;
        visited[startCity] = true;

        for (int step = 1; step < vertexCount; step++) {
          int current = antTours[ant][step - 1];
          int next = selectNextCity(current, visited, pheromone, matrix, alpha, beta);
          if (next == -1) {
            // Невозможно завершить маршрут.
            antDistances[ant] = Double.MAX_VALUE;
            break;
          }
          antTours[ant][step] = next;
          visited[next] = true;
          antDistances[ant] += matrix[current][next];
        }

        // Возвращаемся в стартовый город.
        if (antDistances[ant] != Double.MAX_VALUE) {
          int last = antTours[ant][vertexCount - 1];
          int first = antTours[ant][0];
          if (matrix[last][first] != 0) {
            antDistances[ant] += matrix[last][first];
          } else {
            antDistances[ant] = Double.MAX_VALUE;
          }
        }

        // Обновляем лучший маршрут.
        if (antDistances[ant] < bestDistance) {
          bestDistance = antDistances[ant];
          bestTour = antTours[ant].clone();
          noImprovementCount = 0;
        }
      }

      // Испаряем феромон.
      for (int i = 0; i < vertexCount; i++) {
        for (int j = 0; j < vertexCount; j++) {
          pheromone[i][j] *= (1 - evaporationRate);
        }
      }

      // Откладываем феромон.
      for (int ant = 0; ant < antCount; ant++) {
        if (antDistances[ant] == Double.MAX_VALUE) {
          continue;
        }
        double contribution = 1.0 / antDistances[ant];
        for (int step = 0; step < vertexCount - 1; step++) {
          int from = antTours[ant][step];
          int to = antTours[ant][step + 1];
          pheromone[from][to] += contribution;
          pheromone[to][from] += contribution; // Неориентированное ребро.
        }
        // Замыкаем маршрут.
        int last = antTours[ant][vertexCount - 1];
        int first = antTours[ant][0];
        if (matrix[last][first] != 0) {
          pheromone[last][first] += contribution;
          pheromone[first][last] += contribution;
        }
      }

      noImprovementCount++;
      if (noImprovementCount >= maxNoImprovement) {
        break; // Ранняя остановка.
      }
    }

    if (bestTour == null || bestDistance == Double.MAX_VALUE) {
      throw new IllegalStateException("Could not find a valid TSP tour for the given graph. "
          + "The graph may not contain a Hamiltonian cycle.");
    }

    // Переводим результат к нумерации с 1.
    int[] resultTour = new int[vertexCount + 1];
    for (int i = 0; i < vertexCount; i++) {
      resultTour[i] = bestTour[i] + 1;
    }
    resultTour[vertexCount] = bestTour[0] + 1; // Возвращаемся в стартовую вершину.

    return new TsmResult(resultTour, bestDistance);
  }

  /**
   * Выбирает следующий город для муравья по вероятностному правилу.
   *
   * @param current   текущий город (нумерация с 0)
   * @param visited   массив посещенных городов
   * @param pheromone матрица феромонов
   * @param distances матрица расстояний (смежности)
   * @param alpha     влияние феромона
   * @param beta      влияние расстояния
   * @return индекс следующего города или -1, если достижимого города нет
   */
  public static int selectNextCity(int current, boolean[] visited, double[][] pheromone,
      int[][] distances, double alpha, double beta) {
    int n = visited.length;
    double[] probabilities = new double[n];
    double sum = 0.0;

    for (int next = 0; next < n; next++) {
      if (!visited[next] && distances[current][next] != 0) {
        double pheromoneFactor = Math.pow(pheromone[current][next], alpha);
        double heuristicFactor = Math.pow(1.0 / distances[current][next], beta);
        probabilities[next] = pheromoneFactor * heuristicFactor;
        sum += probabilities[next];
      }
    }

    if (sum == 0.0) {
      return -1; // Нет достижимого непосещенного города.
    }

    // Выбор методом рулетки.
    double r = Math.random() * sum;
    double cumulative = 0.0;
    for (int next = 0; next < n; next++) {
      if (!visited[next] && distances[current][next] != 0) {
        cumulative += probabilities[next];
        if (r <= cumulative) {
          return next;
        }
      }
    }

    return -1;
  }

  /**
   * Проверяет, что индекс вершины находится в допустимом диапазоне.
   *
   * @param vertex      номер вершины (нумерация с 1)
   * @param vertexCount общее количество вершин
   * @throws IllegalArgumentException если вершина вне диапазона
   */
  private static void validateVertex(int vertex, int vertexCount) {
    if (vertex < 1 || vertex > vertexCount) {
      throw new IllegalArgumentException(
          "Vertex " + vertex + " is out of range [1, " + vertexCount + "]");
    }
  }

  /**
   * Обрезает массив результата до фактического количества посещенных вершин.
   *
   * @param result     массив результата
   * @param actualSize фактическое количество элементов
   * @return обрезанный массив
   */
  public static int[] trimResult(int[] result, int actualSize) {
    if (actualSize == result.length) {
      return result;
    }
    int[] trimmed = new int[actualSize];
    System.arraycopy(result, 0, trimmed, 0, actualSize);
    return trimmed;
  }
}
