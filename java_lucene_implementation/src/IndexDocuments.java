import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;

import java.io.*;
import java.sql.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;

public class IndexDocuments {
  public static final String JDBC_CONNECTION_STRING = "jdbc:mysql://localhost:3306/wiki?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
  public static final String JDBC_USERNAME = "java";
  public static final String JDBC_PASSWORD = "password";
  public static final String FORWARD_INDEX_FILE_LOCATION = "/Users/Fabian/Desktop/selfmade_index/forward_index";
  public static final String REVERSE_INDEX_FILE_LOCATION = "/Users/Fabian/Desktop/selfmade_index/reverse_index";
  private Directory index;

  public IndexDocuments(Directory index) {
    this.index = index;
  }

  public void buildForwardIndex(String queryString) {
    try {
      StandardAnalyzer analyzer = new StandardAnalyzer();
      IndexWriterConfig config = new IndexWriterConfig(analyzer);
      IndexWriter w = new IndexWriter(index, config);

      Connection connection = DriverManager.getConnection(JDBC_CONNECTION_STRING, JDBC_USERNAME, JDBC_PASSWORD);
      System.out.println("Database connected!");
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery(queryString);
      ArrayList<WikiDoc> documents = new ArrayList<>();
      double count = 0;
      while (rs.next()) {
        try {
          HashMap<String, Integer> words = new HashMap<>();
          String title = rs.getString("title");
          String content = rs.getString("content");
          title = title.replaceAll("_", " ");

          // The term weighting for headers is 100
          words.put(title, 100);

          for (String line : content.split("\n")) {
            for (String word : line.split(" ")) {
              int wordOccurrences = words.getOrDefault(word, 0);
              words.put(word, wordOccurrences + 1);
            }
          }
          WikiDoc doc = new WikiDoc(title, content, words);
          documents.add(doc);
        } catch (Exception e) {
          e.printStackTrace();
        }
        count++;
        if (count % 100000 == 0) {
          System.out.println(count);
        }
      }
      w.close();
      FileOutputStream f = new FileOutputStream(new File(FORWARD_INDEX_FILE_LOCATION));
      ObjectOutputStream o = new ObjectOutputStream(f);
      o.writeObject(documents);
      o.close();
      f.close();
      System.out.println("Indexed the documents");
    } catch (Exception e) {
      throw new IllegalStateException("Cannot connect the database!", e);
    }
  }

  public void buildReverseIndex() {
    // Read objects
    try {
      FileInputStream fi = new FileInputStream(new File(FORWARD_INDEX_FILE_LOCATION));
      ObjectInputStream oi = new ObjectInputStream(fi);
      ArrayList documents = (ArrayList) oi.readObject();
      HashMap<String, ReverseIndex> reverseIndices = new HashMap<>();
      final int[] iterationCount = {0};
      System.out.println("read the forward index");
      documents.forEach(doc -> {
        HashMap<String, Integer> words = ((WikiDoc) doc).getCountedWords();
        words.forEach((word, count) -> {
          // if the term is seen for the first time
          word = normalizeString(word);
          ReverseIndex reverseIndex = reverseIndices.remove(word);
          if (reverseIndex == null) {
            // TODO save the real document
            ReverseIndex index = new ReverseIndex(Math.log10(documents.size()), doc);
            reverseIndices.put(word, index);
          } else {
            reverseIndex.setIdf(Math.log10(documents.size() / reverseIndex.getDocuments().size() + 1));
            ((WikiDoc) doc).setIdfTf(reverseIndex.getIdf() * count);
            reverseIndex.addDocument(doc);
            reverseIndices.put(word, reverseIndex);
          }
        });
        iterationCount[0]++;
        if (iterationCount[0] % 10000 == 0) {
          System.out.println(iterationCount[0]);
        }
      });
      FileOutputStream f = new FileOutputStream(new File(REVERSE_INDEX_FILE_LOCATION));
      ObjectOutputStream o = new ObjectOutputStream(f);
      o.writeObject(reverseIndices);
      o.close();
      f.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static String normalizeString(String input) {
    input = Normalizer.normalize(input, Normalizer.Form.NFD);
    input = input.toLowerCase();
    return input.replaceAll("\\s+", "-")
            .replaceAll("[^-a-zA-Z0-9]", "");
  }

  private static void addDoc(IndexWriter w, String title, String content) throws IOException {
    Document doc = new Document();
    doc.add(new TextField("title", title, Field.Store.YES));
    // use a string field for isbn because we don't want it tokenized
    String[] lines = content.split("\n");
    for (String line : lines) {
      if (line.contains("=")) {

      }
    }
    doc.add(new TextField("content", content, Field.Store.YES));
    w.addDocument(doc);
  }
}
