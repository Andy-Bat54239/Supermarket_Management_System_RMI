package service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.Customer;

/**
 *
 * @author andyb
 */
public interface CustomerService extends Remote{
    Integer addCustomer(Customer customer) throws RemoteException;
    boolean updateCustomer(Customer customer) throws RemoteException;
    boolean deleteCustomer(int customerId) throws RemoteException;
    Customer findCustomerById(int customerId) throws RemoteException;
    List<Customer> findAllCustomers() throws RemoteException;
     List<Integer> getAllCustomerIds() throws RemoteException;
}
