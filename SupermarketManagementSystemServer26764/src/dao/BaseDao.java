package dao;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

/**
 *
 * @author andyb
 */
public abstract class BaseDao<T> {
    private Class<T> entityClass;
    
    protected BaseDao(Class<T> entityClass){
        this.entityClass = entityClass;
    }
    
    public Integer save(T entity) {
        try{
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            Integer id = (Integer) ss.save(entity);
            tr.commit();
            ss.close();
            return id;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }  
    }
    
    public boolean update(T entity) {
        try{
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.update(entity);
            tr.commit();
            ss.close();
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean delete(T entity) {
        try{
            Session ss = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = ss.beginTransaction();
            ss.delete(entity);
            tr.commit();
            ss.close();
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
    
    public T findById(int id){
        try{
            Session ss = HibernateUtil.getSessionFactory().openSession();
            return (T) ss.get(entityClass, id);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    
    public List<T> findAll(){
        try{
            Session ss = HibernateUtil.getSessionFactory().openSession();
            List<T> entities = ss.createQuery("FROM "+ entityClass.getSimpleName()).list();
            ss.close();
            return entities;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
