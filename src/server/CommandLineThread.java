package server;

import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Gregory on 8/6/17.
 */
public class CommandLineThread extends Thread {

    public static LinkedBlockingQueue<String> queuedCommands = new LinkedBlockingQueue<>();

    private Scanner scanner = new Scanner(System.in);

    @Override
    public void run() {
        while(true) {
            try {
                String line = scanner.nextLine();
                queuedCommands.add(line.trim());
            } catch (NoSuchElementException e) {
                System.out.println("Error reading line");
            }
        }
    }
}
