package org.ruleml.psoa.restful.models;

import org.semanticweb.owlapi.model.IRI;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by sadnana on 26/02/16.
 */
@XmlRootElement
public class URILoadRequest {
    private String iri;

    /**
     *
     * @return uri of the ontology
     */
    public String getIri() {
        return iri.trim();
    }

    public void setIri(String iri) {
        this.iri = iri;
    }
}
