/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import model.EmployeeProfile;

/**
 *
 * @author andyb
 */
public class EmployeeProfileDao extends BaseDao<EmployeeProfile>{
    
    public EmployeeProfileDao(){
        super(EmployeeProfile.class);
    }
}
