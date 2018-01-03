package org.uengine.iam.client.model;

/**
 * Created by uengine on 2015. 6. 3..
 */
public class OauthScope {
    private String name;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
