import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;

import java.io.IOException;

public class QueryDocuments {
  private Directory index;

  public QueryDocuments(Directory index) {
    this.index = index;
  }

  public void query(String queryString) throws IOException, ParseException {
    StandardAnalyzer analyzer = new StandardAnalyzer();

    // the "title" arg specifies the default field to use
    // when no field is explicitly specified in the query.
    Query q = new QueryParser("title", analyzer).parse(queryString);

    // 3. search
    int hitsPerPage = 1000000;
    IndexReader reader = DirectoryReader.open(index);
    IndexSearcher searcher = new IndexSearcher(reader);
    TopDocs docs = searcher.search(q, hitsPerPage);
    ScoreDoc[] hits = docs.scoreDocs;

    // 4. display results
    System.out.println("Found " + hits.length + " hits.");
    for (int i = 0; i < hits.length; ++i) {
      int docId = hits[i].doc;
      Document d = searcher.doc(docId);
//      System.out.println((i + 1) + ". " + d.get("content") + "\t" + d.get("title"));
      System.out.println((i + 1) + ". " + d.get("title"));
    }

    // reader can only be closed when there
    // is no need to access the documents any more.
    reader.close();

  }
}
