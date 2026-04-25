# MCP Chat 

A simple chat bot based on a Python example developed by Anthropic for a course in AWS Bedrock, augmented with AWS Java SDK (for the Bedrock API), Java SDK for MCP Client and Quarkus MCP Server.


## Prerequisites

- **Java 21** for chatbot-cli (chatbot-tools-server requires Java 17).
- **Maven 3.8+**
- **AWS IAM credentials** configured (env vars AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY, or set access key information in `~/.aws/credentials` by running `aws configure`)
- **MCP server** running via streaming http (chatbot-tools-server)

## Build & Run

Open chatbot-tools-server in Intellij IDEA and run Maven install to compile.  Run this MCP server in development mode in the IDE.   The server will run with base URL http://localhost:8080.

Open chatbot-cli in Intellij IDEA and run Maven install to generate a jar file `chatcli.jar` in `~/tmp/cs548`.  Run this with the command line options below:

```bash
java -jar ~/tmp/cs548/chatcli.jar 
```
The app needs to choose a region and a model, and it needs the MCP server base URL.  Defaults are specified in a properties file in the app, and these can be overwritten with command line options --region, --model and --tools, respectively.

## Usage


| Input | Behaviour |
|-------|-----------|
| `Hello!` | Plain chat with the model |
| `Tell me about @deposition.md` | Fetches doc content, injects as context |
| `/format deposition.md` | Runs the "format" MCP prompt |
| `Tab` after `/` | Auto-completes command names |
| `Tab` after `@` | Auto-completes document IDs |
| `Ctrl-C` | Exit |

#