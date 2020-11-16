package fr.insalyon.stream;

public class ChatHistory {
    StringBuilder history;

    public ChatHistory(){
        history = new StringBuilder();
    }

    public void addMessageToHistory(String message){
        history.append(message + '\n');
    }

    public String getHistoryAsString(){
        return history.toString();
    }

}
