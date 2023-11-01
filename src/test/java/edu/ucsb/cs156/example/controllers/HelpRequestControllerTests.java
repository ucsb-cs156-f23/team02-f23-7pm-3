package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.HelpRequest;
import edu.ucsb.cs156.example.repositories.HelpRequestRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDateTime;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = HelpRequestController.class)
@Import(TestConfig.class)
public class HelpRequestControllerTests extends ControllerTestCase {

       @MockBean
        HelpRequestRepository helpRequestRepository;

        @MockBean
        UserRepository userRepository;

        // Tests for GET /api/ucsbdates/all
        
      @Test
        public void logged_out_users_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/helprequest/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
                mockMvc.perform(get("/api/helprequest/all"))
                                .andExpect(status().is(200)); // logged
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_ucsbdates() throws Exception {

                // arrange
                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

                HelpRequest helpRequest1 = HelpRequest.builder()
                                .requestEmail("cgaucho@ucsb.edu")
                                .teamId("f23-7pm-3")
                                .tableOrBreakoutRoom("11")
                                .requestTime(ldt1)
                                .explanation("Dokku problems")
                                .solved(false)
                                .build();

                LocalDateTime ldt2 = LocalDateTime.parse("2022-03-11T00:00:00");

                HelpRequest helpRequest2 = HelpRequest.builder()
                                .requestEmail("cgaucho@ucsb.edu")
                                .teamId("f23-7pm-3")
                                .tableOrBreakoutRoom("11")
                                .requestTime(ldt2)
                                .explanation("Dokku problems")
                                .solved(false)
                                .build();

                ArrayList<HelpRequest> expectedHelpRequest = new ArrayList<>();
                expectedHelpRequest.addAll(Arrays.asList(helpRequest1, helpRequest2));

                when(helpRequestRepository.findAll()).thenReturn(expectedHelpRequest);

                // act
                MvcResult response = mockMvc.perform(get("/api/helprequest/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(helpRequestRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedHelpRequest);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }
        
             // Tests for POST /api/ucsbdates/post...

             @Test
             public void logged_out_users_cannot_post() throws Exception {
                     mockMvc.perform(post("/api/helprequest/post"))
                                     .andExpect(status().is(403));
             }
     
             @WithMockUser(roles = { "USER" })
             @Test
             public void logged_in_regular_users_cannot_post() throws Exception {
                     mockMvc.perform(post("/api/helprequest/post"))
                                     .andExpect(status().is(403)); // only admins can post
             }
     
             @WithMockUser(roles = { "ADMIN", "USER" })
             @Test
             public void an_admin_user_can_post_a_new_helprequest() throws Exception {
                     // arrange
     
                     LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");
     
                     HelpRequest helpRequest1 = HelpRequest.builder()
                                .requestEmail("cgaucho@ucsb.edu")
                                .teamId("f23-7pm-3")
                                .tableOrBreakoutRoom("11")
                                .requestTime(ldt1)
                                .explanation("Dokku problems")
                                .solved(true)
                                .build();
     
                     when(helpRequestRepository.save(eq(helpRequest1))).thenReturn(helpRequest1);
     
                     // act
                     MvcResult response = mockMvc.perform(
                                     post("/api/helprequest/post?requestEmail=cgaucho@ucsb.edu&teamID=f23-7pm-3&tableOrBreakoutRoom=11&requestTime=2022-01-03T00:00:00&explanation=Dokku problems&solved=true")
                                                     .with(csrf()))
                                     .andExpect(status().isOk()).andReturn();
     
                     // assert
                     verify(helpRequestRepository, times(1)).save(helpRequest1);
                     String expectedJson = mapper.writeValueAsString(helpRequest1);
                     String responseString = response.getResponse().getContentAsString();
                     assertEquals(expectedJson, responseString);
             }

    }
