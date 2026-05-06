package com.voyagrr.tripservice.repository;

import com.voyagrr.tripservice.model.Trip;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findAllByOwnerIdOrGroupIdInAndDeletedFalse(String ownerId, List<Long> groupIds);

}
