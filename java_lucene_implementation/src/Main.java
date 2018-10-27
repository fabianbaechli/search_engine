import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
  public static final String QUERY_STRING = "SELECT p.page_title AS title, LOWER(CONVERT(t.old_text USING utf8)) AS content FROM page as p INNER JOIN text AS t ON p.page_latest = t.old_id WHERE p.page_is_redirect = 0 LIMIT 1000000";
  public static final String INDEX_LOCATION = "/Users/Fabian/Desktop/index";
  public static void main(String[] args) {
    StandardAnalyzer analyzer = new StandardAnalyzer();

    try {
      while (true) {
        Scanner scanner = new Scanner(System.in);
        String queryString = scanner.nextLine();
        Directory index = FSDirectory.open(Paths.get(INDEX_LOCATION));
        QueryDocuments query = new QueryDocuments(index);
        query.query(queryString);
      }
    } catch (IOException | ParseException e) {
      e.printStackTrace();
    }
  }
}