import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to the Kotlin-to-Java Code Converter!");
        System.out.println("Enter your Kotlin code line by line. Type 'END' on a new line to finish.\n");

        String kotlinCode = collectKotlinCode();
        String javaCode = convertKotlinToJavaCode(kotlinCode);

        if (javaCode != null) {
            displayJavaCode(javaCode);
        } else {
            System.err.println("Conversion failed. Please check your input.");
        }
    }

    private static String collectKotlinCode() {
        Scanner scanner = new Scanner(System.in);
        StringBuilder kotlinCode = new StringBuilder();
        String line;

        System.out.println("Enter Kotlin code:");
        while (!(line = scanner.nextLine()).equalsIgnoreCase("END")) {
            kotlinCode.append(line).append("\n");
        }
        return kotlinCode.toString();
    }

    private static String convertKotlinToJavaCode(String kotlinCode) {
        try {
            return KotlinToJavaInterpreter.convertKotlinToJava(kotlinCode);
        } catch (Exception e) {
            System.err.println("Error during conversion: " + e.getMessage());
            return null;
        }
    }

    private static void displayJavaCode(String javaCode) {
        System.out.println("\nConverted Java Code:\n");
        System.out.println(javaCode);
    }

}
