import java.util.Optional;

public class FillableCell extends Cell {
  private Pipe pipe;

  public FillableCell() {}
  public Optional<Pipe> getPipe() { return Optional.ofNullable(pipe); }
  public void setPipe(Pipe pipe) { this.pipe = pipe; }

  @Override
  public char toSingleChar() {
    return pipe == null ? '.' : pipe.toSingleChar();
  }
}
