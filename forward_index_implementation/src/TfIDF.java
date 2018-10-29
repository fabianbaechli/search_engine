import java.io.Serializable;

public class TfIDF implements Serializable {
  private Integer wordCount;
  private Double idf = 0.0;
  public TfIDF(Integer wordCount) {
    this.wordCount = wordCount;
  }

  public void setIdf(Double idf) {
    this.idf = idf;
  }

  public Integer getWordCount() {
    return wordCount;
  }

  public Double getTfIDF() {
    return idf * wordCount;
  }

  @Override
  public String toString() {
    return "TfIDF{" +
            "wordCount=" + wordCount +
            ", idf=" + idf +
            '}';
  }
}
