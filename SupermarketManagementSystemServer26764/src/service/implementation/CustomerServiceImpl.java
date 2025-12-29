package service.implementation;

import dao.CustomerDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import model.Customer;
import service.CustomerService;

/**
 * Customer Service Implementation
 */
public class CustomerServiceImpl extends UnicastRemoteObject implements CustomerService{
    
    private CustomerDao customerDao = new CustomerDao();
    
    public CustomerServiceImpl() throws RemoteException{
        
    }

    @Override
    public Integer addCustomer(Customer customer) throws RemoteException {
        return customerDao.save(customer);
    }

    @Override
    public boolean updateCustomer(Customer customer) throws RemoteException {
        return customerDao.update(customer);
    }

    @Override
    public boolean deleteCustomer(int customerId) throws RemoteException {
        // Use the proper delete method with validation
        return customerDao.deleteCustomer(customerId);
    }

    @Override
    public Customer findCustomerById(int customerId) throws RemoteException {
        return customerDao.findById(customerId);
    }

    @Override
    public List<Customer> findAllCustomers() throws RemoteException {
        return customerDao.findAll();
    }

    @Override
    public List<Integer> getAllCustomerIds() throws RemoteException {
        return customerDao.getAllCustomerIds();
    }
    
}
