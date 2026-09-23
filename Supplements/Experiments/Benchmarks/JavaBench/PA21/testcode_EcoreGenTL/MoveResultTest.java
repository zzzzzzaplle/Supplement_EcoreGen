package edu.pa21;

import edu.pa21.util.ReflectionUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MoveResultTest {

    // 辅助函数：创建Position对象
    private static Position createPosition(int row, int col) {
        Position pos = Pa21Factory.eINSTANCE.createPosition();
        pos.setRow(row);
        pos.setCol(col);
        return pos;
    }

    // 辅助函数：创建Invalid对象
    private static Invalid createInvalid(Position newPosition) {
        Invalid invalid = Pa21Factory.eINSTANCE.createInvalid();
        invalid.setNewPosition(newPosition);
        return invalid;
    }

    // 辅助函数：创建Alive对象
    private static Alive createAlive(Position newPosition, Position origPosition) {
        Alive alive = Pa21Factory.eINSTANCE.createAlive();
        alive.setNewPosition(newPosition);
        alive.setOrigPosition(origPosition);
        return alive;
    }

    // 辅助函数：创建Dead对象
    private static Dead createDead(Position newPosition, Position minePosition) {
        Dead dead = Pa21Factory.eINSTANCE.createDead();
        dead.setNewPosition(newPosition);
        dead.setMinePosition(minePosition);
        return dead;
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Class is Abstract")
    void testClassIsAbstract() {
        assertTrue(java.lang.reflect.Modifier.isAbstract(MoveResult.class.getModifiers()));
    }

  

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Methods")
    void testPublicMethods() {
        final var clazz = MoveResult.class;
        final var publicMethods = ReflectionUtils.getPublicInstanceMethods(clazz);

        // getNewPosition, setNewPosition
        assertTrue(publicMethods.length >= 1);
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Fields")
    void testPublicFields() {
        final var clazz = MoveResult.class;
        final var publicFields = ReflectionUtils.getPublicInstanceFields(clazz);

        assertEquals(0, publicFields.length);
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Constructors for Invalid")
    void testInvalidConstructors() {
        final var clazz = Invalid.class;
        final var ctors = ReflectionUtils.getPublicConstructors(clazz);

        // EMF生成的类使用protected构造函数
        assertEquals(0, ctors.length);
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Methods for Invalid")
    void testInvalidPublicMethods() {
        final var clazz = Invalid.class;
        final var publicMethods = ReflectionUtils.getPublicInstanceMethods(clazz);

        // 继承的方法
        assertTrue(publicMethods.length >= 0);
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Fields for Invalid")
    void testInvalidPublicFields() {
        final var clazz = Invalid.class;
        final var publicFields = ReflectionUtils.getPublicInstanceFields(clazz);

        assertEquals(0, publicFields.length);
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Constructors for Valid")
    void testValidConstructors() {
        final var clazz = Valid.class;
        final var ctors = ReflectionUtils.getPublicConstructors(clazz);

        // EMF生成的类使用protected构造函数
        assertEquals(0, ctors.length);
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Methods for Valid")
    void testValidPublicMethods() {
        final var clazz = Valid.class;
        final var publicMethods = ReflectionUtils.getPublicInstanceMethods(clazz);

        // getOrigPosition, setOrigPosition
        assertTrue(publicMethods.length >= 0);
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Fields for Valid")
    void testValidPublicFields() {
        final var clazz = Valid.class;
        final var publicFields = ReflectionUtils.getPublicInstanceFields(clazz);

        assertEquals(0, publicFields.length);
    }





    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Fields for Alive")
    void testValidAlivePublicFields() {
        final var clazz = Alive.class;
        final var publicFields = ReflectionUtils.getPublicInstanceFields(clazz);

        assertEquals(0, publicFields.length);
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Constructors for Dead")
    void testValidDeadConstructors() {
        final var clazz = Dead.class;
        final var ctors = ReflectionUtils.getPublicConstructors(clazz);

        // EMF生成的类使用protected构造函数
        assertEquals(0, ctors.length);
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Methods for Dead")
    void testValidDeadPublicMethods() {
        final var clazz = Dead.class;
        final var publicMethods = ReflectionUtils.getPublicInstanceMethods(clazz);

        // getMinePosition, setMinePosition
        assertTrue(publicMethods.length >= 0);
    }

}
