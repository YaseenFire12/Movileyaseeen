package edu.stevens.cs548.chatbot.cli;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main program for CLI chatbot
 */
public class App implements ILogger  {

    public static final String APP_PROPERTIES = "/app.properties";

    private static final String BEDROCK_REGION_PROPERTY = "bedrock.region";

    private static final String BEDROCK_MODEL_ID_PROPERTY = "bedrock.model-id";

    private static final String TOOL_SERVER_URL_PROPERTY = "tool-server.url";
    
    private static final Logger logger = Logger.getLogger(App.class.getCanonicalName());


    private String bedrockRegion;

    private String bedrockModelId;

    private URI toolServerUrl;


    public static void msg(String m) {
        System.out.print(m);
    }

    public static void msgln(String m) {
        System.out.println(m);
    }

    public static void err(String s) {
        System.err.println("** " + s);
    }

    public static void severe(Exception e) {
        logger.log(Level.SEVERE, "Error during processing!", e);
    }

    public void severe(String s) {
        logger.severe(s);
    }

    public void warning(String s) {
        logger.info(s);
    }

    public void info(String s) {
        logger.info(s);
    }


    public static void main(String[] args) {
        try {
            new App(args);
        }  catch (Exception ex) {
            severe(ex);
        }
    }

    protected void loadProperties() {
        /*
         * Load default properties.
         */
        try {
            Properties props = new Properties();
            InputStream propsIn = getClass().getResourceAsStream(APP_PROPERTIES);
            props.load(propsIn);
            Objects.requireNonNull(propsIn).close();

            bedrockRegion = props.getProperty(BEDROCK_REGION_PROPERTY);
            bedrockModelId = props.getProperty(BEDROCK_MODEL_ID_PROPERTY);
            toolServerUrl = URI.create(props.getProperty(TOOL_SERVER_URL_PROPERTY));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load properties from "+APP_PROPERTIES, e);
        }
    }

    protected void processArgs(String[] args) {
        /*
         * Default properties may be overridden on the command line.
         */
        List<String> commandLineArgs = new ArrayList<String>();
        int ix = 0;
        Hashtable<String, String> opts = new Hashtable<String, String>();

        while (ix < args.length) {
            if (args[ix].startsWith("--")) {
                String option = args[ix++].substring(2);
                if (ix == args.length || args[ix].startsWith("--"))
                    severe("Missing argument for --" + option + " option.");
                else if (opts.containsKey(option))
                    severe("Option \"" + option + "\" already set.");
                else
                    opts.put(option, args[ix++]);
            } else {
                commandLineArgs.add(args[ix++]);
            }
        }
        /*
         * Overrides of values from configuration file.
         */
        Enumeration<String> keys = opts.keys();
        while (keys.hasMoreElements()) {
            String k = keys.nextElement();
            if ("region".equals(k))
                bedrockRegion = opts.get("region");
            else if ("model".equals(k))
                bedrockModelId = opts.get("model");
            else if ("tools".equals(k))
                toolServerUrl = URI.create(opts.get("tools"));
            else
                severe("Unrecognized option: --" + k);
        }

        for (String arg : commandLineArgs) {
            err("Unrecognized command line argument: " + arg);
        }
    }



    public App(String[] args) throws Exception {

        loadProperties();

        processArgs(args);

        BedrockService bedrockService = new BedrockService(bedrockRegion, bedrockModelId);

        Client toolClient = new Client(this, toolServerUrl.toString());

        try (toolClient) {
            toolClient.connect();
            CliChat cliChat = new CliChat(bedrockService, toolClient);
            CliApp cliApp = new CliApp(cliChat);
            cliApp.initialize();
            cliApp.run();
        }
    }
}
