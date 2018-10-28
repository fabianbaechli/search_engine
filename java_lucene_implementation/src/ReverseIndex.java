import java.io.Serializable;
import java.util.ArrayList;

public class ReverseIndex implements Serializable {
  private double idf;
  private ArrayList<Object> documents;

  public ReverseIndex(double idf, Object firstDocument) {
    this.idf = idf;
    this.documents = new ArrayList<>();
    addDocument(firstDocument);
  }

  public double getIdf() {
    return idf;
  }

  public ArrayList<Object> getDocuments() {
    return documents;
  }

  public void setIdf(double idf) {
    this.idf = idf;
  }

  public void addDocument(Object doc) {
    if (!documents.contains(doc)) {
      documents.add(doc);
    }
  }

  @Override
  public String toString() {
    return "ReverseIndex{" +
            "idf=" + idf +
            ", documents=" + documents +
            '}';
  }
}
