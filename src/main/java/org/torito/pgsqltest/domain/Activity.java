package org.torito.pgsqltest.domain;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Activity.
 */
@Entity
@Table(name = "activity")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "activity")
public class Activity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "user_code")
    private Integer user_code;

    public JsonObj getActivity() {
        return activity;
    }

    public void setActivity(JsonObj activity) {
        this.activity = activity;
    }

    @Column(name="activity")
    @Type(type = "ObjectNode")
    private JsonObj activity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getUser_code() {
        return user_code;
    }

    public Activity user_code(Integer user_code) {
        this.user_code = user_code;
        return this;
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
        Activity activity = (Activity) o;
        if(activity.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, activity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Activity{" +
            "id=" + id +
            ", user_code='" + user_code + "'" +
            '}';
    }
}
