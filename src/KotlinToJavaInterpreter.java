import java.util.Scanner;

public class KotlinToJavaInterpreter {
    private static final StringBuilder javaCode = new StringBuilder();
    private static int openBraces = 0;

    public static String convertKotlinToJava(String kotlinCode) {
        String[] lines = kotlinCode.split("\\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
        }
    }

  private static void handleImport(String line) {
        javaCode.append(line.replace("import", "import").trim()).append(";\n");
    }

  private static void handleFunctionDefinition(String line) {
        // Step 1: Extract function name
        String functionName = line.split("\\(")[0].replace("fun", "").trim();

        // Step 2: Extract return type
        String returnType = "void";  // Default return type is void
        if (line.contains(":")) {
            String[] parts = line.split(":");
            returnType = parts[1].trim().split("\\s")[0]  // Take the first word after ":"
                    .replace("Int", "int")
                    .replace("Boolean", "boolean")
                    .replace("String", "String");
        }

        // Step 3: Extract parameters and format them for Java
        String params = "";
        if (line.contains("(") && line.contains(")")) {
            params = line.substring(line.indexOf('(') + 1, line.indexOf(')')).trim();
            if (!params.isEmpty()) {
                String[] paramList = params.split(",");
                StringBuilder formattedParams = new StringBuilder();
                for (int i = 0; i < paramList.length; i++) {
                    String param = paramList[i].trim();
                    String[] paramParts = param.split(":");
                    if (paramParts.length == 2) {
                        String paramName = paramParts[0].trim();
                        String paramType = paramParts[1].trim()
                                .replace("Int", "int")
                                .replace("Boolean", "boolean")
                                .replace("String", "String");
                        formattedParams.append(paramType).append(" ").append(paramName);
                        if (i < paramList.length - 1) {  // Check if it's not the last parameter
                            formattedParams.append(", ");
                        }
                    }
                }
                params = formattedParams.toString();
            }
        }

        // Clean up any malformed return type right before appending
        returnType = returnType.replaceAll("\\)", "");

        // Append the corrected function signature to the javaCode StringBuilder
        javaCode.append("public static ").append(returnType).append(" ").append(functionName)
                .append("(").append(params).append(") {\n");
        increaseBraceCount();
    }

     private static void handleVariableDeclaration(String line) {
        String varType = line.startsWith("val") ? "final " : "";
        line = line.replace("val", "").replace("var", "").trim();
        String[] parts = line.split("=");
        String name = parts[0].trim();
        String value = parts.length > 1 ? parts[1].trim() : "";

        String type = "int"; // Default type
        if (value.matches("\".*\"")) {
            type = "String";
        } else if (value.matches("true|false")) {
            type = "boolean";
        }

        if (value.contains("readLine")) {
            value = "Integer.parseInt(scanner.nextLine())";
        } else if (value.contains(" ?: ")) {
            String[] coalesceParts = value.split("\\?:");
            value = coalesceParts[0].trim() + " != null ? " + coalesceParts[0].trim() + " : " + coalesceParts[1].trim();
        }

        javaCode.append(varType).append(type).append(" ").append(name).append(" = ").append(value).append(";\n");
    }

    private static void handleForLoop(String line) {
        if (line.contains("..")) {
            line = line.replace("for (", "for (int ")
                    .replace("in ", "= ")
                    .replace("..", "; i <= ")
                    .replace(") {", "; i++) {");
        } else if (line.contains("downTo")) {
            line = line.replace("for (", "for (int ")
                    .replace("in ", "= ")
                    .replace("downTo", "; i >= ")
                    .replace(") {", "; i--) {");
        }
        javaCode.append(line).append("\n");
        increaseBraceCount();
    }

    private static void handlePrint(String line) {
        line = line.replace("println(", "System.out.println(");
        while (line.contains("$")) {
            if (line.contains("{")) {
                line = line.replaceFirst("\\$\\{", "\" + (").replaceFirst("\\}", ") + \"");
            } else {
                line = line.replaceFirst("\\$", "\" + ").replaceFirst(" ", " + \"");
            }
        }
        line = line.replace(")", ");");
        javaCode.append(line).append("\n");
    }

    private static void handleInput(String line) {
        if (!javaCode.toString().contains("Scanner scanner")) {
            javaCode.append("Scanner scanner = new Scanner(System.in);\n");
        }
    }

    private static void handleDefault(String line) {
        // Handle closing braces
        if (line.equals("}")) {
            javaCode.append(line).append("\n");
            decreaseBraceCount();
        } else {
            // Split the line into code and comment parts
            String[] parts = line.split("//", 2);
            String codePart = parts[0].trim();
            String commentPart = parts.length > 1 ? "// " + parts[1].trim() : "";

            // Add a semicolon if needed
            if (!codePart.isEmpty() && !codePart.endsWith("{") && !codePart.endsWith("}") && !codePart.endsWith(";")) {
                codePart += ";";
            }

            // Append the code and comment parts
            if (!codePart.isEmpty()) {
                javaCode.append(codePart);
            }
            if (!commentPart.isEmpty()) {
                javaCode.append(" ").append(commentPart);
            }
            javaCode.append("\n");

            // Track opening braces
            if (codePart.endsWith("{")) {
                increaseBraceCount();
            }
        }
    }
