package console;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;
import s21_graph.Graph;
import s21_graph_algorithms.GraphAlgorithms;
import s21_graph_algorithms.TsmResult;

/**
 * Консольное приложение для проверки библиотек SimpleNavigator.
 */
public class Main {
  private static final Scanner scanner = new Scanner(System.in);
  private static Graph graph = null;

  /**
   * Точка входа.
   *
   * @param args аргументы командной строки (не используются)
   */
  public static void main(String[] args) {
    System.out.println("=== Simple Navigator ===");

    boolean running = true;
    while (running) {
      printMenu();
      int choice;
      try {
        choice = readIntInput("Enter your choice: ");
      } catch (IllegalArgumentException e) {
        System.out.println("Error: " + e.getMessage());
        continue;
      }

      try {
        switch (choice) {
          case 1:
            loadGraph();
            break;
          case 2:
            breadthFirstSearch();
            break;
          case 3:
            depthFirstSearch();
            break;
          case 4:
            shortestPathBetweenVertices();
            break;
          case 5:
            shortestPathsBetweenAllVertices();
            break;
          case 6:
            leastSpanningTree();
            break;
          case 7:
            solveTravelingSalesman();
            break;
          case 8:
            exportGraphToDot();
            break;
          case 0:
            System.out.println("Exiting...");
            running = false;
            break;
          default:
            System.out.println("Invalid choice. Please enter a number from 0 to 8.");
        }
      } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
      }

      if (running && choice != 0) {
        System.out.println();
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
        scanner.nextLine(); // Считываем оставшийся перевод строки.
      }
    }

    scanner.close();
  }

  private static void printMenu() {
    System.out.println();
    System.out.println("1. Load graph from file");
    System.out.println("2. Breadth-first search (BFS)");
    System.out.println("3. Depth-first search (DFS)");
    System.out.println("4. Shortest path between two vertices (Dijkstra)");
    System.out.println("5. Shortest paths between all vertices (Floyd-Warshall)");
    System.out.println("6. Minimum spanning tree (Prim)");
    System.out.println("7. Traveling salesman problem (Ant Colony)");
    System.out.println("8. Export graph to DOT file");
    System.out.println("0. Exit");
  }

  private static void loadGraph() {
    System.out.print("Enter filename: ");
    String filename = scanner.next();

    graph = new Graph();
    try {
      graph.loadGraphFromFile(filename);
      System.out.println("Graph loaded successfully!");
      System.out.println("Vertices: " + graph.getVertexCount());
      System.out.println("Type: " + (graph.isDirected() ? "directed" : "undirected") + ", "
          + (graph.isWeighted() ? "weighted" : "unweighted"));
    } catch (IOException e) {
      System.out.println("Error reading file: " + e.getMessage());
      graph = null;
    } catch (IllegalArgumentException e) {
      System.out.println("Invalid graph data: " + e.getMessage());
      graph = null;
    }
  }

  private static void breadthFirstSearch() {
    checkGraphLoaded();
    int startVertex = readIntInput("Enter start vertex: ");
    int[] result = GraphAlgorithms.breadthFirstSearch(graph, startVertex);
    System.out.print("BFS result: ");
    printArray(result);
  }

  private static void depthFirstSearch() {
    checkGraphLoaded();
    int startVertex = readIntInput("Enter start vertex: ");
    int[] result = GraphAlgorithms.depthFirstSearch(graph, startVertex);
    System.out.print("DFS result: ");
    printArray(result);
  }

  private static void shortestPathBetweenVertices() {
    checkGraphLoaded();
    int v1 = readIntInput("Enter first vertex: ");
    int v2 = readIntInput("Enter second vertex: ");
    int distance = GraphAlgorithms.getShortestPathBetweenVertices(graph, v1, v2);
    if (distance == Integer.MAX_VALUE) {
      System.out.println("No path exists between vertices " + v1 + " and " + v2);
    } else {
      System.out.println("Shortest distance between " + v1 + " and " + v2 + ": " + distance);
    }
  }

  private static void shortestPathsBetweenAllVertices() {
    checkGraphLoaded();
    int[][] result = GraphAlgorithms.getShortestPathsBetweenAllVertices(graph);
    System.out.println("Shortest paths matrix:");
    printMatrix(result);
  }

  private static void leastSpanningTree() {
    checkGraphLoaded();
    int[][] mst = GraphAlgorithms.getLeastSpanningTree(graph);
    System.out.println("Minimum spanning tree adjacency matrix:");
    printMatrix(mst);
  }

  private static void solveTravelingSalesman() {
    checkGraphLoaded();
    TsmResult result = GraphAlgorithms.solveTravelingSalesmanProblem(graph);
    System.out.print("TSP route: ");
    printArray(result.vertices());
    System.out.println("Route length: " + result.distance());
  }

  private static void exportGraphToDot() throws IOException {
    checkGraphLoaded();
    System.out.print("Enter output DOT filename: ");
    String filename = scanner.next();
    graph.exportGraphToDot(filename);
    System.out.println("Graph exported successfully!");
  }

  private static void checkGraphLoaded() {
    if (graph == null) {
      throw new IllegalStateException("No graph loaded. Please load a graph first (option 1).");
    }
  }

  private static int readIntInput(String prompt) {
    System.out.print(prompt);
    try {
      return scanner.nextInt();
    } catch (InputMismatchException e) {
      scanner.next(); // Считываем некорректный ввод.
      throw new IllegalArgumentException("Invalid input. Please enter an integer.");
    }
  }

  private static void printArray(int[] array) {
    if (array == null || array.length == 0) {
      System.out.println("(empty)");
      return;
    }
    for (int i = 0; i < array.length; i++) {
      if (i > 0) {
        System.out.print(" ");
      }
      System.out.print(array[i]);
    }
    System.out.println();
  }

  private static void printMatrix(int[][] matrix) {
    if (matrix == null || matrix.length == 0) {
      System.out.println("(empty)");
      return;
    }
    for (int[] row : matrix) {
      for (int j = 0; j < row.length; j++) {
        if (j > 0) {
          System.out.print("\t");
        }
        if (row[j] == Integer.MAX_VALUE || row[j] == Integer.MAX_VALUE / 2) {
          System.out.print("INF");
        } else {
          System.out.print(row[j]);
        }
      }
      System.out.println();
    }
  }
}
