package edu.stevens.cs548.chatbot.mcp.service;

import jakarta.inject.Singleton;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jboss.logging.Logger;

@Singleton
public class DocService {

    private static final Map<String, String> DOCS;

    static {
        DOCS = new HashMap<>();
        DOCS.put("deposition.md", "This deposition covers the testimony of Angela Smith, P.E.");
        DOCS.put("report.pdf", "The report details the state of a 20m condenser tower.");
        DOCS.put("financials.docx", "These financials outline the project's budget and expenditures.");
        DOCS.put("outlook.pdf", "This document presents the projected future performance of the system.");
        DOCS.put("plan.md", "The plan outlines the steps for the project's implementation.");
        DOCS.put("spec.txt", "These specifications define the technical requirements for the equipment.");
    }

    private final Logger logger;

    public DocService(Logger logger) {
        this.logger = logger;
    }

    public List<String> listDocuments() {
        return DOCS.keySet().stream().toList();
    }

    public String getDocument(String docId) {
        synchronized(DOCS) {
            return DOCS.get(docId);
        }
    }

    public String editDocument(String docId, String oldStr, String newStr) {
        synchronized (DOCS) {
            String content = DOCS.get(docId);
            if (content != null) {
                content = content.replace(oldStr, newStr);
                DOCS.put(docId, content);
            }
            return content;
        }
    }

    private static final String FORMAT_PROMPT = """
                Your goal is to reformat a document to be written with markdown syntax.
            
                The id of the document you need to reformat is:
                <documentId>
                {docId}
                </documentId>
            
                Add in headers, bullet points, tables, etc as necessary. Feel free to add in extra text, but don't change the meaning of the report.
                Use the 'editDocument' tool to edit the document. After the document has been edited, respond with the final version of the doc. Don't explain your changes.
            
            """;

    public String formatPrompt() {
        return FORMAT_PROMPT;
    }

    private static final String SUMMARIZE_PROMPT = """
        Your goal is to summarize the contents of a document.
        
        The id of the document you need to summarize is:
        <documentId>
        {docId}
        </documentId>
        
        Read the document using the 'readDocument' tool, then provide a clear and concise summary of its contents. Focus on the key points and main ideas.
        """;

    public String summarizePrompt() {
        return SUMMARIZE_PROMPT;
    }
}
