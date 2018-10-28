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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

public class QueryDocuments {
  private Directory index;
  private HashMap<String, ReverseIndex> loadedReverseIndex;

  public QueryDocuments(Directory index) {
    this.index = index;
    try {
      FileInputStream fi = new FileInputStream(new File(IndexDocuments.REVERSE_INDEX_FILE_LOCATION));
      ObjectInputStream oi = new ObjectInputStream(fi);
      this.loadedReverseIndex = (HashMap) oi.readObject();
    } catch (Exception e) {
      e.printStackTrace();
    }
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

  public void search(String searchQuery) {
    searchQuery = IndexDocuments.normalizeString(searchQuery);
    HashMap<String, ReverseIndex> reverseIndices = new HashMap<>();
    for (String word : searchQuery.split("-")) {
      ReverseIndex reverseIndex = loadedReverseIndex.get(searchQuery);
      reverseIndices.put(word, reverseIndex);
    }
    reverseIndices.forEach((key, value) -> {
      if (value != null) {
        System.out.println("the term: " + key + " was found in: " + value.getDocuments().size() + " documents");
        value.getDocuments().forEach(doc -> {
          System.out.println(((WikiDoc) doc).getHeader() + " : " + ((WikiDoc)doc).getIdfTf());
        });
      } else {
        System.out.println("The term: " + key + " was not found");
      }
    });
  }
}
