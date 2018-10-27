import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.sql.*;

public class IndexDocuments {
  public static final String JDBC_CONNECTION_STRING = "jdbc:mysql://localhost:3306/wiki?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
  public static final String JDBC_USERNAME = "java";
  public static final String JDBC_PASSWORD = "password";
  private Directory index;

  public IndexDocuments(Directory index) {
    this.index = index;
  }

  public void index(String queryString) {
    try {
      StandardAnalyzer analyzer = new StandardAnalyzer();
      IndexWriterConfig config = new IndexWriterConfig(analyzer);
      IndexWriter w = new IndexWriter(index, config);

      Connection connection = DriverManager.getConnection(JDBC_CONNECTION_STRING, JDBC_USERNAME, JDBC_PASSWORD);
      System.out.println("Database connected!");
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery(queryString);
      int count = 0;

      while (rs.next()) {
        try {
          String title = rs.getString("title");
          String content = rs.getString("content");
          title = title.replaceAll("_", " ");
          if (content != null) {
            addDoc(w, title, content);
            count++;
            if (count % 100000 == 0) {
              System.out.println(count);
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      System.out.println("Indexed the documents");
      w.close();
    } catch (Exception e) {
      throw new IllegalStateException("Cannot connect the database!", e);
    }
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
