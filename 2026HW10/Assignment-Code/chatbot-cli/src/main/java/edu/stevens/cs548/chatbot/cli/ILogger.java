package edu.stevens.cs548.chatbot.cli;

public interface ILogger {

    void info(String message);

    void warning(String message);

    void severe(String message);

}
