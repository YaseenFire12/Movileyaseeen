package edu.stevens.cs548.chatbot.cli;

import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.document.Document;
import software.amazon.awssdk.core.document.Document.MapBuilder;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.AnyToolChoice;
import software.amazon.awssdk.services.bedrockruntime.model.AutoToolChoice;
import software.amazon.awssdk.services.bedrockruntime.model.ContentBlock;
import software.amazon.awssdk.services.bedrockruntime.model.ConversationRole;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseRequest;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseResponse;
import software.amazon.awssdk.services.bedrockruntime.model.Message;
import software.amazon.awssdk.services.bedrockruntime.model.SpecificToolChoice;
import software.amazon.awssdk.services.bedrockruntime.model.StopReason;
import software.amazon.awssdk.services.bedrockruntime.model.SystemContentBlock;
import software.amazon.awssdk.services.bedrockruntime.model.Tool;
import software.amazon.awssdk.services.bedrockruntime.model.ToolChoice;
import software.amazon.awssdk.services.bedrockruntime.model.ToolConfiguration;
import software.amazon.awssdk.services.bedrockruntime.model.ToolInputSchema;
import software.amazon.awssdk.services.bedrockruntime.model.ToolSpecification;

/**
 * Wraps the AWS Bedrock Runtime "Converse" API.
 *
 * The access key for the IAM user should be set in environment variables
 * (AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY) or the AWS CLI should be
 * configured with them (aws configure).
 */
public class BedrockService {

    private final BedrockRuntimeClient client;
    private final String modelId;

    public BedrockService(String regionName, String modelId) {
        this.client = BedrockRuntimeClient.builder()
                .region(Region.of(regionName))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
        this.modelId = modelId;
    }

    /** Appends a user message (string shorthand) to the conversation list. */
    public void addUserMessage(List<Message> messages, String content) {
        messages.add(Message.builder()
                .role(ConversationRole.USER)
                .content(ContentBlock.fromText(content))
                .build());
    }

    /** Appends a user message from a pre-built list of ContentBlocks. */
    public void addUserMessage(List<Message> messages, List<ContentBlock> blocks) {
        messages.add(Message.builder()
                .role(ConversationRole.USER)
                .content(blocks)
                .build());
    }

    /** Appends an assistant message (string shorthand). */
    public void addAssistantMessage(List<Message> messages, String content) {
        messages.add(Message.builder()
                .role(ConversationRole.ASSISTANT)
                .content(ContentBlock.fromText(content))
                .build());
    }

    /** Appends an assistant message from a pre-built list of ContentBlocks. */
    public void addAssistantMessage(List<Message> messages, List<ContentBlock> blocks) {
        messages.add(Message.builder()
                .role(ConversationRole.ASSISTANT)
                .content(blocks)
                .build());
    }

    /**
     * Calls the Bedrock Converse endpoint and returns a {@link ChatResponse}
     * containing the raw content blocks, the stop reason, and the joined text.
     */
    public ChatResponse chat(
            List<Message> messages,
            String system,
            float temperature,
            List<Tool> tools,
            String toolChoice) {

        List<ContentBlock> parts = null;
        StopReason stopReason = null;
        StringBuilder text = new StringBuilder();  // to collapse result content to single string

        // TODO invoke the model via the Converse API

        // End TODO

        return new ChatResponse(parts, stopReason, text.toString());
    }

    /** Convenience overload with defaults */
    public ChatResponse chat(List<Message> messages, List<Tool> tools) {
        return chat(messages, null, 1.0f, tools, "auto");
    }

    /**
     * Converts a list of MCP PromptMessages to Bedrock SDK Message objects.
     */
    private static ContentBlock toContentBlock(McpSchema.Content content) {
        switch (content) {
            case TextContent tx -> { return ContentBlock.fromText(tx.text()); }
            default -> throw new IllegalArgumentException("Invalid content type: " + content.type());
        }
    }

    public static List<Message> toBedrockMessages(List<McpSchema.PromptMessage> promptMessages) {
        List<Message> out = new ArrayList<>();
        for (McpSchema.PromptMessage pm : promptMessages) {
            ConversationRole role = "assistant".equals(pm.role().name())
                    ? ConversationRole.ASSISTANT
                    : ConversationRole.USER;
            out.add(Message.builder()
                    .role(role)
                    .content(toContentBlock(pm.content()))
                    .build());
        }
        return out;
    }

    /**
     * Converts a JSON schema to AWS SDK Document
     */
    private static Document toBedrockDocument(Object schema) {
        switch(schema) {
            case String s -> { return Document.fromString(s); }
            case List<?> xs -> {
                List<Document> docs = new ArrayList<>();
                for (Object x : xs) {
                    docs.add(toBedrockDocument(x));
                }
                return Document.fromList(docs);
            }
            case Map<?,?> map -> {
                MapBuilder mb = Document.mapBuilder();
                for (Map.Entry<?, ?> m : map.entrySet()) {
                    mb.putDocument((String)m.getKey(), toBedrockDocument(m.getValue()));
                }
                return mb.build();
            }
            default -> throw new IllegalArgumentException("Invalid schema: " + schema);
        }
    }

    /**
     * Converts a list of MCP Tools to Bedrock SDK Tool objects.
     */
    public static List<Tool> toBedrockTools(List<McpSchema.Tool> mcpTools) {
        List<Tool> out = new ArrayList<>();
        for (McpSchema.Tool t : mcpTools) {

            Document schemaDocument = Document.mapBuilder()
                    .putString("type", "object")
                    .putDocument("properties", toBedrockDocument(t.inputSchema().properties()))
                    .putDocument("required", toBedrockDocument(t.inputSchema().required()))
                    .putBoolean("additionalProperties", false)
                    .build();

            ToolInputSchema inputSchema = ToolInputSchema.builder()
                    .json(schemaDocument)
                    .build();

            out.add(Tool.builder()
                    .toolSpec(ToolSpecification.builder()
                            .name(t.name())
                            .description(t.description())
                            .inputSchema(inputSchema)
                            .build())
                    .build());
        }
        return out;
    }


    public record ChatResponse(
            List<ContentBlock> parts,
            StopReason stopReason,
            String text) {}
}
