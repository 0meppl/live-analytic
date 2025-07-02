package com.example.realtimedashboard.google;

// This is a simplified representation. A real response is more complex.
// See Actions SDK documentation for full details.
// https://developers.google.com/assistant/conversational/reference/rest/Shared.Types/AppResponse
public class GoogleAssistantResponse {

    private Prompt prompt;
    // We can add scene, session, user fields if needed for more complex interactions

    public GoogleAssistantResponse(String speechText) {
        this.prompt = new Prompt();
        this.prompt.setFirstSimple(new SimpleMessage(speechText));
    }

    // Getters and Setters
    public Prompt getPrompt() { return prompt; }
    public void setPrompt(Prompt prompt) { this.prompt = prompt; }


    // Inner classes for nested structure
    public static class Prompt {
        private SimpleMessage firstSimple;
        // Can also have content, lastSimple, etc.

        public SimpleMessage getFirstSimple() { return firstSimple; }
        public void setFirstSimple(SimpleMessage firstSimple) { this.firstSimple = firstSimple; }
    }

    public static class SimpleMessage {
        private String speech;
        // Can also have text

        public SimpleMessage(String speech) {
            this.speech = speech;
        }

        public String getSpeech() { return speech; }
        public void setSpeech(String speech) { this.speech = speech; }
    }
}
