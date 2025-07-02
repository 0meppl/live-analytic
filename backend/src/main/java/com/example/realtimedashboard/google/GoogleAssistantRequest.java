package com.example.realtimedashboard.google;

// This is a simplified representation. A real request is more complex.
// See Actions SDK documentation for full details.
// https://developers.google.com/assistant/conversational/reference/rest/Shared.Types/AppRequest
public class GoogleAssistantRequest {
    private String handlerName; // The handler (intent) name
    private Intent intent;
    private User user;
    private Session session;

    // Getters and Setters
    public String getHandlerName() { return handlerName; }
    public void setHandlerName(String handlerName) { this.handlerName = handlerName; }

    public Intent getIntent() { return intent; }
    public void setIntent(Intent intent) { this.intent = intent; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Session getSession() { return session; }
    public void setSession(Session session) { this.session = session; }

    // Inner classes for nested structures
    public static class Intent {
        private String name; // e.g., "actions.intent.MAIN", "GetTemperature"
        // Parameters would go here if any
        // private Map<String, Object> params;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class User {
        private String locale;
        // Other user properties
        public String getLocale() { return locale; }
        public void setLocale(String locale) { this.locale = locale; }
    }

    public static class Session {
        private String id;
        // Session params
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
    }

    @Override
    public String toString() {
        return "GoogleAssistantRequest{" +
               "handlerName='" + (handlerName != null ? handlerName : (intent != null ? intent.getName() : "N/A")) + '\'' +
               ", intent=" + (intent != null ? intent.getName() : "null") +
               ", userLocale=" + (user != null ? user.getLocale() : "null") +
               ", sessionId=" + (session != null ? session.getId() : "null") +
               '}';
    }
}
