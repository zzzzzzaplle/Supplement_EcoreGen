import java.util.UUID;

public class UniqueIdGenerator {
    public UniqueIdGenerator() {
    }

    public static String nextId() {
        return UUID.randomUUID().toString();
    }
}
