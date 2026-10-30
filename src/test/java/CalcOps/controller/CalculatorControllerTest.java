package CalcOps.controller;

import CalcOps.service.CalculatorService;
import CalcOps.exception.GlobalExceptionHandler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CalculatorController.class)
@Import(GlobalExceptionHandler.class)
class CalculatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CalculatorService calculatorService;

    @Test
    void testAdditionEndpoint() throws Exception {
        when(calculatorService.add(2.0, 3.0)).thenReturn(5.0);

        mockMvc.perform(get("/api/v1/add")
                        .param("a", "2")
                        .param("b", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("add"))
                .andExpect(jsonPath("$.result").value(5.0));
    }
    @Test
    void testSubtractionEndpoint() throws Exception {
        when(calculatorService.subtract(2.0, 3.0)).thenReturn(-1.0);

        mockMvc.perform(get("/api/v1/subtract")
                        .param("a", "2")
                        .param("b", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("subtract"))
                .andExpect(jsonPath("$.result").value(-1.0));
    }

    @Test
    void testMultiplicationEndpoint() throws Exception {
        when(calculatorService.multiply(2.0, 3.0)).thenReturn(6.0);

        mockMvc.perform(get("/api/v1/multiply")
                        .param("a", "2")
                        .param("b", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("multiply"))
                .andExpect(jsonPath("$.result").value(6.0));
    }

    @Test
    void testPowerEndpoint() throws Exception {
        when(calculatorService.power(2.0, 3.0)).thenReturn(8.0);

        mockMvc.perform(get("/api/v1/power")
                        .param("a", "2")
                        .param("b", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("power"))
                .andExpect(jsonPath("$.result").value(8.0));
    }

    @Test
    void testDivisionEndpoint() throws Exception {
        when(calculatorService.divide(6.0, 3.0)).thenReturn(2.0);

        mockMvc.perform(get("/api/v1/divide")
                        .param("a", "6")
                        .param("b", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("divide"))
                .andExpect(jsonPath("$.result").value(2.0));
    }

    @Test
    void testMissingParameter() throws Exception {
        mockMvc.perform(get("/api/v1/add")
                        .param("a", "2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("MISSING_PARAMETER"));
    }

    @Test
    void testInvalidParameter() throws Exception {
        mockMvc.perform(get("/api/v1/add")
                        .param("a", "abc")
                        .param("b", "3"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("INVALID_PARAMETER"));
    }

    @Test
    void testDivisionByZero() throws Exception {
        when(calculatorService.divide(8.0, 0.0))
                .thenThrow(new ArithmeticException("Cannot divide by zero"));

        mockMvc.perform(get("/api/v1/divide")
                        .param("a", "8")
                        .param("b", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("DIVISION_BY_ZERO"));
    }
}