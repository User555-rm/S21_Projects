package data_structures;

import java.util.EmptyStackException;

/**
 * Обобщенная реализация стека на основе динамического массива.
 *
 * @param <T> тип элементов, хранящихся в стеке
 */
public class Stack<T> {
  private static final int DEFAULT_CAPACITY = 10;
  private Object[] elements;
  private int size;

  /** Создает пустой стек. */
  public Stack() {
    elements = new Object[DEFAULT_CAPACITY];
    size = 0;
  }

  /**
   * Добавляет элемент на вершину стека.
   *
   * @param value добавляемый элемент
   */
  public void push(T value) {
    ensureCapacity();
    elements[size++] = value;
  }

  /**
   * Удаляет и возвращает верхний элемент стека.
   *
   * @return верхний элемент
   * @throws EmptyStackException если стек пуст
   */
  @SuppressWarnings("unchecked")
  public T pop() {
    if (isEmpty()) {
      throw new EmptyStackException();
    }
    T value = (T) elements[--size];
    elements[size] = null; // Помогает сборщику мусора.
    return value;
  }

  /**
   * Возвращает верхний элемент без удаления из стека.
   *
   * @return верхний элемент
   * @throws EmptyStackException если стек пуст
   */
  @SuppressWarnings("unchecked")
  public T top() {
    if (isEmpty()) {
      throw new EmptyStackException();
    }
    return (T) elements[size - 1];
  }

  /**
   * Проверяет, пуст ли стек.
   *
   * @return true, если стек пуст; иначе false
   */
  public boolean isEmpty() {
    return size == 0;
  }

  /**
   * Возвращает количество элементов в стеке.
   *
   * @return количество элементов
   */
  public int size() {
    return size;
  }

  private void ensureCapacity() {
    if (size == elements.length) {
      int newCapacity = elements.length * 2;
      Object[] newElements = new Object[newCapacity];
      System.arraycopy(elements, 0, newElements, 0, size);
      elements = newElements;
    }
  }
}
