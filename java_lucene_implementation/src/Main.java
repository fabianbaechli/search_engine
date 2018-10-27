import java.io.IOException;
import java.sql.*;
import java.text.ParseException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class Main {
  public static void main(String[] args) throws IOException, org.apache.lucene.queryparser.classic.ParseException {
    StandardAnalyzer analyzer = new StandardAnalyzer();
    /*
    // 1. create the index
    Directory index = new RAMDirectory();

    IndexWriterConfig config = new IndexWriterConfig(analyzer);

    IndexWriter w = new IndexWriter(index, config);
    addDoc(w, "Lucene in Action", "193398817");
    addDoc(w, "Lucene for Dummies", "55320055Z");
    addDoc(w, "Managing Gigabytes", "55063554A");
    addDoc(w, "The Art of Computer Science", "9900333X");
    w.close();

    // 2. query
    String querystr = args.length > 0 ? args[0] : "hello";

    // the "title" arg specifies the default field to use
    // when no field is explicitly specified in the query.
//    Query q = new QueryParser("title", analyzer).parse(querystr);
    Query q = new QueryParser("value", analyzer).parse(querystr);

    // 3. search
    int hitsPerPage = 10;
    IndexReader reader = DirectoryReader.open(index);
    IndexSearcher searcher = new IndexSearcher(reader);
    TopDocs docs = searcher.search(q, hitsPerPage);
    ScoreDoc[] hits = docs.scoreDocs;

    // 4. display results
    System.out.println("Found " + hits.length + " hits.");
    for(int i=0;i<hits.length;++i) {
      int docId = hits[i].doc;
      Document d = searcher.doc(docId);
      System.out.println((i + 1) + ". " + d.get("isbn") + "\t" + d.get("title"));
    }

    // reader can only be closed when there
    // is no need to access the documents any more.
    reader.close();
    */
    String url = "jdbc:mysql://localhost:3306/wiki?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    String username = "java";
    String password = "password";

    System.out.println("Connecting database...");

    try (Connection connection = DriverManager.getConnection(url, username, password)) {
      System.out.println("Database connected!");
      Statement stmt = connection.createStatement() ;
      String query = "SELECT p.page_title AS title, LOWER(CONVERT(t.old_text USING utf8)) AS content FROM page as p INNER JOIN text AS t ON p.page_latest = t.old_id WHERE p.page_is_redirect = 0 LIMIT 10000";
      ResultSet rs = stmt.executeQuery(query) ;
      while(rs.next()) {
        /*
        Blob blob = rs.getBlob("old_text");
        String text = new String(blob.getBytes(1, (int)blob.length()));
        System.out.println(text);
        */
        String title = rs.getString("title");
        String content = rs.getString("content");
        title = title.replaceAll("_", " ");
        System.out.println(title);
      }
    } catch (SQLException e) {
      throw new IllegalStateException("Cannot connect the database!", e);
    }
  }

  private static void addDoc(IndexWriter w, String title, String isbn) throws IOException {
    Document doc = new Document();
    doc.add(new TextField("title", title, Field.Store.YES));
    doc.add(new TextField("value", "hello", Field.Store.YES));

    // use a string field for isbn because we don't want it tokenized
    doc.add(new StringField("isbn", isbn, Field.Store.YES));
    w.addDocument(doc);
  }
}