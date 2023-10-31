package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBDate;
import edu.ucsb.cs156.example.entities.UCSBMenuItemReviews;
import edu.ucsb.cs156.example.repositories.UCSBMenuItemReviewsRepository;

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

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = UCSBMenuItemReviewsController.class)
@Import(TestConfig.class)
public class UCSBMenuItemReviewsControllerTests extends ControllerTestCase {

        @MockBean
        UCSBMenuItemReviewsRepository ucsbMenuItemReviewsRepository;

        @MockBean
        UserRepository userRepository;

        // Tests for GET /api/ucsbmenuitemreviews/all
        
        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/ucsbmenuitemreviews/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
                mockMvc.perform(get("/api/ucsbmenuitemreviews/all"))
                                .andExpect(status().is(200)); // logged
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_ucsbmenuitemreviews() throws Exception {

                // arrange
                LocalDate ldt1 = LocalDate.parse("2022-04-20");

                UCSBMenuItemReviews ucsbMenuItemReview1 = UCSBMenuItemReviews.builder()
                                .itemId(27)
                                .reviewerEmail("cgaucho@ucsb.edu")
                                .stars(3)
                                .dateReviewed(ldt1.atStartOfDay())
                                .comments("bland af but edible I guess")
                                .build();

                LocalDate ldt2 = LocalDate.parse("2022-04-20");

                UCSBMenuItemReviews ucsbMenuItemReview2 = UCSBMenuItemReviews.builder()
                                .itemId(29)
                                .reviewerEmail("cgaucho@ucsb.edu")
                                .stars(5)
                                .dateReviewed(ldt2.atStartOfDay())
                                .comments("best veggie pizza ever")
                                .build();

                LocalDate ldt3 = LocalDate.parse("2022-04-21");

                UCSBMenuItemReviews ucsbMenuItemReview3 = UCSBMenuItemReviews.builder()
                                .itemId(29)
                                .reviewerEmail("ldelplaya@ucsb.edu")
                                .stars(0)
                                .dateReviewed(ldt3.atStartOfDay())
                                .comments("not tryna get food poisoning, but if I were this would do it")
                                .build();


                ArrayList<UCSBMenuItemReviews> expectedMenuItemReviews = new ArrayList<>();
                expectedMenuItemReviews.addAll(Arrays.asList(ucsbMenuItemReview1, ucsbMenuItemReview2, ucsbMenuItemReview3));

                when(ucsbMenuItemReviewsRepository.findAll()).thenReturn(expectedMenuItemReviews);

                // act
                MvcResult response = mockMvc.perform(get("/api/ucsbmenuitemreviews/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(ucsbMenuItemReviewsRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedMenuItemReviews);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        // Tests for POST /api/ucsbmenuitemreviews/post...

        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/ucsbmenuitemreviews/post"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/ucsbmenuitemreviews/post"))
                                .andExpect(status().is(403)); // only admins can post
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_ucsbmenureviewitem() throws Exception {
                // arrange

                LocalDate ldt1 = LocalDate.parse("2022-01-03");

                UCSBMenuItemReviews ucsbMenuItemReview1 = UCSBMenuItemReviews.builder()
                                .itemId(29)
                                .reviewerEmail("ldelplaya@ucsb.edu")
                                .stars(2)
                                .dateReviewed(ldt1.atStartOfDay())
                                .comments("eh")
                                .build();

                when(ucsbMenuItemReviewsRepository.save(eq(ucsbMenuItemReview1))).thenReturn(ucsbMenuItemReview1);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/ucsbmenuitemreviews/post?itemId=29&reviewerEmail=ldelplaya@ucsb.edu&stars=2&dateReviewed=2022-01-03&comments=eh")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(ucsbMenuItemReviewsRepository, times(1)).save(ucsbMenuItemReview1);
                String expectedJson = mapper.writeValueAsString(ucsbMenuItemReview1);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }
}
