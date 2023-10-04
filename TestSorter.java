package prog08;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;

public class TestSorter<E extends Comparable<E>> {
  public static void main (String[] args) {
   //tests(new InsertionSort<Integer>());
    tests(new HeapSort<Integer>());
    //tests(new QuickSort<Integer>());
     //tests(new MergeSort<Integer>());
  }

  public static void tests (Sorter<Integer> sorter) {

    test(sorter, 10);
    test(sorter, 10000);
    test(sorter, 100000);
    test(sorter, 1000000);
    test(sorter, 10000000);
  }
  
  public static void test (Sorter<Integer> sorter, int n) {
    if (sorter instanceof InsertionSort && n > 100)
      n /= 100;

    Integer[] array = new Integer[n];
    Random random = new Random(0);
    for (int i = 0; i < n; i++)
      array[i] = random.nextInt(n);

    TestSorter<Integer> tester = new TestSorter<Integer>();
    tester.test(sorter, array);
  }

  public void test (Sorter<E> sorter, E[] array) {
    System.out.println(sorter + " on array of length " + array.length);

    if (inOrder(array))
      System.out.println("array is already sorted!");

    E[] copy = array.clone();
    long time1 = System.nanoTime();
    sorter.sort(copy);
    long time2 = System.nanoTime();
    double time = (time2-time1)/1000.0;

    double constant = time/sorter.O(array.length);
    System.out.println((time2-time1)/1000.0 + " microseconds, constant: " + constant);

    if (!sameElements(array, copy))
      System.out.println("sorted array does not have the same elements!");

    if (!inOrder(copy))
      System.out.println("sorted array is not sorted");

    if (array.length < 100) {
      print(array);
      print(copy);
    }
  }

  public void print (E[] array) {
    String s = "";
    for (E e : array)
      s += e + " ";
    System.out.println(s);
  }

  /** Check if array is nondecreasing. */
  public boolean inOrder (E[] array) {
    // EXERCISE

    for (int i = array.length-1; i > 0; --i){
      if (array[i].compareTo(array[i - 1])  < 0) {
        return false;
      }
    }



    return true;
  }
 
  /* Check if arrays have the same elements. */
  public boolean sameElements (E[] array1, E[] array2) {
    // EXERCISE
    // If the two arrays have different lengths, return false.
    if(array1.length != array2.length) {
      return false;
    }
    
    // Create two Map from E to Integer, using the HashMap implementation.
    HashMap<E,Integer> map1 = new HashMap<>();
    HashMap<E, Integer> map2 = new HashMap<>();

    // For each element of the first array, if it is not a key in the
    // first map, make it map to 1.  If it is already a key, increment
    // the integer it maps to.
    for (E item : array1) {
      if (!map1.containsKey(item)) {
        map1.put(item, 1);
      }
    else {
        int integer = map1.get(item);
        map1.replace(item, ++integer);
      }
    }

    // Ditto the second array and second map.
    for (E item : array2) {
      if (!map2.containsKey(item)) {
        map2.put(item, 1);
      }
      else {
        int integer = map2.get(item);
        map2.replace(item, ++integer);
      }
    }
    // For each element of the second array, check that it maps to the
    // same integer in both maps.  If not, return false.
    for (E item : array2) {
      if (!map1.get(item).equals(map2.get(item)))  {
        return false;
      }
    }


    return true;
  }
}

class InsertionSort<E extends Comparable<E>> implements Sorter<E> {
  public double O (int n) { return n*n; }

  public void sort (E[] array) {
    for (int n = 0; n < array.length; n++) {
      E data = array[n];
      int i = n;

      // EXERCISE
      // while array[i-1] > data move array[i-1] to array[i] and
      // decrement i

      while (i != 0 && array[i-1].compareTo(data) > 0) {
        array[i] = array[i-1];
        --i;
        }
      array[i] = data;
      }

    }
  }


