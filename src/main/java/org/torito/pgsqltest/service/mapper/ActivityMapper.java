package org.torito.pgsqltest.service.mapper;

import org.torito.pgsqltest.domain.*;
import org.torito.pgsqltest.service.dto.ActivityDTO;

import org.mapstruct.*;
import java.util.List;

/**
 * Mapper for the entity Activity and its DTO ActivityDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ActivityMapper {

    ActivityDTO activityToActivityDTO(Activity activity);

    List<ActivityDTO> activitiesToActivityDTOs(List<Activity> activities);

    Activity activityDTOToActivity(ActivityDTO activityDTO);

    List<Activity> activityDTOsToActivities(List<ActivityDTO> activityDTOs);
}
