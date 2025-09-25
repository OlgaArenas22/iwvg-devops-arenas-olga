package es.upm.miw.devops.functionaltests.code;

import es.upm.miw.devops.code.Fraction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FractionTest {

    @Test
    void testConstructorAndGetters() {
        Fraction f = new Fraction(3, 5);
        assertEquals(3, f.getNumerator());
        assertEquals(5, f.getDenominator());
    }

    @Test
    void testDefaultConstructor() {
        Fraction f = new Fraction();
        assertEquals(1, f.getNumerator());
        assertEquals(1, f.getDenominator());
    }

    @Test
    void testSetters() {
        Fraction f = new Fraction(1, 2);
        f.setNumerator(7);
        f.setDenominator(9);
        assertEquals(7, f.getNumerator());
        assertEquals(9, f.getDenominator());
    }

    @Test
    void testDecimalPositive() {
        Fraction f = new Fraction(1, 4);
        assertEquals(0.25, f.decimal(), 1e-12);
    }

    @Test
    void testDecimalNegative() {
        Fraction f1 = new Fraction(-2, 5);
        Fraction f2 = new Fraction(2, -5);
        Fraction f3 = new Fraction(-2, -5);

        assertEquals(-0.4, f1.decimal(), 1e-12);
        assertEquals(-0.4, f2.decimal(), 1e-12);
        assertEquals(0.4, f3.decimal(), 1e-12);
    }

    @Test
    void testDecimalDivisionByZeroBehavior() {
        // Con double, en Java la división por 0 no lanza excepción: ±Infinity o NaN
        Fraction fInf = new Fraction(3, 0);
        Fraction fNegInf = new Fraction(-3, 0);
        Fraction fNaN = new Fraction(0, 0);

        assertTrue(Double.isInfinite(fInf.decimal()));
        assertTrue(Double.isInfinite(fNegInf.decimal()));
        assertTrue(Double.isNaN(fNaN.decimal()));
        assertEquals(Double.POSITIVE_INFINITY, fInf.decimal());
        assertEquals(Double.NEGATIVE_INFINITY, fNegInf.decimal());
    }

    @Test
    void testToString() {
        Fraction f = new Fraction(7, 3);
        assertEquals("Fraction{numerator=7, denominator=3}", f.toString());
    }
}
