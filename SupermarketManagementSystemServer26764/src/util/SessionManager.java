package util;

import model.UserSession;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author andyb
 */
public class SessionManager {
    
    // Singleton instance
    private static SessionManager instance;
    
    // Store sessions (sessionId -> UserSession)
    private final Map<String, UserSession> sessions;
    
    // Store username to sessionId mapping
    private final Map<String, String> userSessions;
    
    // Cleanup thread
    private Timer cleanupTimer;
    
    private SessionManager(){
        sessions = new ConcurrentHashMap<>();
        userSessions = new ConcurrentHashMap<>();
        startCleanupTask();
    }
    
    /**
     * Get singleton instance
     */
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    public String createSession(String username, String employeeName, int employeeId, String role) {
        // Check if user already has an active session
        String existingSessionId = userSessions.get(username);
        if (existingSessionId != null) {
            UserSession existingSession = sessions.get(existingSessionId);
            if (existingSession != null && existingSession.isActive()) {
                // Terminate old session
                terminateSession(existingSessionId);
                System.out.println("[SESSION MANAGER] Terminated old session for user: " + username);
            }
        }
        UserSession session = new UserSession(username, employeeName, employeeId, role);
        String sessionId = session.getSessionId();
        
        sessions.put(sessionId, session);
        userSessions.put(username, sessionId);
        
        System.out.println("[SESSION MANAGER] Session created for user: " + username + " (ID: " + sessionId + ")");
        return sessionId;
    }
    
    /**
     * Get session by ID
     */
    public UserSession getSession(String sessionId) {
        UserSession session = sessions.get(sessionId);
        
        if (session != null) {
            if (session.isExpired()) {
                terminateSession(sessionId);
                return null;
            }
            session.updateActivity();
        }
        
        return session;
    }
    
    /**
     * Get session by username
     */
    public UserSession getSessionByUsername(String username) {
        String sessionId = userSessions.get(username);
        return sessionId != null ? getSession(sessionId) : null;
    }
    
    /**
     * Validate session
     */
    public boolean validateSession(String sessionId) {
        UserSession session = getSession(sessionId);
        return session != null && session.isActive();
    }
    
    /**
     * Update session activity
     */
    public void updateActivity(String sessionId) {
        UserSession session = sessions.get(sessionId);
        if (session != null) {
            session.updateActivity();
        }
    }
    
    /**
     * Terminate session
     */
    public void terminateSession(String sessionId) {
        UserSession session = sessions.remove(sessionId);
        if (session != null) {
            session.terminate();
            userSessions.remove(session.getUsername());
            System.out.println("[SESSION MANAGER] Session terminated: " + sessionId + " (User: " + session.getUsername() + ")");
        }
    }
    
    /**
     * Terminate all sessions for a user
     */
    public void terminateUserSessions(String username) {
        String sessionId = userSessions.remove(username);
        if (sessionId != null) {
            terminateSession(sessionId);
        }
    }
    
    /**
     * Get all active sessions
     */
    public List<UserSession> getAllActiveSessions() {
        List<UserSession> activeSessions = new ArrayList<>();
        for (UserSession session : sessions.values()) {
            if (session.isActive()) {
                activeSessions.add(session);
            }
        }
        return activeSessions;
    }
    
    /**
     * Get session count
     */
    public int getActiveSessionCount() {
        return (int) sessions.values().stream().filter(UserSession::isActive).count();
    }
    
    /**
     * Check if user has active session
     */
    public boolean hasActiveSession(String username) {
        UserSession session = getSessionByUsername(username);
        return session != null && session.isActive();
    }
    
    /**
     * Start cleanup task for expired sessions
     */
    private void startCleanupTask() {
        cleanupTimer = new Timer("SessionCleanup", true);
        cleanupTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                cleanupExpiredSessions();
            }
        }, 60000, 60000); // Run every minute
    }
    
    /**
     * Cleanup expired sessions
     */
    private void cleanupExpiredSessions() {
        List<String> expiredSessions = new ArrayList<>();
        
        for (Map.Entry<String, UserSession> entry : sessions.entrySet()) {
            if (entry.getValue().isExpired()) {
                expiredSessions.add(entry.getKey());
            }
        }
        
        for (String sessionId : expiredSessions) {
            terminateSession(sessionId);
        }
        
        if (!expiredSessions.isEmpty()) {
            System.out.println("[SESSION MANAGER] Cleaned up " + expiredSessions.size() + " expired sessions");
        }
    }
    
    /**
     * Shutdown session manager
     */
    public void shutdown() {
        if (cleanupTimer != null) {
            cleanupTimer.cancel();
        }
        sessions.clear();
        userSessions.clear();
        System.out.println("[SESSION MANAGER] Session manager shut down");
    }
}
