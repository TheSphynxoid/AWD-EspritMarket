package net.thesphynx.patnership.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.thesphynx.patnership.dto.InterviewDto;
import net.thesphynx.patnership.service.InterviewService;
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
class InterviewControllerTest {

    private MockMvc mockMvc;

    @Mock
    private InterviewService interviewService;

    @InjectMocks
    private InterviewController interviewController;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(interviewController).build();
    }

    @Test
    void testFindAll() throws Exception {
        InterviewDto dto = new InterviewDto(1L, LocalDate.now(), "Teams", "TECHNICAL", "SCHEDULED", "Coding interview", 5L);
        when(interviewService.findAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/partnership/interviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].type").value("TECHNICAL"));

        verify(interviewService, times(1)).findAll();
    }

    @Test
    void testFindById() throws Exception {
        InterviewDto dto = new InterviewDto(1L, LocalDate.now(), "Teams", "TECHNICAL", "SCHEDULED", "Coding interview", 5L);
        when(interviewService.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/partnership/interviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.location").value("Teams"))
                .andExpect(jsonPath("$.applicationId").value(5L));

        verify(interviewService, times(1)).findById(1L);
    }

    @Test
    void testFindByApplicationId() throws Exception {
        InterviewDto dto = new InterviewDto(1L, LocalDate.now(), "Teams", "TECHNICAL", "SCHEDULED", "Coding interview", 5L);
        when(interviewService.findByApplicationId(5L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/partnership/interviews/application/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].applicationId").value(5L));

        verify(interviewService, times(1)).findByApplicationId(5L);
    }

    @Test
    void testCreate() throws Exception {
        InterviewDto requestDto = new InterviewDto(null, LocalDate.now(), "Google Meet", "HR", "SCHEDULED", "Intro interview", 5L);
        InterviewDto createdDto = new InterviewDto(2L, LocalDate.now(), "Google Meet", "HR", "SCHEDULED", "Intro interview", 5L);

        when(interviewService.create(any(InterviewDto.class))).thenReturn(createdDto);

        mockMvc.perform(post("/api/partnership/interviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/partnership/interviews/2"))
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.location").value("Google Meet"));

        verify(interviewService, times(1)).create(any(InterviewDto.class));
    }

    @Test
    void testUpdate() throws Exception {
        InterviewDto updateDto = new InterviewDto(1L, LocalDate.now(), "Office Room 101", "HR", "COMPLETED", "Candidate passed", 5L);
        when(interviewService.update(eq(1L), any(InterviewDto.class))).thenReturn(updateDto);

        mockMvc.perform(put("/api/partnership/interviews/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        verify(interviewService, times(1)).update(eq(1L), any(InterviewDto.class));
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(interviewService).delete(1L);

        mockMvc.perform(delete("/api/partnership/interviews/1"))
                .andExpect(status().isNoContent());

        verify(interviewService, times(1)).delete(1L);
    }
}
