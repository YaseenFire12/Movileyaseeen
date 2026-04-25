package edu.stevens.cs548.chatbot.cli;

import java.util.ArrayList;
import java.util.List;
import software.amazon.awssdk.services.bedrockruntime.model.ContentBlock;
import software.amazon.awssdk.services.bedrockruntime.model.Message;
import software.amazon.awssdk.services.bedrockruntime.model.StopReason;

/**
 * Maintains the running conversation and drives the tool-use loop:
 * call Bedrock → if tool_use, dispatch tools and continue → else return.
 */
public class Chat {

    protected final BedrockService bedrockService;
    protected final Client client;
    protected final List<Message> messages = new ArrayList<>();

    public Chat(BedrockService bedrockService, Client client) {
        this.bedrockService = bedrockService;
        this.client = client;
    }

    // -----------------------------------------------------------------------
    // _process_query — override in subclasses to customise message building
    // -----------------------------------------------------------------------

    /**
     * Appends the user turn to {@code messages}. Subclasses can override to
     * inject extra context (e.g. document resources, commands).
     */
    protected void processQuery(String query) {
        bedrockService.addUserMessage(messages, query);
    }

    /**
     * Runs one complete user turn, including any tool-use rounds, and returns
     * the final text response from the model.
     */
    public String run(String query) throws Exception {
        processQuery(query);

        String finalTextResponse = "";

        while (true) {

            BedrockService.ChatResponse response = null;
            // TODO perform one query on the model using Bedrock


            // TODO Add the assistant turn to history

            if (StopReason.TOOL_USE.equals(response.stopReason())) {
                client.getLogger().info("The model has requested the use of some tools.");

                // Print any partial text the model emitted alongside tool calls
                if (!response.text().isBlank()) {
                    App.msgln(response.text());
                }

                // TODO Execute every tool the model requested

                // TODO Feed results back as the next user turn

            } else {
                finalTextResponse = response.text();
                break;
            }
        }

        return finalTextResponse;
    }
}
