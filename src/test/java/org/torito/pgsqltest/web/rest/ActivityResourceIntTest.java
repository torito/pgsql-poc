package org.torito.pgsqltest.web.rest;

import org.torito.pgsqltest.PgsqltestApp;

import org.torito.pgsqltest.domain.Activity;
import org.torito.pgsqltest.repository.ActivityRepository;
import org.torito.pgsqltest.service.ActivityService;
import org.torito.pgsqltest.repository.search.ActivitySearchRepository;
import org.torito.pgsqltest.service.dto.ActivityDTO;
import org.torito.pgsqltest.service.mapper.ActivityMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ActivityResource REST controller.
 *
 * @see ActivityResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PgsqltestApp.class)
public class ActivityResourceIntTest {

    private static final Integer DEFAULT_USER_CODE = 1;
    private static final Integer UPDATED_USER_CODE = 2;

    @Inject
    private ActivityRepository activityRepository;

    @Inject
    private ActivityMapper activityMapper;

    @Inject
    private ActivityService activityService;

    @Inject
    private ActivitySearchRepository activitySearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restActivityMockMvc;

    private Activity activity;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ActivityResource activityResource = new ActivityResource();
        ReflectionTestUtils.setField(activityResource, "activityService", activityService);
        this.restActivityMockMvc = MockMvcBuilders.standaloneSetup(activityResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Activity createEntity(EntityManager em) {
        Activity activity = new Activity()
                .user_code(DEFAULT_USER_CODE);
        return activity;
    }

    @Before
    public void initTest() {
        activitySearchRepository.deleteAll();
        activity = createEntity(em);
    }

    @Test
    @Transactional
    public void createActivity() throws Exception {
        int databaseSizeBeforeCreate = activityRepository.findAll().size();

        // Create the Activity
        ActivityDTO activityDTO = activityMapper.activityToActivityDTO(activity);

        restActivityMockMvc.perform(post("/api/activities")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(activityDTO)))
                .andExpect(status().isCreated());

        // Validate the Activity in the database
        List<Activity> activities = activityRepository.findAll();
        assertThat(activities).hasSize(databaseSizeBeforeCreate + 1);
        Activity testActivity = activities.get(activities.size() - 1);
        assertThat(testActivity.getUser_code()).isEqualTo(DEFAULT_USER_CODE);

        // Validate the Activity in ElasticSearch
        Activity activityEs = activitySearchRepository.findOne(testActivity.getId());
        assertThat(activityEs).isEqualToComparingFieldByField(testActivity);
    }

    @Test
    @Transactional
    public void getAllActivities() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activities
        restActivityMockMvc.perform(get("/api/activities?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(activity.getId().intValue())))
                .andExpect(jsonPath("$.[*].user_code").value(hasItem(DEFAULT_USER_CODE)));
    }

    @Test
    @Transactional
    public void getActivity() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get the activity
        restActivityMockMvc.perform(get("/api/activities/{id}", activity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(activity.getId().intValue()))
            .andExpect(jsonPath("$.user_code").value(DEFAULT_USER_CODE));
    }

    @Test
    @Transactional
    public void getNonExistingActivity() throws Exception {
        // Get the activity
        restActivityMockMvc.perform(get("/api/activities/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateActivity() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);
        activitySearchRepository.save(activity);
        int databaseSizeBeforeUpdate = activityRepository.findAll().size();

        // Update the activity
        Activity updatedActivity = activityRepository.findOne(activity.getId());
        updatedActivity
                .user_code(UPDATED_USER_CODE);
        ActivityDTO activityDTO = activityMapper.activityToActivityDTO(updatedActivity);

        restActivityMockMvc.perform(put("/api/activities")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(activityDTO)))
                .andExpect(status().isOk());

        // Validate the Activity in the database
        List<Activity> activities = activityRepository.findAll();
        assertThat(activities).hasSize(databaseSizeBeforeUpdate);
        Activity testActivity = activities.get(activities.size() - 1);
        assertThat(testActivity.getUser_code()).isEqualTo(UPDATED_USER_CODE);

        // Validate the Activity in ElasticSearch
        Activity activityEs = activitySearchRepository.findOne(testActivity.getId());
        assertThat(activityEs).isEqualToComparingFieldByField(testActivity);
    }

    @Test
    @Transactional
    public void deleteActivity() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);
        activitySearchRepository.save(activity);
        int databaseSizeBeforeDelete = activityRepository.findAll().size();

        // Get the activity
        restActivityMockMvc.perform(delete("/api/activities/{id}", activity.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean activityExistsInEs = activitySearchRepository.exists(activity.getId());
        assertThat(activityExistsInEs).isFalse();

        // Validate the database is empty
        List<Activity> activities = activityRepository.findAll();
        assertThat(activities).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchActivity() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);
        activitySearchRepository.save(activity);

        // Search the activity
        restActivityMockMvc.perform(get("/api/_search/activities?query=id:" + activity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(activity.getId().intValue())))
            .andExpect(jsonPath("$.[*].user_code").value(hasItem(DEFAULT_USER_CODE)));
    }
}
