package net.thesphynx.patnership.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.thesphynx.patnership.dto.JobOfferDto;
import net.thesphynx.patnership.service.JobOfferService;
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
class JobOfferControllerTest {

    private MockMvc mockMvc;

    @Mock
    private JobOfferService jobOfferService;

    @InjectMocks
    private JobOfferController jobOfferController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(jobOfferController).build();
    }

    @Test
    void testFindAll() throws Exception {
        JobOfferDto dto = new JobOfferDto(1L, "Software Engineer", "Full stack Java", "CDI", "Tunis", "OPEN", 10L);
        when(jobOfferService.findAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/partnership/job-offers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Software Engineer"));

        verify(jobOfferService, times(1)).findAll();
    }

    @Test
    void testFindById() throws Exception {
        JobOfferDto dto = new JobOfferDto(1L, "Software Engineer", "Full stack Java", "CDI", "Tunis", "OPEN", 10L);
        when(jobOfferService.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/partnership/job-offers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Software Engineer"))
                .andExpect(jsonPath("$.companyId").value(10L));

        verify(jobOfferService, times(1)).findById(1L);
    }

    @Test
    void testFindByCompanyId() throws Exception {
        JobOfferDto dto = new JobOfferDto(1L, "Software Engineer", "Full stack Java", "CDI", "Tunis", "OPEN", 10L);
        when(jobOfferService.findByCompanyId(10L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/partnership/job-offers/company/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].companyId").value(10L));

        verify(jobOfferService, times(1)).findByCompanyId(10L);
    }

    @Test
    void testCreate() throws Exception {
        JobOfferDto requestDto = new JobOfferDto(null, "DevOps Engineer", "Kubernetes & CI/CD", "CDI", "Remote", "OPEN", 10L);
        JobOfferDto createdDto = new JobOfferDto(2L, "DevOps Engineer", "Kubernetes & CI/CD", "CDI", "Remote", "OPEN", 10L);

        when(jobOfferService.create(any(JobOfferDto.class))).thenReturn(createdDto);

        mockMvc.perform(post("/api/partnership/job-offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/partnership/job-offers/2"))
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.title").value("DevOps Engineer"));

        verify(jobOfferService, times(1)).create(any(JobOfferDto.class));
    }

    @Test
    void testUpdate() throws Exception {
        JobOfferDto updateDto = new JobOfferDto(1L, "Senior Software Engineer", "Tech Lead", "CDI", "Tunis", "OPEN", 10L);
        when(jobOfferService.update(eq(1L), any(JobOfferDto.class))).thenReturn(updateDto);

        mockMvc.perform(put("/api/partnership/job-offers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Senior Software Engineer"));

        verify(jobOfferService, times(1)).update(eq(1L), any(JobOfferDto.class));
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(jobOfferService).delete(1L);

        mockMvc.perform(delete("/api/partnership/job-offers/1"))
                .andExpect(status().isNoContent());

        verify(jobOfferService, times(1)).delete(1L);
    }
}
