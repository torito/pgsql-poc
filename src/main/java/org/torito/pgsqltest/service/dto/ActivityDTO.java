package org.torito.pgsqltest.service.dto;

import java.io.Serializable;
import java.util.Objects;


/**
 * A DTO for the Activity entity.
 */
public class ActivityDTO implements Serializable {

    private Long id;

    private Integer user_code;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Integer getUser_code() {
        return user_code;
    }

    public void setUser_code(Integer user_code) {
        this.user_code = user_code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ActivityDTO activityDTO = (ActivityDTO) o;

        if ( ! Objects.equals(id, activityDTO.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ActivityDTO{" +
            "id=" + id +
            ", user_code='" + user_code + "'" +
            '}';
    }
}
