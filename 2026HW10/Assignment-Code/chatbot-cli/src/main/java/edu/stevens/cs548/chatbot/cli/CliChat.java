package edu.stevens.cs548.chatbot.cli;

import io.modelcontextprotocol.spec.McpSchema.Prompt;
import io.modelcontextprotocol.spec.McpSchema.PromptMessage;
import java.util.List;
import software.amazon.awssdk.services.bedrockruntime.model.Message;

/**
  * Extends {@link Chat} with:
 *   • document resource fetching (@-mentions)
 *   • slash-command handling (/format, /summarize, …)
 *   • prompt retrieval from the doc MCP client
 */
public class CliChat extends Chat {

    public CliChat(BedrockService bedrockService, Client client) {
        super(bedrockService, client);
    }

    public List<Prompt> listPrompts() throws Exception {
        return client.listPrompts();
    }

    public List<String> listDocIds() throws Exception {
        return client.listResources();
    }

    public String getDocContent(String docId) throws Exception {
        return client.readDocResource(docId);

    }

    /**
     * Scans the query for @-mentions, fetches each mentioned document, and
     * returns them wrapped in XML document tags.
     */
    private String extractResources(String query) throws Exception {
        List<String> docIds = listDocIds();
        StringBuilder sb = new StringBuilder();

        for (String word : query.split("\\s+")) {
            if (word.startsWith("@")) {
                String mention = word.substring(1);
                if (docIds.contains(mention)) {
                    String content = getDocContent(mention);
                    sb.append("\n<document id=\"").append(mention).append("\">\n")
                      .append(content).append("\n</document>\n");
                }
            }
        }
        return sb.toString();
    }

    /**
     * If {@code query} starts with "/", interprets it as a slash command,
     * converts the resulting MCP prompt messages into Bedrock messages, appends
     * them to the conversation, and returns {@code true}.
     * Returns {@code false} for ordinary queries.
     */
    private boolean processCommand(String query) throws Exception {
        if (!query.startsWith("/")) return false;

        String[] parts = query.split("\\s+");
        String command = parts[0].substring(1);  // strip leading "/"
        String docId = parts.length > 1 ? parts[1] : "";

        List<PromptMessage> promptMessages = client.getPrompt(command, docId);
        List<Message> bedrockMessages = BedrockService.toBedrockMessages(promptMessages);
        messages.addAll(bedrockMessages);
        return true;
    }

    @Override
    protected void processQuery(String query) {
        try {
            if (processCommand(query)) return;

            String addedResources = extractResources(query);

            String prompt = """
                    The user has a question:
                    <query>
                    %s
                    </query>

                    The following context may be useful in answering their question:
                    <context>
                    %s
                    </context>

                    Note the user's query might contain references to documents like "@report.docx". \
                    The "@" is only included as a way of mentioning the doc. The actual name of the \
                    document would be "report.docx". If the document content is included in this prompt, \
                    you don't need to use an additional tool to read the document.
                    Answer the user's question directly and concisely. Start with the exact information \
                    they need. Don't refer to or mention the provided context in any way - just use it \
                    to inform your answer.
                    """.formatted(query, addedResources);

            bedrockService.addUserMessage(messages, prompt);

        } catch (Exception e) {
            // Fallback: treat as a plain message so the chat loop can continue
            bedrockService.addUserMessage(messages, query);
        }
    }
}
