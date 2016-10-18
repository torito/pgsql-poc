package org.torito.pgsqltest.service.impl;

import org.torito.pgsqltest.service.ActivityService;
import org.torito.pgsqltest.domain.Activity;
import org.torito.pgsqltest.repository.ActivityRepository;
import org.torito.pgsqltest.repository.search.ActivitySearchRepository;
import org.torito.pgsqltest.service.dto.ActivityDTO;
import org.torito.pgsqltest.service.mapper.ActivityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Activity.
 */
@Service
@Transactional
public class ActivityServiceImpl implements ActivityService{

    private final Logger log = LoggerFactory.getLogger(ActivityServiceImpl.class);
    
    @Inject
    private ActivityRepository activityRepository;

    @Inject
    private ActivityMapper activityMapper;

    @Inject
    private ActivitySearchRepository activitySearchRepository;

    /**
     * Save a activity.
     *
     * @param activityDTO the entity to save
     * @return the persisted entity
     */
    public ActivityDTO save(ActivityDTO activityDTO) {
        log.debug("Request to save Activity : {}", activityDTO);
        Activity activity = activityMapper.activityDTOToActivity(activityDTO);
        activity = activityRepository.save(activity);
        ActivityDTO result = activityMapper.activityToActivityDTO(activity);
        activitySearchRepository.save(activity);
        return result;
    }

    /**
     *  Get all the activities.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<ActivityDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Activities");
        Page<Activity> result = activityRepository.findAll(pageable);
        return result.map(activity -> activityMapper.activityToActivityDTO(activity));
    }

    /**
     *  Get one activity by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public ActivityDTO findOne(Long id) {
        log.debug("Request to get Activity : {}", id);
        Activity activity = activityRepository.findOne(id);
        ActivityDTO activityDTO = activityMapper.activityToActivityDTO(activity);
        return activityDTO;
    }

    /**
     *  Delete the  activity by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Activity : {}", id);
        activityRepository.delete(id);
        activitySearchRepository.delete(id);
    }

    /**
     * Search for the activity corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<ActivityDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Activities for query {}", query);
        Page<Activity> result = activitySearchRepository.search(queryStringQuery(query), pageable);
        return result.map(activity -> activityMapper.activityToActivityDTO(activity));
    }
}
