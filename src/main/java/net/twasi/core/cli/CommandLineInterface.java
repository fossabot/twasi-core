package net.twasi.core.cli;

import net.twasi.core.logger.TwasiLogger;

import java.util.Scanner;

public class CommandLineInterface {

    public void start() {
        Scanner scanner = new Scanner(System.in);
        TwasiLogger.log.info("Started Twasi CLI. Use /help for a list of commands.");

        while(true) {
            System.out.print("> ");
            String input = scanner.nextLine();

            switch (input) {
                case "/help":
                    System.out.println("Available commands:\n" +
                    "/help: Show all commands");
                    break;
            }
        }
    }

}
