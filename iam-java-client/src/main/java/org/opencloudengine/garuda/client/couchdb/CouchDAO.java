package org.opencloudengine.garuda.client.couchdb;

import java.io.Serializable;

/**
 * Created by uengine on 2016. 4. 28..
 */
public class CouchDAO implements Serializable {

    private String _id;
    private String _rev;
    private String docType;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_rev() {
        return _rev;
    }

    public void set_rev(String _rev) {
        this._rev = _rev;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }
}
