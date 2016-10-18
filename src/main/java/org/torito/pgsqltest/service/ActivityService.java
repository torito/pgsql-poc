package org.torito.pgsqltest.service;

import org.torito.pgsqltest.service.dto.ActivityDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.LinkedList;
import java.util.List;

/**
 * Service Interface for managing Activity.
 */
public interface ActivityService {

    /**
     * Save a activity.
     *
     * @param activityDTO the entity to save
     * @return the persisted entity
     */
    ActivityDTO save(ActivityDTO activityDTO);

    /**
     *  Get all the activities.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<ActivityDTO> findAll(Pageable pageable);

    /**
     *  Get the "id" activity.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    ActivityDTO findOne(Long id);

    /**
     *  Delete the "id" activity.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the activity corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<ActivityDTO> search(String query, Pageable pageable);
}
