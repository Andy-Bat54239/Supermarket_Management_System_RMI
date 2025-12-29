package service.implementation;

import model.UserSession;
import service.SessionService;
import util.SessionManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 *
 * @author andyb
 */
public class SessionServiceImpl  extends UnicastRemoteObject implements SessionService{
    
    private SessionManager sessionManager = SessionManager.getInstance();
    
    public SessionServiceImpl() throws RemoteException {
        super();
    }
    
    @Override
    public String createSession(String username, String employeeName, int employeeId, String role) throws RemoteException {
        try {
            return sessionManager.createSession(username, employeeName, employeeId, role);
        } catch (Exception e) {
            throw new RemoteException("Error creating session", e);
        }
    }
    
    @Override
    public boolean validateSession(String sessionId) throws RemoteException {
        try {
            return sessionManager.validateSession(sessionId);
        } catch (Exception e) {
            throw new RemoteException("Error validating session", e);
        }
    }
    
    @Override
    public UserSession getSessionDetails(String sessionId) throws RemoteException {
        try {
            return sessionManager.getSession(sessionId);
        } catch (Exception e) {
            throw new RemoteException("Error getting session details", e);
        }
    }
    
    @Override
    public void updateSessionActivity(String sessionId) throws RemoteException {
        try {
            sessionManager.updateActivity(sessionId);
        } catch (Exception e) {
            throw new RemoteException("Error updating session activity", e);
        }
    }
    
    @Override
    public void terminateSession(String sessionId) throws RemoteException {
        try {
            sessionManager.terminateSession(sessionId);
        } catch (Exception e) {
            throw new RemoteException("Error terminating session", e);
        }
    }
    
    @Override
    public List<UserSession> getAllActiveSessions(String adminSessionId) throws RemoteException {
        try {
            // Validate admin session
            UserSession adminSession = sessionManager.getSession(adminSessionId);
            if (adminSession == null || !adminSession.getRole().equalsIgnoreCase("ADMIN")) {
                throw new RemoteException("Unauthorized: Admin access required");
            }
            
            return sessionManager.getAllActiveSessions();
        } catch (Exception e) {
            throw new RemoteException("Error getting active sessions", e);
        }
    }
    
    @Override
    public void forceTerminateUserSession(String username, String adminSessionId) throws RemoteException {
        try {
            // Validate admin session
            UserSession adminSession = sessionManager.getSession(adminSessionId);
            if (adminSession == null || !adminSession.getRole().equalsIgnoreCase("ADMIN")) {
                throw new RemoteException("Unauthorized: Admin access required");
            }
            
            sessionManager.terminateUserSessions(username);
        } catch (Exception e) {
            throw new RemoteException("Error force terminating session", e);
        }
    }
    
}
