package CalcOps.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorServiceTest {

    private CalculatorService calculatorService;

    @BeforeEach
    void setUp() {
        calculatorService = new CalculatorService();
    }

    @Test
    void testAddition() {
        assertEquals(5.0, calculatorService.add(2, 3), 0.0001);
    }

    @Test
    void testSubtraction() {
        assertEquals(5.0, calculatorService.subtract(8, 3), 0.0001);
    }

    @Test
    void testMultiplication() {
        assertEquals(12.0, calculatorService.multiply(4, 3), 0.0001);
    }

    @Test
    void testDivision() {
        assertEquals(4.0, calculatorService.divide(8, 2), 0.0001);
    }

    @Test
    void testDivisionByZero() {
        ArithmeticException exception = assertThrows(
                ArithmeticException.class,
                () -> calculatorService.divide(8, 0)
        );

        assertEquals("Cannot divide by zero", exception.getMessage());
    }

    @Test
    void testNegativeNumbers() {
        assertEquals(-6.0, calculatorService.multiply(-2, 3), 0.0001);
    }

    @Test
    void testDecimalNumbers() {
        assertEquals(4.0, calculatorService.add(2.5, 1.5), 0.0001);
    }
}