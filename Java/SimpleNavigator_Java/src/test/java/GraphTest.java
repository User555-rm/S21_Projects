import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import s21_graph.Graph;

/**
 * Модульные тесты для класса Graph.
 */
public class GraphTest {
  private Graph graph;

  @BeforeEach
  public void setUp() {
    graph = new Graph();
  }

  @Test
  public void testLoadGraphFromFile_validGraph() throws IOException {
    graph.loadGraphFromFile("../data-samples/graph3.txt");
    assertEquals(3, graph.getVertexCount());
    assertFalse(graph.isDirected());
    assertFalse(graph.isWeighted());
  }

  @Test
  public void testLoadGraphFromFile_validWeightedGraph() throws IOException {
    graph.loadGraphFromFile("../data-samples/graph11.txt");
    assertEquals(11, graph.getVertexCount());
    assertFalse(graph.isDirected());
    assertTrue(graph.isWeighted());
  }

  @Test
  public void testLoadGraphFromFile_fileNotFound() {
    assertThrows(IOException.class, () -> graph.loadGraphFromFile("nonexistent.txt"));
  }

  @Test
  public void testLoadGraphFromFile_emptyFile(@TempDir Path tempDir) throws IOException {
    Path emptyFile = tempDir.resolve("empty.txt");
    Files.createFile(emptyFile);
    assertThrows(
        IllegalArgumentException.class, () -> graph.loadGraphFromFile(emptyFile.toString()));
  }

  @Test
  public void testLoadGraphFromFile_invalidFirstLine(@TempDir Path tempDir) throws IOException {
    Path invalidFile = tempDir.resolve("invalid.txt");
    Files.writeString(invalidFile, "abc\n0 1\n1 0");
    assertThrows(
        IllegalArgumentException.class, () -> graph.loadGraphFromFile(invalidFile.toString()));
  }

  @Test
  public void testLoadGraphFromFile_negativeVertexCount(@TempDir Path tempDir) throws IOException {
    Path invalidFile = tempDir.resolve("negative.txt");
    Files.writeString(invalidFile, "-1\n0 1\n1 0");
    assertThrows(
        IllegalArgumentException.class, () -> graph.loadGraphFromFile(invalidFile.toString()));
  }

  @Test
  public void testLoadGraphFromFile_wrongMatrixSize(@TempDir Path tempDir) throws IOException {
    Path invalidFile = tempDir.resolve("wrongsize.txt");
    Files.writeString(invalidFile, "3\n0 1\n1 0 1\n1 1 0");
    assertThrows(
        IllegalArgumentException.class, () -> graph.loadGraphFromFile(invalidFile.toString()));
  }

  @Test
  public void testLoadGraphFromFile_negativeWeight(@TempDir Path tempDir) throws IOException {
    Path invalidFile = tempDir.resolve("negativeweight.txt");
    Files.writeString(invalidFile, "2\n0 -1\n-1 0");
    assertThrows(
        IllegalArgumentException.class, () -> graph.loadGraphFromFile(invalidFile.toString()));
  }

  @Test
  public void testLoadGraphFromFile_extraData(@TempDir Path tempDir) throws IOException {
    Path invalidFile = tempDir.resolve("extra.txt");
    Files.writeString(invalidFile, "2\n0 1\n1 0\nextra data");
    assertThrows(
        IllegalArgumentException.class, () -> graph.loadGraphFromFile(invalidFile.toString()));
  }

  @Test
  public void testExportGraphToDot_undirectedUnweighted(@TempDir Path tempDir) throws IOException {
    graph.loadGraphFromFile("../data-samples/graph3.txt");
    Path dotFile = tempDir.resolve("test.dot");
    graph.exportGraphToDot(dotFile.toString());

    String content = Files.readString(dotFile);
    assertTrue(content.contains("graph G {"));
    assertTrue(content.contains("1 -- 2"));
    assertTrue(content.contains("1 -- 3"));
    assertTrue(content.contains("2 -- 3"));
    assertFalse(content.contains("digraph"));
    assertFalse(content.contains("[weight="));
  }

  @Test
  public void testExportGraphToDot_undirectedWeighted(@TempDir Path tempDir) throws IOException {
    graph.loadGraphFromFile("../data-samples/graph11.txt");
    Path dotFile = tempDir.resolve("graph11.dot");
    graph.exportGraphToDot(dotFile.toString());

    String content = Files.readString(dotFile);
    assertTrue(content.contains("graph G {"));
    assertTrue(content.contains("[weight="));
  }

  @Test
  public void testExportGraphToDot_directedWeighted(@TempDir Path tempDir) throws IOException {
    // Создаем файл ориентированного графа: 2 вершины, несимметричная матрица.
    Path directedFile = tempDir.resolve("directed.txt");
    Files.writeString(directedFile, "2\n0 5\n3 0");
    graph.loadGraphFromFile(directedFile.toString());

    Path dotFile = tempDir.resolve("directed.dot");
    graph.exportGraphToDot(dotFile.toString());

    String content = Files.readString(dotFile);
    assertTrue(content.startsWith("digraph"), "Should be digraph, got: " + content);
    assertTrue(content.contains("1 -> 2 [weight=5]"), "Missing edge 1->2 with weight");
    assertTrue(content.contains("2 -> 1 [weight=3]"), "Missing edge 2->1 with weight");
    assertFalse(content.contains(" -- "), "Should not contain undirected edges");
  }

