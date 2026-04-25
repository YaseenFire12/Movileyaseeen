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
// TODO add annotations
public class DocTools {

    @Inject
    DocService docService;

    @Inject
    Logger logger;

    ToolResponse readDocument(String docId) {
        logger.infof("Reading document %s", docId);
        return ToolResponse.success(new TextContent(docService.getDocument(docId)));
    }

    ToolResponse editDocument(String docId,
                              String oldStr,
                              String newStr) {
        logger.infof("Editing document %s", docId);
        String content = docService.editDocument(docId, oldStr, newStr);
        if (content == null) {
            throw new McpException("No such document: "+docId, JsonRpcErrorCodes.RESOURCE_NOT_FOUND);
        }
        return ToolResponse.success();
    }

    List<String> documents() {
        logger.infof("Listing resource of all documents");
        return docService.listDocuments();
    }

    TextResourceContents dpcument(String name, RequestUri uri) {
        logger.infof("Reading document resource %s", name);
        String content = docService.getDocument(name);
        if (content == null) {
            throw new McpException("No such document: "+name, JsonRpcErrorCodes.RESOURCE_NOT_FOUND);
        }
        return TextResourceContents.create(uri.value(), content);
    }

    PromptMessage format(String docId) {
        logger.infof("Formatting document %s", docId);
        return PromptMessage.withUserRole(new TextContent(docService.formatPrompt()));
    }

    PromptMessage summarize(String docId) {
        logger.infof("Summarizing document %s", docId);
        return PromptMessage.withUserRole(new TextContent(docService.summarizePrompt()));
    }
}
