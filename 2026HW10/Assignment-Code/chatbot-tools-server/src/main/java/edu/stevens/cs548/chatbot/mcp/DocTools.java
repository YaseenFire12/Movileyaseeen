package edu.stevens.cs548.chatbot.mcp;

import com.fasterxml.jackson.annotation.ObjectIdGenerators.StringIdGenerator;
import edu.stevens.cs548.chatbot.mcp.service.DocService;
import io.quarkiverse.mcp.server.JsonRpcErrorCodes;
import io.quarkiverse.mcp.server.McpException;
import io.quarkiverse.mcp.server.Prompt;
import io.quarkiverse.mcp.server.PromptArg;
import io.quarkiverse.mcp.server.PromptMessage;
import io.quarkiverse.mcp.server.RequestUri;
import io.quarkiverse.mcp.server.Resource;
import io.quarkiverse.mcp.server.ResourceTemplate;
import io.quarkiverse.mcp.server.ResourceTemplateArg;
import io.quarkiverse.mcp.server.TextContent;
import io.quarkiverse.mcp.server.TextResourceContents;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import io.quarkiverse.mcp.server.ToolResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.List;
import org.jboss.logging.Logger;

@Singleton
public class DocTools {

    @Inject
    DocService docService;

    @Inject
    Logger logger;

    @Tool(description = "Read the contents of a document and return it as a string.")
    ToolResponse readDocument(@ToolArg(description = "Id of the document to read") String docId) {
        logger.infof("Reading document %s", docId);
        return ToolResponse.success(new TextContent(docService.getDocument(docId)));
    }

    @Tool(description = "Edit a document by replacing a string in the documents content with a new string")
    ToolResponse editDocument(
            @ToolArg(description = "Id of the document that will be edited") String docId,
            @ToolArg(description = "The text to replace. Must match exactly, including whitespace") String oldStr,
            @ToolArg(description = "The new text to insert in place of the old text") String newStr) {
        logger.infof("Editing document %s", docId);
        String content = docService.editDocument(docId, oldStr, newStr);
        if (content == null) {
            throw new McpException("No such document: "+docId, JsonRpcErrorCodes.RESOURCE_NOT_FOUND);
        }
        return ToolResponse.success();
    }

    @Resource(uri = "doc://contents")
    List<String> documents() {
        logger.infof("Listing resource of all documents");
        return docService.listDocuments();
    }

    @ResourceTemplate(uriTemplate = "doc://contents/{name}")
    TextResourceContents document(String name, RequestUri uri) {
        logger.infof("Reading document resource %s", name);
        String content = docService.getDocument(name);
        if (content == null) {
            throw new McpException("No such document: "+name, JsonRpcErrorCodes.RESOURCE_NOT_FOUND);
        }
        return TextResourceContents.create(uri.value(), content);
    }

    @Prompt(description = "Rewrites the contents of the document in Markdown format.")
    PromptMessage format(@PromptArg(name = "doc_id", description = "The name of the document.") String docId) {
        logger.infof("Formatting document %s", docId);
        return PromptMessage.withUserRole(new TextContent(docService.formatPrompt().replace("{docId}", docId)));

    }

    @Prompt(description = "Summarizes the content of the document.")
    PromptMessage summarize(@PromptArg(name = "doc_id", description = "The name of the document.") String docId) {
        logger.infof("Summarizing document %s", docId);
        return PromptMessage.withUserRole(new TextContent(docService.summarizePrompt().replace("{docId}", docId)));
    }
}
