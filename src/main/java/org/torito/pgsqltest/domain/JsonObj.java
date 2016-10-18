package org.torito.pgsqltest.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import springfox.documentation.spring.web.json.Json;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fgg on 18/10/16.
 */
public class JsonObj implements Serializable {

    private String node ;

    public JsonObj(){

    }

    public void setJson(String node) {
        this.node = node;
    }
    public String getJson() {
        return node;
    }
}
