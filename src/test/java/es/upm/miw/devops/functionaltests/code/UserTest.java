package es.upm.miw.devops.functionaltests.code;

import es.upm.miw.devops.code.User;
import es.upm.miw.devops.code.Fraction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;
    private List<Fraction> fractions;

    @BeforeEach
    void setUp() {
        fractions = new ArrayList<>();
        fractions.add(new Fraction()); // Como Fraction está vacía
        user = new User("1", "Juan", "Pérez", fractions);
    }

    @Test
    void testEmptyConstructor() {
        User emptyUser = new User();
        assertNotNull(emptyUser.getFractions());
        assertTrue(emptyUser.getFractions().isEmpty());
    }

    @Test
    void testGettersAndSetters() {
        assertEquals("1", user.getId());
        assertEquals("Juan", user.getName());
        assertEquals("Pérez", user.getFamilyName());

        user.setName("Carlos");
        user.setFamilyName("Gómez");

        assertEquals("Carlos", user.getName());
        assertEquals("Gómez", user.getFamilyName());
    }

    @Test
    void testFractions() {
        assertEquals(1, user.getFractions().size());
        user.addFraction(new Fraction());
        assertEquals(2, user.getFractions().size());
    }

    @Test
    void testFullName() {
        assertEquals("Juan Pérez", user.fullName());
    }

    @Test
    void testInitials() {
        assertEquals("J.", user.initials());
    }

    @Test
    void testToString() {
        String result = user.toString();
        assertTrue(result.contains("Juan"));
        assertTrue(result.contains("Pérez"));
        assertTrue(result.contains("fractions"));
    }
}
