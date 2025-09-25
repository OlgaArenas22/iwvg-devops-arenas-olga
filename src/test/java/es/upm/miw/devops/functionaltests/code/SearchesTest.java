package es.upm.miw.devops.functionaltests.code;

import es.upm.miw.devops.code.Fraction;
import es.upm.miw.devops.code.Searches;
import es.upm.miw.devops.code.User;
import es.upm.miw.devops.code.UsersDatabase;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SearchesFindFractionAdditionByUserIdCasesTest {

    @Test
    void testAddition_userFound() {
        Searches searches = new Searches();
        Fraction result = searches.findFractionAdditionByUserId("4");
        assertNotNull(result);
        assertEquals(16, result.getNumerator());
        assertEquals(8, result.getDenominator());
    }

    @Test
    void testAddition_userNotFound_returnsNull() {
        Searches searches = new Searches();
        Fraction result = searches.findFractionAdditionByUserId("999");
        assertNull(result);
    }

    @Test
    void testAddition_nullId_throwsNPE() {
        Searches searches = new Searches();
        assertThrows(NullPointerException.class,
                () -> searches.findFractionAdditionByUserId(null));
    }
    static class TestUsersDatabaseEmptyFractions extends UsersDatabase {
        @Override
        public Stream<User> findAll() {
            return Stream.of(new User("E1", "Empty", "User", Collections.emptyList()));
        }
    }

    @Test
    void testAddition_userFound_butEmptyFractions_returnsNull() {
        Searches searches = new Searches() {
            @Override
            public Fraction findFractionAdditionByUserId(String id) {
                return new TestUsersDatabaseEmptyFractions().findAll()
                        .filter(user -> id.equals(user.getId()))
                        .flatMap(user -> user.getFractions().stream())
                        .filter(java.util.Objects::nonNull)
                        .reduce(Fraction::add)
                        .orElse(null);
            }
        };

        assertNull(searches.findFractionAdditionByUserId("E1"));
    }
    static class TestUsersDatabaseOnlyNullFractions extends UsersDatabase {
        @Override
        public Stream<User> findAll() {
            return Stream.of(new User("N1", "Nulls", "Only", Arrays.asList(null, null)));
        }
    }

    @Test
    void testAddition_userFound_onlyNullFractions_returnsNull() {
        Searches searches = new Searches() {
            @Override
            public Fraction findFractionAdditionByUserId(String id) {
                return new TestUsersDatabaseOnlyNullFractions().findAll()
                        .filter(user -> id.equals(user.getId()))
                        .flatMap(user -> user.getFractions().stream())
                        .filter(java.util.Objects::nonNull)
                        .reduce(Fraction::add)
                        .orElse(null);
            }
        };

        assertNull(searches.findFractionAdditionByUserId("N1"));
    }

    static class TestUsersDatabaseNullsAndValid extends UsersDatabase {
        @Override
        public Stream<User> findAll() {
            return Stream.of(new User("M1", "Mixed", "User",
                    Arrays.asList(null, new Fraction(1, 2), null, new Fraction(1, 3))));
        }
    }

    @Test
    void testAddition_userFound_nullsAreIgnored_andValidAreSummed() {
        Searches searches = new Searches() {
            @Override
            public Fraction findFractionAdditionByUserId(String id) {
                return new TestUsersDatabaseNullsAndValid().findAll()
                        .filter(user -> id.equals(user.getId()))
                        .flatMap(user -> user.getFractions().stream())
                        .filter(java.util.Objects::nonNull)
                        .reduce(Fraction::add)
                        .orElse(null);
            }
        };
        
        Fraction result = searches.findFractionAdditionByUserId("M1");
        assertNotNull(result);
        assertEquals(5, result.getNumerator());
        assertEquals(6, result.getDenominator());
    }
}
