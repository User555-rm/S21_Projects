package s21_graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Представляет граф в виде матрицы смежности.
 * Поддерживает загрузку из файла и экспорт в формата DOT.
 */
public class Graph {
  private int[][] adjacencyMatrix;
  private int vertexCount;
  private boolean isDirected;
  private boolean isWeighted;

  /** Создает пустой граф. */
  public Graph() {
    adjacencyMatrix = new int[0][0];
    vertexCount = 0;
    isDirected = false;
    isWeighted = false;
  }

  /**
   * Загружает граф из файла в формате матрицы смежности.
   *
   * Формат файла:
   * Строка 1: количество вершин N
   * Строки 2..N+1: N строк по N целых чисел, разделенных пробелами
   *
   * @param filename путь к файлу
   * @throws IOException              если файл не удалось прочитать
   * @throws IllegalArgumentException если формат файла некорректен или граф не связный
   */
  public void loadGraphFromFile(String filename) throws IOException {
    Path path = Paths.get(filename);
    if (!Files.exists(path)) {
      throw new IOException("File not found: " + filename);
    }
    if (!Files.isReadable(path)) {
      throw new IOException("File is not readable: " + filename);
    }

    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
      String firstLine = reader.readLine();
      if (firstLine == null || firstLine.trim().isEmpty()) {
        throw new IllegalArgumentException("File is empty");
      }

      int n;
      try {
        n = Integer.parseInt(firstLine.trim());
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException(
            "First line must contain the number of vertices, got: " + firstLine.trim());
      }

      if (n <= 0) {
        throw new IllegalArgumentException("Number of vertices must be positive, got: " + n);
      }

      int[][] matrix = new int[n][n];
      for (int i = 0; i < n; i++) {
        String line = reader.readLine();
        if (line == null) {
          throw new IllegalArgumentException(
              "Unexpected end of file. Expected " + n + " rows, got " + (i + 1));
        }
        String[] parts = line.trim().split("\\s+");
        if (parts.length != n) {
          throw new IllegalArgumentException(
              "Row " + (i + 1) + " has " + parts.length + " values, expected " + n);
        }
        for (int j = 0; j < n; j++) {
          int value;
          try {
            value = Integer.parseInt(parts[j]);
          } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                "Invalid value at row " + (i + 1) + ", column " + (j + 1) + ": " + parts[j]);
          }
          if (value < 0) {
            throw new IllegalArgumentException("Edge weights must be non-negative, got " + value
                + " at (" + (i + 1) + "," + (j + 1) + ")");
          }
          matrix[i][j] = value;
        }
      }

      // Проверка лишних данных после матрицы.
      String extraLine = reader.readLine();
      if (extraLine != null && !extraLine.trim().isEmpty()) {
        throw new IllegalArgumentException("Extra data found after the adjacency matrix");
      }

      this.adjacencyMatrix = matrix;
      this.vertexCount = n;
      determineGraphProperties();
      checkConnectivity();
    }
  }

  /**
   * Экспортирует граф в DOT-файл.
   *
   * @param filename путь к выходному DOT-файлу
   * @throws IOException если файл не удалось записать
   */
  public void exportGraphToDot(String filename) throws IOException {
    try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
      if (isDirected) {
        writer.println("digraph G {");
      } else {
        writer.println("graph G {");
      }

      if (vertexCount == 1 && adjacencyMatrix[0][0] == 0) {
        writer.println("    1;");
      }

      for (int i = 0; i < vertexCount; i++) {
        for (int j = (isDirected ? 0 : i); j < vertexCount; j++) {
          if (adjacencyMatrix[i][j] != 0) {
            String edgeOp = isDirected ? " -> " : " -- ";
            int vertex1 = i + 1; // Нумерация с 1.
            int vertex2 = j + 1;
            if (isWeighted) {
              writer.println(
                  "    " + vertex1 + edgeOp + vertex2 + " [weight=" + adjacencyMatrix[i][j] + "];");
            } else {
              writer.println("    " + vertex1 + edgeOp + vertex2 + ";");
            }
          }
        }
      }

      writer.println("}");
    }
  }

  /**
   * Возвращает матрицу смежности.
   *
   * @return копия матрицы смежности
   */
  public int[][] getAdjacencyMatrix() {
    int[][] copy = new int[vertexCount][vertexCount];
    for (int i = 0; i < vertexCount; i++) {
      System.arraycopy(adjacencyMatrix[i], 0, copy[i], 0, vertexCount);
    }
    return copy;
  }

  /**
   * Возвращает количество вершин.
   *
   * @return количество вершин
   */
  public int getVertexCount() {
    return vertexCount;
  }

  /**
   * Возвращает признак ориентированности графа.
   *
   * @return true, если граф ориентированный; false, если неориентированный
   */
  public boolean isDirected() {
    return isDirected;
  }

  /**
   * Возвращает признак взвешенности графа.
   *
   * @return true, если граф взвешенный; false, если невзвешенный
   */
  public boolean isWeighted() {
    return isWeighted;
  }

  /**
   * Определяет ориентированность и взвешенность графа по матрице смежности.
   */
  private void determineGraphProperties() {
    // Граф ориентированный, если матрица несимметрична.
    isDirected = false;
    for (int i = 0; i < vertexCount && !isDirected; i++) {
      for (int j = 0; j < vertexCount && !isDirected; j++) {
        if (adjacencyMatrix[i][j] != adjacencyMatrix[j][i]) {
          isDirected = true;
        }
      }
    }

    // Граф взвешенный, если есть хотя бы одно значение больше 1.
    isWeighted = false;
    for (int i = 0; i < vertexCount && !isWeighted; i++) {
      for (int j = 0; j < vertexCount && !isWeighted; j++) {
        if (adjacencyMatrix[i][j] > 1) {
          isWeighted = true;
        }
      }
    }
  }

  /**
   * Проверяет связность графа.
   * Для ориентированных графов проверяет слабую связность.
   *
   * @throws IllegalArgumentException если граф не связен
   */
  private void checkConnectivity() {
    if (vertexCount == 0) {
      return;
    }

    boolean[] visited = new boolean[vertexCount];
    Deque<Integer> stack = new ArrayDeque<>();
    stack.push(0);
    visited[0] = true;

    while (!stack.isEmpty()) {
      int v = stack.pop();
      for (int u = 0; u < vertexCount; u++) {
        boolean hasEdge = adjacencyMatrix[v][u] != 0 || adjacencyMatrix[u][v] != 0;
        if (hasEdge && !visited[u]) {
          visited[u] = true;
          stack.push(u);
        }
      }
    }

    for (int i = 0; i < vertexCount; i++) {
      if (!visited[i]) {
        throw new IllegalArgumentException(
            "Graph is not connected. Vertex " + (i + 1) + " is unreachable.");
      }
    }
  }
}
