package org.distantshoresmedia.model;

/**
 * Created by Fechner on 11/7/14.
 */
public class BaseDataClass {

    private BaseDataClass parent;
    public void setParent(BaseDataClass parent){
        this.parent = parent;
    }
    public BaseDataClass getParent(){
        return this.parent;
    }

    private int uid;
    public void setUid(int uid) {
        this.uid = uid;
    }
    public int getUid() {
        return uid;
    }

    private int created;
    public void setCreated(int created) {
        this.created = created;
    }
    public int getCreated() {
        return created;
    }


    private int updated;
    public void setUpdated(int updated) {
        this.updated = updated;
    }
    public int getUpdated() {
        return updated;
    }

    public BaseDataClass(int uid, int created, int updated){
        this.uid = uid;
        this.created = created;
        this.updated = updated;
    }
}
