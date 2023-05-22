/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sm.base.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.Table;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
public class LoginDTO {
  String token;
  String userName;
  String userFullName;
  String roleCode;
  String roleName;
  String roleDescription;
  String provinceCode;
  String provinceName;
  String reasonFail;
  List<MenuParent> lstMenu;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<MenuParent> getLstMenu() {
        return lstMenu;
    }

    public void setLstMenu(List<MenuParent> lstMenu) {
        this.lstMenu = lstMenu;
    }
}
