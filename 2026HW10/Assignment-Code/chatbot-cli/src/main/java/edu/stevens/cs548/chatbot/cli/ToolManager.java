package edu.stevens.cs548.chatbot.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.Content;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.services.bedrockruntime.model.ContentBlock;
import software.amazon.awssdk.services.bedrockruntime.model.ToolResultBlock;
import software.amazon.awssdk.services.bedrockruntime.model.ToolResultContentBlock;
import software.amazon.awssdk.services.bedrockruntime.model.ToolResultStatus;
import software.amazon.awssdk.services.bedrockruntime.model.ToolUseBlock;

/**
 * Discovers tools across all MCP clients and dispatches tool-use requests
 * from the Bedrock model back to the appropriate client.
 */
public class ToolManager {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Iterates over the ContentBlocks returned by Bedrock, finds every
     * {@code toolUse} block, dispatches to the matching MCP client, and
     * returns a list of {@code toolResult} ContentBlocks to send back.
     */
    public static List<ContentBlock> executeToolRequests(
            Client client,
            List<ContentBlock> parts) throws Exception {

        List<ContentBlock> results = new ArrayList<>();

        for (ContentBlock part : parts) {
            if (part.toolUse() != null) {
                ToolUseBlock toolUse = part.toolUse();
                String toolUseId = toolUse.toolUseId();
                String toolName = toolUse.name();
                Map<String, Object> toolInput = extractInput(toolUse);

                try {
                    CallToolResult toolOutput = client.callTool(toolName, toolInput);
                    List<ToolResultContentBlock> partResult = extractContentText(toolOutput);
                    results.add(buildToolResult(toolUseId, partResult, toolOutput.isError()));
                } catch (Exception e) {
                    String errorMsg = "Error executing tool " + toolName + ": " + e.getMessage();
                    App.err(errorMsg);
                    results.add(buildToolResult(toolUseId, List.of(ToolResultContentBlock.fromText(errorMsg)), true));
                }
            }
        }

        return results;
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private static Map<String, Object> extractInput(ToolUseBlock toolUse) {
        try {
            // The AWS SDK represents tool input as a Document (JSON-like structure)
            String json = MAPPER.writeValueAsString(toolUse.input().unwrap());
            return MAPPER.readValue(json, Map.class);
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    private static List<ToolResultContentBlock> extractContentText(CallToolResult toolOutput) {

        List<ToolResultContentBlock> results = new ArrayList<>();
        List<Content> content = toolOutput.content();
        for (Content part : content) {
            switch (part) {
                case TextContent tx -> {
                    results.add(ToolResultContentBlock.fromText(tx.text()));
                }
                default -> { throw new IllegalArgumentException("Unknown content type: " + part.type()); }
            }
        }
        return results;
    }

    private static ContentBlock buildToolResult(
            String toolUseId, List<ToolResultContentBlock> result, boolean isError) {

        ToolResultStatus status = isError
                ? ToolResultStatus.ERROR
                : ToolResultStatus.SUCCESS;

        return ContentBlock.fromToolResult(
                ToolResultBlock.builder()
                        .toolUseId(toolUseId)
                        .status(status)
                        .content(result)
                        .build());
    }
}
