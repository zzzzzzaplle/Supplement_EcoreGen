import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

public abstract class Document {
  private String title;

  public Document() {
  }

  public Document(String title) {
    this.title = title;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Document document = (Document) o;
    return title != null ? title.equals(document.title) : document.title == null;
  }

  @Override
  public int hashCode() {
    return title != null ? title.hashCode() : 0;
  }
}
