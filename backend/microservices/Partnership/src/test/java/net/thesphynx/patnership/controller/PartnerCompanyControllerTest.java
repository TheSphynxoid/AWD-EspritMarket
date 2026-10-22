package net.thesphynx.patnership.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.thesphynx.patnership.dto.PartnerCompanyDto;
import net.thesphynx.patnership.service.PartnerCompanyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PartnerCompanyControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PartnerCompanyService partnerCompanyService;

    @InjectMocks
    private PartnerCompanyController partnerCompanyController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(partnerCompanyController).build();
    }

    @Test
    void testFindAll() throws Exception {
        PartnerCompanyDto dto = new PartnerCompanyDto(1L, "Google", "Tech", "contact@google.com", "APPROVED", "https://google.com", "+12345678");
        when(partnerCompanyService.findAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/partnership/companies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Google"));

        verify(partnerCompanyService, times(1)).findAll();
    }

    @Test
    void testFindById() throws Exception {
        PartnerCompanyDto dto = new PartnerCompanyDto(1L, "Google", "Tech", "contact@google.com", "APPROVED", "https://google.com", "+12345678");
        when(partnerCompanyService.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/partnership/companies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Google"))
                .andExpect(jsonPath("$.contactEmail").value("contact@google.com"));

        verify(partnerCompanyService, times(1)).findById(1L);
    }

    @Test
    void testCreate() throws Exception {
        PartnerCompanyDto requestDto = new PartnerCompanyDto(null, "Microsoft", "IT", "jobs@microsoft.com", "PENDING", "https://microsoft.com", "+98765432");
        PartnerCompanyDto createdDto = new PartnerCompanyDto(2L, "Microsoft", "IT", "jobs@microsoft.com", "PENDING", "https://microsoft.com", "+98765432");

        when(partnerCompanyService.create(any(PartnerCompanyDto.class))).thenReturn(createdDto);

        mockMvc.perform(post("/api/partnership/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/partnership/companies/2"))
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.name").value("Microsoft"));

        verify(partnerCompanyService, times(1)).create(any(PartnerCompanyDto.class));
    }

    @Test
    void testUpdate() throws Exception {
        PartnerCompanyDto updateDto = new PartnerCompanyDto(1L, "Google Inc", "Technology", "hr@google.com", "APPROVED", "https://google.com", "+11111111");
        when(partnerCompanyService.update(eq(1L), any(PartnerCompanyDto.class))).thenReturn(updateDto);

        mockMvc.perform(put("/api/partnership/companies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Google Inc"))
                .andExpect(jsonPath("$.contactEmail").value("hr@google.com"));

        verify(partnerCompanyService, times(1)).update(eq(1L), any(PartnerCompanyDto.class));
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(partnerCompanyService).delete(1L);

        mockMvc.perform(delete("/api/partnership/companies/1"))
                .andExpect(status().isNoContent());

        verify(partnerCompanyService, times(1)).delete(1L);
    }
}
