package util;

import model.UserSession;

/**
 *
 * @author andyb
 */
public class SessionActivityInterceptor {
    
    /**
     * Update session activity before executing any service method
     */
    public static boolean checkAndUpdateSession(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            System.out.println("[SESSION INTERCEPTOR] No session ID provided");
            return false;
        }
        
        SessionManager sessionManager = SessionManager.getInstance();
        UserSession session = sessionManager.getSession(sessionId);
        
        if (session == null) {
            System.out.println("[SESSION INTERCEPTOR] Invalid session: " + sessionId);
            return false;
        }
        
        if (!session.isActive()) {
            System.out.println("[SESSION INTERCEPTOR] Inactive/expired session: " + sessionId);
            return false;
        }
        
        // Update activity
        sessionManager.updateActivity(sessionId);
        
        return true;
    }
    
    /**
     * Validate session and get user details
     */
    public static UserSession validateAndGetSession(String sessionId) {
        if (!checkAndUpdateSession(sessionId)) {
            return null;
        }
        
        return SessionManager.getInstance().getSession(sessionId);
    }
    
}
