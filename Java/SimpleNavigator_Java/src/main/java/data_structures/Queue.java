package data_structures;

import java.util.NoSuchElementException;

/**
 * Обобщенная реализация очереди на основе динамического массива.
 *
 * @param <T> тип элементов, хранящихся в очереди
 */
public class Queue<T> {
  private static final int DEFAULT_CAPACITY = 10;
  private Object[] elements;
  private int head;
  private int tail;
  private int size;

  /** Создает пустую очередь. */
  public Queue() {
    elements = new Object[DEFAULT_CAPACITY];
    head = 0;
    tail = 0;
    size = 0;
  }

  /**
   * Добавляет элемент в конец очереди.
   *
   * @param value добавляемый элемент
   */
  public void push(T value) {
    ensureCapacity();
    elements[tail] = value;
    tail = (tail + 1) % elements.length;
    size++;
  }

  /**
   * Удаляет и возвращает первый элемент очереди.
   *
   * @return первый элемент
   * @throws NoSuchElementException если очередь пуста
   */
  @SuppressWarnings("unchecked")
  public T pop() {
    if (isEmpty()) {
      throw new NoSuchElementException("Queue is empty");
    }
    T value = (T) elements[head];
    elements[head] = null; // Помогает сборщику мусора.
    head = (head + 1) % elements.length;
    size--;
    return value;
  }

  /**
   * Возвращает первый элемент без удаления из очереди.
   *
   * @return первый элемент
   * @throws NoSuchElementException если очередь пуста
   */
  @SuppressWarnings("unchecked")
  public T front() {
    if (isEmpty()) {
      throw new NoSuchElementException("Queue is empty");
    }
    return (T) elements[head];
  }

  /**
   * Возвращает последний элемент без удаления из очереди.
   *
   * @return последний элемент
   * @throws NoSuchElementException если очередь пуста
   */
  @SuppressWarnings("unchecked")
  public T back() {
    if (isEmpty()) {
      throw new NoSuchElementException("Queue is empty");
    }
    int backIndex = (tail - 1 + elements.length) % elements.length;
    return (T) elements[backIndex];
  }

  /**
   * Проверяет, пуста ли очередь.
   *
   * @return true, если очередь пуста; иначе false
   */
  public boolean isEmpty() {
    return size == 0;
  }

  /**
   * Возвращает количество элементов в очереди.
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
      for (int i = 0; i < size; i++) {
        newElements[i] = elements[(head + i) % elements.length];
      }
      elements = newElements;
      head = 0;
      tail = size;
    }
  }
}
