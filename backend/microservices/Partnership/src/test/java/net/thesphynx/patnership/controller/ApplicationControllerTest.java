package net.thesphynx.patnership.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.thesphynx.patnership.dto.ApplicationDto;
import net.thesphynx.patnership.service.ApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ApplicationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ApplicationService applicationService;

    @InjectMocks
    private ApplicationController applicationController;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(applicationController).build();
    }

    @Test
    void testFindAll() throws Exception {
        ApplicationDto dto = new ApplicationDto(1L, "Alice Doe", "alice@example.com", "http://cv.pdf", "PENDING", LocalDate.now(), 100L, 1L);
        when(applicationService.findAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/partnership/applications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].candidateName").value("Alice Doe"));

        verify(applicationService, times(1)).findAll();
    }

    @Test
    void testFindById() throws Exception {
        ApplicationDto dto = new ApplicationDto(1L, "Alice Doe", "alice@example.com", "http://cv.pdf", "PENDING", LocalDate.now(), 100L, 1L);
        when(applicationService.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/partnership/applications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.candidateName").value("Alice Doe"))
                .andExpect(jsonPath("$.candidateEmail").value("alice@example.com"));

        verify(applicationService, times(1)).findById(1L);
    }

    @Test
    void testFindByJobOfferId() throws Exception {
        ApplicationDto dto = new ApplicationDto(1L, "Alice Doe", "alice@example.com", "http://cv.pdf", "PENDING", LocalDate.now(), 100L, 1L);
        when(applicationService.findByJobOfferId(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/partnership/applications/job-offer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].jobOfferId").value(1L));

        verify(applicationService, times(1)).findByJobOfferId(1L);
    }

    @Test
    void testFindByUserId() throws Exception {
        ApplicationDto dto = new ApplicationDto(1L, "Alice Doe", "alice@example.com", "http://cv.pdf", "PENDING", LocalDate.now(), 100L, 1L);
        when(applicationService.findByUserId(100L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/partnership/applications/user/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].userId").value(100L));

        verify(applicationService, times(1)).findByUserId(100L);
    }

    @Test
    void testCreate() throws Exception {
        ApplicationDto requestDto = new ApplicationDto(null, "Bob Smith", "bob@example.com", "http://bob-cv.pdf", "PENDING", LocalDate.now(), 101L, 1L);
        ApplicationDto createdDto = new ApplicationDto(2L, "Bob Smith", "bob@example.com", "http://bob-cv.pdf", "PENDING", LocalDate.now(), 101L, 1L);

        when(applicationService.create(any(ApplicationDto.class))).thenReturn(createdDto);

        mockMvc.perform(post("/api/partnership/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/partnership/applications/2"))
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.candidateName").value("Bob Smith"));

        verify(applicationService, times(1)).create(any(ApplicationDto.class));
    }

    @Test
    void testUpdate() throws Exception {
        ApplicationDto updateDto = new ApplicationDto(1L, "Alice Doe", "alice.doe@example.com", "http://cv-updated.pdf", "ACCEPTED", LocalDate.now(), 100L, 1L);
        when(applicationService.update(eq(1L), any(ApplicationDto.class))).thenReturn(updateDto);

        mockMvc.perform(put("/api/partnership/applications/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));

        verify(applicationService, times(1)).update(eq(1L), any(ApplicationDto.class));
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(applicationService).delete(1L);

        mockMvc.perform(delete("/api/partnership/applications/1"))
                .andExpect(status().isNoContent());

        verify(applicationService, times(1)).delete(1L);
    }
}