  @Test
  public void testExportGraphToDot_directedUnweighted(@TempDir Path tempDir) throws IOException {
    // Создаем ориентированный невзвешенный граф: цикл 1->2->3->1,
    // несимметричный и связный.
    Path directedFile = tempDir.resolve("directed_unweighted.txt");
    Files.writeString(directedFile, "3\n0 1 0\n0 0 1\n1 0 0");
    graph.loadGraphFromFile(directedFile.toString());

    Path dotFile = tempDir.resolve("directed_unweighted.dot");
    graph.exportGraphToDot(dotFile.toString());

    String content = Files.readString(dotFile);
    assertTrue(content.startsWith("digraph"), "Should be digraph, got: " + content);
    assertTrue(content.contains("1 -> 2;"), "Missing edge 1->2, got: " + content);
    assertTrue(content.contains("2 -> 3;"), "Missing edge 2->3, got: " + content);
    assertTrue(content.contains("3 -> 1;"), "Missing edge 3->1, got: " + content);
    assertFalse(content.contains("[weight="), "Should not contain weight for unweighted graph");
  }

  @Test
  public void testExportGraphToDot_withLoops(@TempDir Path tempDir) throws IOException {
    // Создаем граф с петлей.
    Path loopFile = tempDir.resolve("loop.txt");
    Files.writeString(loopFile, "2\n1 1\n1 0");
    graph.loadGraphFromFile(loopFile.toString());

    Path dotFile = tempDir.resolve("loop.dot");
    graph.exportGraphToDot(dotFile.toString());

    String content = Files.readString(dotFile);
    assertTrue(content.contains("1 -- 1")); // Петля.
    assertTrue(content.contains("1 -- 2"));
  }

  @Test
  public void testGetAdjacencyMatrix_returnsCopy() throws IOException {
    graph.loadGraphFromFile("../data-samples/graph3.txt");
    int[][] matrix = graph.getAdjacencyMatrix();
    matrix[0][0] = 999; // Изменяем копию.
    int[][] matrix2 = graph.getAdjacencyMatrix();
    assertEquals(0, matrix2[0][0]); // Оригинал должен остаться без изменений.
  }

  @Test
  public void testGetVertexCount_emptyGraph() {
    assertEquals(0, graph.getVertexCount());
  }

  @Test
  public void testIsDirected_undirectedGraph() throws IOException {
    graph.loadGraphFromFile("../data-samples/graph3.txt");
    assertFalse(graph.isDirected());
  }

  @Test
  public void testIsDirected_directedGraph(@TempDir Path tempDir) throws IOException {
    Path directedFile = tempDir.resolve("directed.txt");
    Files.writeString(directedFile, "2\n0 5\n3 0");
    graph.loadGraphFromFile(directedFile.toString());
    assertTrue(graph.isDirected());
  }

  @Test
  public void testLoadGraphFromFile_directedWeaklyConnected(@TempDir Path tempDir)
      throws IOException {
    Path directedFile = tempDir.resolve("weakly_connected.txt");
    Files.writeString(directedFile, "3\n0 0 0\n1 0 0\n0 1 0");

    graph.loadGraphFromFile(directedFile.toString());

    assertEquals(3, graph.getVertexCount());
    assertTrue(graph.isDirected());
  }

  @Test
  public void testIsWeighted_unweightedGraph() throws IOException {
    graph.loadGraphFromFile("../data-samples/graph3.txt");
    assertFalse(graph.isWeighted());
  }

  @Test
  public void testIsWeighted_weightedGraph() throws IOException {
    graph.loadGraphFromFile("../data-samples/graph11.txt");
    assertTrue(graph.isWeighted());
  }

  @Test
  public void testLoadGraphFromFile_zeroVertex(@TempDir Path tempDir) throws IOException {
    Path zeroFile = tempDir.resolve("zero.txt");
    Files.writeString(zeroFile, "0");
    assertThrows(
        IllegalArgumentException.class, () -> graph.loadGraphFromFile(zeroFile.toString()));
  }

  @Test
  public void testLoadGraphFromFile_nonNumericValue(@TempDir Path tempDir) throws IOException {
    Path badFile = tempDir.resolve("bad.txt");
    Files.writeString(badFile, "2\n0 abc\n1 0");
    assertThrows(IllegalArgumentException.class, () -> graph.loadGraphFromFile(badFile.toString()));
  }

  @Test
  public void testLoadGraphFromFile_unreadableFile(@TempDir Path tempDir) throws IOException {
    // Проверяем передачу директории вместо файла.
    assertThrows(IOException.class, () -> graph.loadGraphFromFile(tempDir.toString()));
  }
}
