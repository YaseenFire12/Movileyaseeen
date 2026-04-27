package edu.stevens.cs548.chatbot.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpClientTransport;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.ClientCapabilities;
import io.modelcontextprotocol.spec.McpSchema.GetPromptRequest;
import io.modelcontextprotocol.spec.McpSchema.GetPromptResult;
import io.modelcontextprotocol.spec.McpSchema.ListPromptsResult;
import io.modelcontextprotocol.spec.McpSchema.ListResourcesResult;
import io.modelcontextprotocol.spec.McpSchema.ListToolsResult;
import io.modelcontextprotocol.spec.McpSchema.Prompt;
import io.modelcontextprotocol.spec.McpSchema.PromptMessage;
import io.modelcontextprotocol.spec.McpSchema.ReadResourceResult;
import io.modelcontextprotocol.spec.McpSchema.Resource;
import io.modelcontextprotocol.spec.McpSchema.ResourceContents;
import io.modelcontextprotocol.spec.McpSchema.Tool;
import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Wrapper around McpClient
 */
public class Client implements Closeable {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String DOC_ID_ARG = "doc_id";

    private static final String SERVER_ENDPOINT = "/tools";

    private static final String RESOURCE_URI_TEMPLATE = "doc://documents/%s";

    private final String baseUrl;

    private final ILogger logger;

    private McpSyncClient client;


    public Client(ILogger logger,  String baseUrl) {
        this.logger = logger;
        this.baseUrl = baseUrl;
    }

    public ILogger getLogger() {
        return logger;
    }

    public void connect() throws IOException {
        McpClientTransport transport = HttpClientStreamableHttpTransport
                .builder(baseUrl)
                .endpoint(SERVER_ENDPOINT)
                .build();
        client = McpClient.sync(transport)
                .requestTimeout(Duration.ofSeconds(10))
                .capabilities(ClientCapabilities.builder()
                        .build())
                .build();
        client.initialize();
    }

    public List<Tool> listTools() {
        ListToolsResult toolsResult = client.listTools();
        return toolsResult.tools();
    }

    public CallToolResult callTool(String toolName, Map<String, Object> toolInput) {
        return client.callTool(new CallToolRequest(toolName, toolInput));
    }

    public List<Prompt> listPrompts() throws IOException {
        ListPromptsResult promptsResult = client.listPrompts();
        return promptsResult.prompts();
    }

    private List<PromptMessage> getPrompt(String promptName, Map<String, Object> args) {
        GetPromptResult promptResult = client.getPrompt(new GetPromptRequest(promptName, args));
        return promptResult.messages();
    }

    public List<PromptMessage> getPrompt(String promptName, String docId) {
        return getPrompt(promptName, Map.of(DOC_ID_ARG, docId));
    }

    public List<String> listResources() {
        ListResourcesResult resourcesResult = client.listResources();
        List<String> resources = new ArrayList<>();
        for (Resource resource : resourcesResult.resources()) {
            resources.add(resource.name());
        }
        return resources;
    }

    public String readResource(String uri)  {
        ReadResourceResult result = client.readResource(Resource.builder().uri(uri).build());
        StringBuilder sb = new StringBuilder();
        for (ResourceContents content : result.contents()) {
            sb.append(content.toString());
        }
        return sb.toString();
    }

    public String readDocResource(String docId) {
        return readResource(String.format(RESOURCE_URI_TEMPLATE, docId));
    }

    @Override
    public void close() {
        client.close();
    }

}
