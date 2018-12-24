package com.ander.weba.model.auth;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ander
 * @Date 2018/12/18 18:08.
 */
public class AuthRole implements Serializable {
    private String id;

    private String roleName;

    private Date crTime;

    private static final long serialVersionUID = 1L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Date getCrTime() {
        return crTime;
    }

    public void setCrTime(Date crTime) {
        this.crTime = crTime;
    }


}