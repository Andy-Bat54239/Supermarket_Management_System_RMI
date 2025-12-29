package service;

import model.UserSession;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 *
 * @author andyb
 */
public interface SessionService extends Remote{
    
     /**
     * Create a new session
     */
    String createSession(String username, String employeeName, int employeeId, String role) throws RemoteException;
    
    /**
     * Validate session
     */
    boolean validateSession(String sessionId) throws RemoteException;
    
    /**
     * Get session details
     */
    UserSession getSessionDetails(String sessionId) throws RemoteException;
    
    /**
     * Update session activity
     */
    void updateSessionActivity(String sessionId) throws RemoteException;
    
    /**
     * Terminate session (logout)
     */
    void terminateSession(String sessionId) throws RemoteException;
    
    /**
     * Get all active sessions (admin only)
     */
    List<UserSession> getAllActiveSessions(String adminSessionId) throws RemoteException;
    
    /**
     * Force terminate user session (admin only)
     */
    void forceTerminateUserSession(String username, String adminSessionId) throws RemoteException;
    
}
