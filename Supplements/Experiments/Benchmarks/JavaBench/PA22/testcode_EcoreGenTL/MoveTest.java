import edu.pa22.Pa22Factory;
import edu.pa22.Position;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(TestExtension.class)
class MoveTest {

    private final Position pos = positionOf(233, 233);

    @Tag(TestKind.PUBLIC)
    @Test
    void moveLeft() {
        assertEquals(
                positionOf(232, 233),
                Pa22Factory.eINSTANCE.createLeft().nextPosition(pos)
        );
    }

    @Tag(TestKind.HIDDEN)
    @Test
    void moveRight() {
        assertEquals(
                positionOf(234, 233),
                Pa22Factory.eINSTANCE.createRight().nextPosition(pos)
        );
    }

    @Tag(TestKind.HIDDEN)
    @Test
    void moveUp() {
        assertEquals(
                positionOf(233, 232),
                Pa22Factory.eINSTANCE.createUp().nextPosition(pos)
        );
    }

    @Tag(TestKind.HIDDEN)
    @Test
    void moveDown() {
        assertEquals(
                positionOf(233, 234),
                Pa22Factory.eINSTANCE.createDown().nextPosition(pos)
        );
    }

    private static Position positionOf(int x, int y) {
        Position p = Pa22Factory.eINSTANCE.createPosition();
        p.setX(x);
        p.setY(y);
        return p;
    }
}
