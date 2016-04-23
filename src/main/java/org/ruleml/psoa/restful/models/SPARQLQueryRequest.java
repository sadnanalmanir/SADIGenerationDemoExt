package org.ruleml.psoa.restful.models;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by sadnana on 23/04/16.
 */
@XmlRootElement
public class SPARQLQueryRequest {
    private String query;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query.trim();
    }
}
