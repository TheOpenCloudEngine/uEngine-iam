package org.uengine.iam.client.couchdb;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by uengine on 2016. 4. 28..
 */
public class CouchFlexibleDAO extends HashMap<String, Object> implements Serializable {

    private String _id;
    private String _rev;
    private String docType;

    public String get_id() {
        return this.asString("_id");
    }

    public void set_id(String _id) {
        this.put("_id", _id);
    }

    public String get_rev() {
        return this.asString("_rev");
    }

    public void set_rev(String _rev) {
        this.put("_rev", _rev);
    }

    public String getDocType() {
        return this.asString("docType");
    }

    public void setDocType(String docType) {
        this.put("docType", docType);
    }

    public Long asLong(Object key) {
        if (this.containsKey(key)) {
            try {
                Object o = this.get(key);
                if(o.getClass().equals(Double.class)){
                    return ((Double) o).longValue();
                }else{
                    return (Long) this.get(key);
                }
            } catch (Exception ex) {
                return null;
            }
        } else {
            return null;
        }
    }

    public String asString(Object key) {
        if (this.containsKey(key)) {
            try {
                return (String) this.get(key);
            } catch (Exception ex) {
                return null;
            }
        } else {
            return null;
        }
    }

    public Integer asInteger(Object key) {
        if (this.containsKey(key)) {
            try {
                Object o = this.get(key);
                if(o.getClass().equals(Double.class)){
                    return ((Double) o).intValue();
                }else{
                    return (Integer) this.get(key);
                }
            } catch (Exception ex) {
                return null;
            }
        } else {
            return null;
        }
    }
}
