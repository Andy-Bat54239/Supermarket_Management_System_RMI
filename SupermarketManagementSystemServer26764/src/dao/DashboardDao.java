package dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import model.DailySalesData;
import org.hibernate.Query;
import org.hibernate.Session;
import util.HibernateUtil;

/**
 *
 * @author andyb
 */
public class DashboardDao {
    public Integer getTotalProducts(){
        try{
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Query query = ss.createQuery("SELECT COUNT(*) FROM Product");
            Long count = (Long) query.uniqueResult();
            return count.intValue();
        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }
    
    public Integer getTotalSales(){
        try{
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Query query = ss.createQuery("SELECT COUNT(*) FROM Sales");
            Long count = (Long) query.uniqueResult();
            return count.intValue();
        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }
    
    public Integer getTotalCustomers(){
        try{
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Query query = ss.createQuery("SELECT COUNT(*) FROM Customer");
            Long count = (Long) query.uniqueResult();
            return count.intValue();
        }catch(Exception e){
            e.printStackTrace();
            return 0;
        }
    }
    
    public Double getTotalRevenue(){
        try{
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Query query = ss.createQuery("SELECT COALESCE(SUM(totalAmount), 0.0) FROM Sales");
            return (Double) query.uniqueResult();
        }catch(Exception e){
            e.printStackTrace();
            return 0.0;
        }
    }
    
    public List<DailySalesData> getDailySalesTrend(){
        try{
            Session ss = HibernateUtil.getSessionFactory().openSession();
            String hql = "SELECT DATE(s.saleDate), SUM(s.totalAmount) " +
                        "FROM Sales s " +
                        "GROUP BY DATE(s.saleDate) " +
                        "ORDER BY DATE(s.saleDate)";
            Query query = ss.createQuery(hql);
            @SuppressWarnings("Unchecked")
            List<Object[]> results = query.list();
            
            List<DailySalesData> salesData = new ArrayList<>();
            
            for(Object[] row : results){
                Date date = (Date) row[0];
                Double total = (Double) row[1];
                salesData.add(new DailySalesData(date, total));
            }
            ss.close();
            return salesData;
        }catch(Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
