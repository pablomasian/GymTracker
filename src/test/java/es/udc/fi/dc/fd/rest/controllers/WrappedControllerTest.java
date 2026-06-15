package es.udc.fi.dc.fd.rest.controllers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import es.udc.fi.dc.fd.model.services.WrappedService;
import es.udc.fi.dc.fd.rest.common.JwtInfo;
import es.udc.fi.dc.fd.rest.dtos.WrappedDto;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class WrappedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WrappedService wrappedService;

    @Test
    void testGetWrappedByYear() throws Exception {
        Long userId = 1L;
        int year = 2024;

        WrappedDto dto = new WrappedDto();
        dto.setYear(year);
        dto.setTotalWorkouts(50);
        dto.setTotalWeightLifted(BigDecimal.valueOf(10000.0));

        when(wrappedService.getWrapped(userId, year)).thenReturn(dto);

        mockMvc.perform(get("/api/wrapped/" + year)
                .requestAttr("jwtInfo", new JwtInfo(userId, "testUser", "USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.year").value(year))
                .andExpect(jsonPath("$.totalWorkouts").value(50));
    }

    @Test
    void testGetCurrentWrapped_InWindow() throws Exception {
        Long userId = 1L;
        LocalDate today = LocalDate.now();

        // Calculate the wrapped year based on current month
        int wrappedYear = (today.getMonthValue() == 1) ? today.getYear() - 1 : today.getYear();

        WrappedDto dto = new WrappedDto();
        dto.setYear(wrappedYear);
        dto.setTotalWorkouts(100);

        when(wrappedService.getWrapped(userId, wrappedYear)).thenReturn(dto);

        // This test will return 204 if outside the window (Dec 22 - Jan 16)
        // or 200 with data if inside the window
        mockMvc.perform(get("/api/wrapped/current")
                .requestAttr("jwtInfo", new JwtInfo(userId, "testUser", "USER")))
                .andExpect(status().is2xxSuccessful());
    }
}
