package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.UCSBDate;
import edu.ucsb.cs156.example.entities.UCSBMenuItemReviews;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.UCSBMenuItemReviewsRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Tag(name = "UCSBMenuItemReviews")
@RequestMapping("/api/ucsbmenuitemreviews")
@RestController
@Slf4j
public class UCSBMenuItemReviewsController extends ApiController {

    @Autowired
    UCSBMenuItemReviewsRepository ucsbMenuItemReviewsRepository;

    @Operation(summary= "List all ucsb menu item reviews")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBMenuItemReviews> allUCSBMenuItemReviews() {
        Iterable<UCSBMenuItemReviews> menuItemReviews = ucsbMenuItemReviewsRepository.findAll();
        return menuItemReviews;
    }

    @Operation(summary= "Create a new menu item review")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public UCSBMenuItemReviews postUCSBMenuItemReviews(
            @Parameter(name="itemId") @RequestParam Long itemId,
            @Parameter(name="reviewerEmail") @RequestParam String reviewerEmail,
            @Parameter(name="stars") @RequestParam int stars,
            @Parameter(name="dateReviewed", description="in iso format, e.g. YYYY-mm-dd; see https://en.wikipedia.org/wiki/ISO_8601") @RequestParam("dateReviewed") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateReviewed,
            @Parameter(name="comments") @RequestParam String comments)
            throws JsonProcessingException {

        log.info("dateReviewed={}", dateReviewed);

        UCSBMenuItemReviews ucsbMenuItemReview = new UCSBMenuItemReviews();
        ucsbMenuItemReview.setItemId(itemId);
        ucsbMenuItemReview.setReviewerEmail(reviewerEmail);
        ucsbMenuItemReview.setStars(stars);
        ucsbMenuItemReview.setDateReviewed(dateReviewed.atStartOfDay());
        ucsbMenuItemReview.setComments(comments);

        UCSBMenuItemReviews savedUcsbMenuItemReview = ucsbMenuItemReviewsRepository.save(ucsbMenuItemReview);

        return savedUcsbMenuItemReview;
    }
}
