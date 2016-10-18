/**
 * JPA domain objects.
 */

@org.hibernate.annotations.TypeDef(name = "ObjectNode", typeClass = JsonType.class)
package org.torito.pgsqltest.domain;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.torito.pgsqltest.domain.util.JsonType;
