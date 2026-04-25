package edu.stevens.cs548.chatbot.cli;

import io.modelcontextprotocol.spec.McpSchema.Prompt;
import org.jline.reader.*;
import org.jline.reader.impl.DefaultHighlighter;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 * JLine3 provides interactive REPL features:
 *   • Tab-completion for / commands and @ resource mentions
 *   • Arrow-key history navigation
 *   • Auto-suggest (inline ghost text)
 */
public class CliApp {

    private final CliChat agent;
    private List<String> resources = new ArrayList<>();
    private List<Prompt> prompts = new ArrayList<>();

    private LineReader reader;

    public CliApp(CliChat agent) {
        this.agent = agent;
    }


    public void initialize() throws Exception {
        refreshResources();
        refreshPrompts();
        buildReader();
    }

    public void run() {
        App.msgln("MCP Chat ready. Type a message, /command <doc_id>, or @doc to mention a document.");
        App.msgln("Press Ctrl-C to exit.\n");

        while (true) {
            String userInput;
            try {
                userInput = reader.readLine("cs548> ");
            } catch (UserInterruptException | EndOfFileException e) {
                App.msgln("\nGoodbye.");
                break;
            }

            if (userInput == null || userInput.isBlank()) continue;

            try {
                String response = agent.run(userInput.trim());
                App.msgln("\nResponse:\n" + response + "\n");
            } catch (Exception e) {
                App.err("Error: " + e.getMessage());
            }
        }
    }

    private void refreshResources() {
        try {
            resources = agent.listDocIds();
        } catch (Exception e) {
            App.err("Error refreshing resources: " + e.getMessage());
        }
    }

    private void refreshPrompts() {
        try {
            prompts = agent.listPrompts();
        } catch (Exception e) {
            App.err("Error refreshing prompts: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------------
    // JLine3 reader setup
    // -----------------------------------------------------------------------

    private void buildReader() throws Exception {
        Terminal terminal = TerminalBuilder.builder()
                .system(true)
                .build();

        McpCompleter completer = new McpCompleter(prompts, resources);

        reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .completer(completer)
                .highlighter(new DefaultHighlighter())
                .history(new DefaultHistory())
                .option(LineReader.Option.AUTO_FRESH_LINE, true)
                .build();
    }


    /**
     * JLine3 {@link Completer}:
     * <ul>
     *   <li>After {@code /}: completes slash-command names.</li>
     *   <li>After {@code /cmd }: completes document IDs as the argument.</li>
     *   <li>After {@code @}: completes resource (document) IDs.</li>
     * </ul>
     */
    private static class McpCompleter implements Completer {

        private final List<Prompt> prompts;
        private final List<String> resources;

        McpCompleter(List<Prompt> prompts, List<String> resources) {
            this.prompts   = prompts;
            this.resources = resources;
        }

        @Override
        public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
            String buffer = line.line().substring(0, line.cursor());

            // ---- @-mention completion ----------------------------------------
            int atPos = buffer.lastIndexOf('@');
            if (atPos >= 0) {
                String prefix = buffer.substring(atPos + 1);
                for (String id : resources) {
                    if (id.toLowerCase().startsWith(prefix.toLowerCase())) {
                        candidates.add(new Candidate(id, id, null, "Resource", null, null, true));
                    }
                }
                return;
            }

            // ---- Slash-command completion ------------------------------------
            if (buffer.startsWith("/")) {
                String[] parts = buffer.substring(1).split(" ", -1);

                if (parts.length == 1) {
                    // Complete command name
                    String cmdPrefix = parts[0];
                    for (Prompt p : prompts) {
                        if (p.name().startsWith(cmdPrefix)) {
                            candidates.add(new Candidate(
                                    p.name(), "/" + p.name(),
                                    null, p.description(), null, null, true));
                        }
                    }
                    return;
                }

                if (parts.length == 2) {
                    // Complete document-id argument
                    String docPrefix = parts[1];
                    for (String id : resources) {
                        if (id.toLowerCase().startsWith(docPrefix.toLowerCase())) {
                            candidates.add(new Candidate(id, id, null, "Document", null, null, true));
                        }
                    }
                }
            }
        }
    }
}
