import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class QueryDocuments {
  private HashMap<String, ReverseIndex> loadedReverseIndex;

  public QueryDocuments() {
    try {
      FileInputStream fi = new FileInputStream(new File(IndexDocuments.REVERSE_INDEX_FILE_LOCATION));
      ObjectInputStream oi = new ObjectInputStream(fi);
      this.loadedReverseIndex = (HashMap) oi.readObject();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void search(String searchQuery) {
    searchQuery = IndexDocuments.normalizeString(searchQuery);
    HashMap<String, ReverseIndex> reverseIndices = new HashMap<>();
    for (String word : searchQuery.split("-")) {
      ReverseIndex reverseIndex = loadedReverseIndex.get(searchQuery);
      reverseIndices.put(word, reverseIndex);
    }
    HashMap<String, Double> sortedResults = new HashMap<>();
    reverseIndices.forEach((key, value) -> {
      if (value != null) {
        System.out.println("the term: " + key + " was found in: " + value.getDocuments().size() + " documents");
        value.getDocuments().forEach(doc -> {
//          System.out.println(doc.getHeader() + " : " + doc.getCountedWords().get(key) * value.getIdf());
          sortedResults.put(doc.getHeader(), doc.getCountedWords().get(key) * value.getIdf());
        });
      } else {
        System.out.println("The term: " + key + " was not found");
      }
    });
    Object[] a = sortedResults.entrySet().toArray();
    Arrays.sort(a, new Comparator() {
      public int compare(Object o1, Object o2) {
        return ((Map.Entry<String, Double>) o2).getValue()
                .compareTo(((Map.Entry<String, Double>) o1).getValue());
      }
    });
    for (Object e : a) {
      System.out.println(((Map.Entry<String, Double>) e).getKey() + " : "
              + ((Map.Entry<String, Double>) e).getValue());
    }

  }
}
