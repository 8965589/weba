package com.ander.weba.model.auth;

import java.io.Serializable;

/**
 * @author ander
 * @Date 2018/12/18 18:08.
 */
public class AuthRoleAccount implements Serializable {
    private String id;

    private String accountId;

    private String roleId;

    private static final long serialVersionUID = 1L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }


}