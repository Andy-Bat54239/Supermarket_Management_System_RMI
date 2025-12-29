/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author andyb
 */
public class DailySalesData implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    private Date date;
    private double total;

    public DailySalesData() {
    }

    public DailySalesData(Date date, double total) {
        this.date = date;
        this.total = total;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "DailySalesData{" 
                + "date=" + date + 
                ", total=" + total + 
                '}';
    }
    
    
    
}
