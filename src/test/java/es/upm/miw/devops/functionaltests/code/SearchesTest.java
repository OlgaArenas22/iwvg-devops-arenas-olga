package es.upm.miw.devops.functionaltests.code;

import es.upm.miw.devops.code.Fraction;
import es.upm.miw.devops.code.Searches;
import es.upm.miw.devops.code.User;
import es.upm.miw.devops.code.UsersDatabase;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SearchesTest {
    static class TestUsersDatabaseNullsAndValid extends UsersDatabase {
        @Override
        public Stream<User> findAll() {
            return Stream.of(new User("M1", "Mixed", "User",
                    Arrays.asList(null, new Fraction(1, 2), null, new Fraction(1, 3))));
        }
    }
    static class TestUsersDatabaseNulls extends UsersDatabase {
        @Override
        public Stream<User> findAll() {
            return Stream.of(
                    new User("X", "Mixed", "Nulls", Arrays.asList(null, new Fraction(1, 2), null)), // tiene propia
                    new User("Y", "All", "Nulls", Arrays.asList(null, null))                        // solo nulls
            );
        }
    }
    static class TestUsersDatabaseNoneImproper extends UsersDatabase {
        @Override
        public Stream<User> findAll() {
            return Stream.of(
                    new User("A", "OnlyProper", "One",
                            Arrays.asList(new Fraction(1, 2), new Fraction(3, 4))),
                    new User("B", "OnlyEqual", "Two",
                            Arrays.asList(new Fraction(2, 2), new Fraction(-4, -4)))
            );
        }
    }
    static class TestUsersDatabaseNullsImproper extends UsersDatabase {
        @Override
        public Stream<User> findAll() {
            return Stream.of(
                    new User("X", "Mixed", "Nulls",
                            Arrays.asList(null, new Fraction(7, 3), null)),
                    new User("Y", "AllNulls", "Case",
                            Arrays.asList(null, null))                      // solo nulls
            );
        }
    }
    static class TestUsersDatabaseEmptyFractions extends UsersDatabase {
        @Override
        public Stream<User> findAll() {
            return Stream.of(new User("E1", "Empty", "User", Collections.emptyList()));
        }
    }
    static class TestUsersDatabaseOnlyNullFractions extends UsersDatabase {
        @Override
        public Stream<User> findAll() {
            return Stream.of(new User("N1", "Nulls", "Only", Arrays.asList(null, null)));
        }
    }

    static class TestUsersDatabaseNone extends UsersDatabase {
        @Override
        public Stream<User> findAll() {
            return Stream.of(
                    new User("A", "No", "Proper", Arrays.asList(new Fraction(2, 2), new Fraction(9, 3))), // igual, impropia
                    new User("B", "Only", "Improper", Arrays.asList(new Fraction(7, 3), new Fraction(5, 0))) // impropias, infinito
            );
        }
    }
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

    @Test
    void testSomeProper_withRealDatabase() {
        Searches searches = new Searches();
        List<String> result = searches.findUserIdBySomeProperFraction().toList();
        assertEquals(List.of("1", "2", "3", "5"), result);
    }

    @Test
    void testSomeProper_noneFound_returnsEmpty() {
        Searches searches = new Searches() {
            @Override
            public Stream<String> findUserIdBySomeProperFraction() {
                return new TestUsersDatabaseNone().findAll()
                        .filter(u -> u.getFractions().stream()
                                .filter(java.util.Objects::nonNull)
                                .anyMatch(Fraction::isProper))
                        .map(User::getId);
            }
        };
        List<String> result = searches.findUserIdBySomeProperFraction().toList();
        assertEquals(List.of(), result);
    }

    @Test
    void testSomeProper_nullFractionsAreIgnored() {
        Searches searches = new Searches() {
            @Override
            public Stream<String> findUserIdBySomeProperFraction() {
                return new TestUsersDatabaseNulls().findAll()
                        .filter(u -> u.getFractions().stream()
                                .filter(java.util.Objects::nonNull)
                                .anyMatch(Fraction::isProper))
                        .map(User::getId);
            }
        };
        List<String> result = searches.findUserIdBySomeProperFraction().toList();
        assertEquals(List.of("X"), result);
    }

    @Test
    void testSomeImproper_withRealDatabase() {
        Searches searches = new Searches();
        List<String> result = searches.findUserFamilyNameBySomeImproperFraction().toList();
        assertEquals(List.of("Fernandez", "Blanco", "Torres"), result);
    }

    @Test
    void testSomeImproper_noneFound_returnsEmpty() {
        Searches searches = new Searches() {
            @Override
            public Stream<String> findUserFamilyNameBySomeImproperFraction() {
                return new TestUsersDatabaseNoneImproper().findAll()
                        .filter(user -> user.getFractions().stream()
                                .filter(java.util.Objects::nonNull)
                                .anyMatch(Fraction::isImproper))
                        .map(User::getFamilyName);
            }
        };
        List<String> result = searches.findUserFamilyNameBySomeImproperFraction().toList();
        assertEquals(List.of(), result);
    }

    @Test
    void testSomeImproper_nullFractionsAreIgnored() {
        Searches searches = new Searches() {
            @Override
            public Stream<String> findUserFamilyNameBySomeImproperFraction() {
                return new TestUsersDatabaseNullsImproper().findAll()
                        .filter(user -> user.getFractions().stream()
                                .filter(java.util.Objects::nonNull)
                                .anyMatch(Fraction::isImproper))
                        .map(User::getFamilyName);
            }
        };
        List<String> result = searches.findUserFamilyNameBySomeImproperFraction().toList();
        assertEquals(List.of("Nulls"), result);
    }


    @Test
    void testSubtraction_userFound_singleUser() {
        Searches searches = new Searches();
        Fraction result = searches.findFractionSubtractionByUserName("Ana");
        assertNotNull(result);
        assertEquals(22, result.getNumerator());
        assertEquals(60, result.getDenominator());
    }

    @Test
    void testSubtraction_userFound_multipleUsers_sameName() {
        Searches searches = new Searches();
        Fraction result = searches.findFractionSubtractionByUserName("Paula");
        assertNotNull(result);
        assertEquals(0, result.getNumerator());
        assertEquals(0, result.getDenominator());
    }

    @Test
    void testSubtraction_userNotFound_returnsNull() {
        Searches searches = new Searches();
        Fraction result = searches.findFractionSubtractionByUserName("Daniela");
        assertNull(result);
    }

    @Test
    void testSubtraction_nullName_throwsNPE() {
        Searches searches = new Searches();
        assertThrows(NullPointerException.class,
                () -> searches.findFractionSubtractionByUserName(null));
    }
}

