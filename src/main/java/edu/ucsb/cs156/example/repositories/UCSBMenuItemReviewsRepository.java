package edu.ucsb.cs156.example.repositories;

import edu.ucsb.cs156.example.entities.UCSBMenuItemReviews;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UCSBMenuItemReviewsRepository extends CrudRepository<UCSBMenuItemReviews, Long> {
}