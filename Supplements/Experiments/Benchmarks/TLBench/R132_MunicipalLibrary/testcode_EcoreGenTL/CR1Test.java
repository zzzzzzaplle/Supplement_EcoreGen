package edu.municipalLibrary.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import edu.municipalLibrary.Library;
import edu.municipalLibrary.Member;
import edu.municipalLibrary.MunicipalLibraryFactory;

public class CR1Test {

    private MunicipalLibraryFactory factory;

    @Before
    public void setUp() {
        factory = MunicipalLibraryFactory.eINSTANCE;
    }

    private Library createLibrary(String name) {
        Library library = factory.createLibrary();
        library.setName(name);
        return library;
    }

    @Test
    public void testCase1_UniqueFullName() {
        Library library = createLibrary("L1");

        boolean result = library.registerMember("Alice", "Brown");

        assertTrue(result);
        assertEquals(1, library.getMembers().size());
        Member member = library.getMembers().get(0);
        assertEquals("Alice", member.getFirstName());
        assertEquals("Brown", member.getSurname());
        assertNotNull(member.getBorrowRecords());
        assertTrue(member.getBorrowRecords().isEmpty());
    }

    @Test
    public void testCase2_ExactNameDuplicate() {
        Library library = createLibrary("L2");
        assertTrue(library.registerMember("Alice", "Brown"));

        boolean result = library.registerMember("Alice", "Brown");

        assertFalse(result);
        assertEquals(1, library.getMembers().size());
    }

    @Test
    public void testCase3_SameSurnameDifferentFirstName() {
        Library library = createLibrary("L3");
        assertTrue(library.registerMember("Alice", "Brown"));

        boolean result = library.registerMember("Bob", "Brown");

        assertTrue(result);
        assertEquals(2, library.getMembers().size());
    }

    @Test
    public void testCase4_SameFirstNameDifferentSurname() {
        Library library = createLibrary("L4");
        assertTrue(library.registerMember("Alice", "Brown"));

        boolean result = library.registerMember("Alice", "Green");

        assertTrue(result);
        assertEquals(2, library.getMembers().size());
    }

    @Test
    public void testCase5_NoFirstNameOrSurname() {
        Library library = createLibrary("L5");

        boolean nullSurnameResult = library.registerMember("Alice", null);
        boolean nullFirstNameResult = library.registerMember(null, "Brown");

        assertFalse(nullSurnameResult);
        assertFalse(nullFirstNameResult);
        assertTrue(library.getMembers().isEmpty());
    }
}