class HeapSort<E extends Comparable<E>>
  implements Sorter<E> {

  public double O (int n) { return n*Math.log(n); }

  private E[] array;
  private int size;

  public void sort (E[] array) {
    this.array = array;
    this.size = array.length;

    for (int i = parent(array.length - 1); i >= 0; i--)
      swapDown(i);

    while (size > 1) {
      swap(0, size-1);
      size--;
      swapDown(0);
    }
  }

  public void swapDown (int index) {
    // EXERCISE

    // While the element at index is smaller than one of its children,
    // swap it with its larger child.  Use the helper methods provided
    // below: compare, left, right, and isValid.
    while (isValid(right(index))) {
      if (array[index].compareTo(array[2 * index + 1]) < 0 || array[index].compareTo(array[index * 2 + 2]) < 0) {
        if (array[index * 2 + 2].compareTo(array[index * 2 + 1]) < 0) {
          index = swap(index, left(index));
        } else {
          index = swap(index, right(index));
        }
      }
      else {
        break;
      }
    }
        while (isValid(left(index))) {
          if (array[index].compareTo(array[2 * index + 1]) < 0) {
          index = swap(index, left(index));
        }
          else {
            break;
          }
      }
    }

  // index = swap(index, left(index)) or
  // index = swap(index, right(index))
  private int swap (int i, int j) {
    E data = array[i];
    array[i] = array[j];
    array[j] = data;
    return j;
  }

  private int compare (int i, int j) { return array[i].compareTo(array[j]); }
  private int left (int i) { return 2 * i + 1; }
  private int right (int i) { return 2 * i + 2; }
  private int parent (int i) { return (i - 1) / 2; }
  private boolean isValid (int i) { return 0 <= i && i < size; }
}

class QuickSort<E extends Comparable<E>>
  implements Sorter<E> {

  public double O (int n) { return n*Math.log(n); }

  private E[] array;

  private InsertionSort<E> insertionSort = new InsertionSort<E>();

  private void swap (int i, int j) {
    E data = array[i];
    array[i] = array[j];
    array[j] = data;
  }

  public void sort (E[] array) {
    this.array = array;
    sort(0, array.length-1);
  }

  private void sort (int first, int last) {
    if (first >= last)
      return;

    swap(first, (first + last) / 2);
    E pivot = array[first];

    int lo = first + 1;
    int hi = last;
    while (lo <= hi) {
      // EXERCISE
      if (array[lo].compareTo(pivot) <= 0) {
        lo++;
      } else if (array[hi].compareTo(pivot) > 0) {
        hi--;
      } else{
        swap(lo,hi);
        lo++;
        hi--;
      }

      // Move lo forward if array[lo] <= pivot
      // Otherwise move hi backward if array[hi] > pivot
      // Otherwise swap array[lo] and array[hi] and move both lo and hi.
    }

    swap(first, hi);

    sort(first, hi-1);
    sort(hi+1, last);
  }
}

class MergeSort<E extends Comparable<E>>
  implements Sorter<E> {

  public double O (int n) { return n*Math.log(n); }

  private E[] array, array2;

  public void sort (E[] array) {
    this.array = array;
    array2 = array.clone();
    sort(0, array.length-1);
  }

  private void sort(int first, int last) {
    if (first >= last)
      return;

    int middle = (first + last) / 2;
    sort(first, middle);
    sort(middle+1, last);

    int i = first;
    int a = first;
    int b = middle+1;
    while (a <= middle && b <= last) {
      // EXERCISE

      if (array[a].compareTo(array[b]) <= 0) {
        array2[i] = array[a];
        i++;
        a++;
      } else {
        array2[i] = array[b];
        i++;
        b++;

      }
      // Copy the smaller of array[a] or array[b] to array2[i]
      // (in the case of a tie, copy array[a] to keep it stable)
      // and increment i and a or b (the one you copied).

    }

    while(a <= middle) {
      array2[i] = array[a];
      a++;
      i++;
    }

    while (b <= last) {
      array2[i] = array[b];
      ++b;
      i++;
    }
    // Copy the rest of a or b, whichever is not at the end.

    System.arraycopy(array2, first, array, first, last - first + 1);
  }
}
