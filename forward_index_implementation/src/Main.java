import java.util.Scanner;

public class Main {
  public static final String QUERY_STRING = "SELECT p.page_title AS title, LOWER(CONVERT(t.old_text USING utf8)) AS content FROM page as p INNER JOIN text AS t ON p.page_latest = t.old_id WHERE p.page_is_redirect = 0 AND LOWER(CONVERT(t.old_text USING utf8)) NOT LIKE '%begriffsklärung%' AND LOWER(CONVERT(t.old_text USING utf8)) NOT LIKE '%falschschreibung%' AND LOWER(CONVERT(t.old_text USING utf8)) NOT LIKE '%obsolete schreibung%' AND LOWER(CONVERT(t.old_text USING utf8)) LIKE '%[[kategorie:%' AND LOWER(CONVERT(t.old_text USING utf8)) LIKE '%=%' LIMIT 200000";

  public static void main(String[] args) {
    try {
//      IndexDocuments indexDocuments = new IndexDocuments();
//      indexDocuments.buildForwardIndex(QUERY_STRING);
//      indexDocuments.buildReverseIndex();
      QueryDocuments query = new QueryDocuments();
      System.out.println("index loaded");

      while (true) {
        Scanner scanner = new Scanner(System.in);
        String queryString = scanner.nextLine();
        query.search(queryString);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}