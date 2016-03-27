package org.ruleml.psoa.restful.models;


import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by sadnana on 15/03/16.
 */
@XmlRootElement
public class ServiceParameterRequest {


    String serviceName;
    String serviceClass;
    String serviceInputURI;
    String serviceOutputURI;
    String serviceDescription;
    String serviceEmail;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceClass() {
        return serviceClass;
    }

    public void setServiceClass(String serviceClass) {
        this.serviceClass = serviceClass;
    }

    public String getServiceInputURI() {
        return serviceInputURI;
    }

    public void setServiceInputURI(String serviceInputURI) {
        this.serviceInputURI = serviceInputURI;
    }

    public String getServiceOutputURI() {
        return serviceOutputURI;
    }

    public void setServiceOutputURI(String serviceOutputURI) {
        this.serviceOutputURI = serviceOutputURI;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }

    public String getServiceEmail() {
        return serviceEmail;
    }

    public void setServiceEmail(String serviceEmail) {
        this.serviceEmail = serviceEmail;
    }
}
