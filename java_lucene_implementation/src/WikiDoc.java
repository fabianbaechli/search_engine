import java.io.Serializable;
import java.util.HashMap;

public class WikiDoc implements Serializable {
  private String header;
  private String content;
  private HashMap<String, Integer> countedWords;
  private double idfTf = 0;

  public WikiDoc(String header, String content, HashMap<String, Integer> countedWords) {
    this.header = header;
    this.content = content;
    this.countedWords = countedWords;
  }

  public String getHeader() {
    return header;
  }

  public String getContent() {
    return content;
  }

  public void setHeader(String header) {
    this.header = header;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public void setCountedWords(HashMap<String, Integer> countedWords) {
    this.countedWords = countedWords;
  }

  public HashMap<String, Integer> getCountedWords() {
    return countedWords;
  }

  public void setIdfTf(double idfTf) {
    this.idfTf = idfTf;
  }

  public double getIdfTf() {
    return idfTf;
  }

  @Override
  public String toString() {
    return "WikiDoc{" +
            "header='" + header + '\'' +
            ", content='" + content + '\'' +
            ", countedWords=" + countedWords +
            ", idfTf=" + idfTf +
            '}';
  }
}
