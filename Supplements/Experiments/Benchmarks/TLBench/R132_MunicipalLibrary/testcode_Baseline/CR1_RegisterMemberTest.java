import static org.junit.Assert.*;

import org.junit.Test;

public class CR1_RegisterMemberTest {
    private Member findMember(Library library, String firstName, String surname) {
        for (Member member : library.getMembers()) {
            if (firstName.equals(member.getFirstName()) && surname.equals(member.getSurname())) {
                return member;
            }
        }
        return null;
    }

    @Test
    public void tc1_UniqueFullName() {
        Library library = new Library();

        boolean result = library.registerMember("Alice", "Brown");

        assertTrue(result);
        Member member = findMember(library, "Alice", "Brown");
        assertNotNull(member);
        assertTrue(member.getBorrowRecords().isEmpty());
    }

    @Test
    public void tc2_ExactNameDuplicate() {
        Library library = new Library();
        assertTrue(library.registerMember("Alice", "Brown"));

        boolean result = library.registerMember("Alice", "Brown");

        assertFalse(result);
        assertEquals(1, library.getMembers().size());
    }

    @Test
    public void tc3_SameSurnameDifferentFirstName() {
        Library library = new Library();
        assertTrue(library.registerMember("Alice", "Brown"));

        boolean result = library.registerMember("Bob", "Brown");

        assertTrue(result);
        assertEquals(2, library.getMembers().size());
    }

    @Test
    public void tc4_SameFirstNameDifferentSurname() {
        Library library = new Library();
        assertTrue(library.registerMember("Alice", "Brown"));

        boolean result = library.registerMember("Alice", "Green");

        assertTrue(result);
        assertEquals(2, library.getMembers().size());
    }

    @Test
    public void tc5_NoFirstNameOrSurname() {
        Library library = new Library();

        assertFalse(library.registerMember("Alice", null));
        assertFalse(library.registerMember(null, "Brown"));
        assertFalse(library.registerMember("", "Brown"));
        assertFalse(library.registerMember("Alice", "   "));
        assertTrue(library.getMembers().isEmpty());
    }
}
