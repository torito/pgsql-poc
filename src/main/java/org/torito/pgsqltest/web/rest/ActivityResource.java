package org.torito.pgsqltest.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.torito.pgsqltest.service.ActivityService;
import org.torito.pgsqltest.web.rest.util.HeaderUtil;
import org.torito.pgsqltest.web.rest.util.PaginationUtil;
import org.torito.pgsqltest.service.dto.ActivityDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Activity.
 */
@RestController
@RequestMapping("/api")
public class ActivityResource {

    private final Logger log = LoggerFactory.getLogger(ActivityResource.class);
        
    @Inject
    private ActivityService activityService;

    /**
     * POST  /activities : Create a new activity.
     *
     * @param activityDTO the activityDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new activityDTO, or with status 400 (Bad Request) if the activity has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/activities",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ActivityDTO> createActivity(@RequestBody ActivityDTO activityDTO) throws URISyntaxException {
        log.debug("REST request to save Activity : {}", activityDTO);
        if (activityDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("activity", "idexists", "A new activity cannot already have an ID")).body(null);
        }
        ActivityDTO result = activityService.save(activityDTO);
        return ResponseEntity.created(new URI("/api/activities/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("activity", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /activities : Updates an existing activity.
     *
     * @param activityDTO the activityDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated activityDTO,
     * or with status 400 (Bad Request) if the activityDTO is not valid,
     * or with status 500 (Internal Server Error) if the activityDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/activities",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ActivityDTO> updateActivity(@RequestBody ActivityDTO activityDTO) throws URISyntaxException {
        log.debug("REST request to update Activity : {}", activityDTO);
        if (activityDTO.getId() == null) {
            return createActivity(activityDTO);
        }
        ActivityDTO result = activityService.save(activityDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("activity", activityDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /activities : get all the activities.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of activities in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/activities",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ActivityDTO>> getAllActivities(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Activities");
        Page<ActivityDTO> page = activityService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/activities");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /activities/:id : get the "id" activity.
     *
     * @param id the id of the activityDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the activityDTO, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/activities/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ActivityDTO> getActivity(@PathVariable Long id) {
        log.debug("REST request to get Activity : {}", id);
        ActivityDTO activityDTO = activityService.findOne(id);
        return Optional.ofNullable(activityDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /activities/:id : delete the "id" activity.
     *
     * @param id the id of the activityDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/activities/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        log.debug("REST request to delete Activity : {}", id);
        activityService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("activity", id.toString())).build();
    }

    /**
     * SEARCH  /_search/activities?query=:query : search for the activity corresponding
     * to the query.
     *
     * @param query the query of the activity search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/_search/activities",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ActivityDTO>> searchActivities(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Activities for query {}", query);
        Page<ActivityDTO> page = activityService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/activities");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
