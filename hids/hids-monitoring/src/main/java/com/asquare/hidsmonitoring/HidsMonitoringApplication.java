package com.asquare.hidsmonitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.nio.file.WatchEvent.Kind;
import java.util.logging.*;

@SpringBootApplication
public class HidsMonitoringApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(HidsMonitoringApplication.class, args);
        System.out.println("HIDS Monitoring Application started!!");

        captureFileSystemEvents();
        captureProcessEvents();
        captureNetworkEvents();
        captureLogEntries();

        System.out.println("HIDS Monitoring Application stopped.");
    }

    private static void captureFileSystemEvents() throws IOException {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path rootDirectory = FileSystems.getDefault().getRootDirectories().iterator().next();
        rootDirectory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);

        System.out.println("Capturing file system events...");
        while (true) {
            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException e) {
                return;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                Kind<?> kind = event.kind();
                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }

                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    System.out.println("File created: " + event.context());
                } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                    System.out.println("File modified: " + event.context());
                } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                    System.out.println("File deleted: " + event.context());
                }
            }

            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }

    private static void captureProcessEvents() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("wmic", "process", "get", "ProcessId,Name,CommandLine", "/format:csv");
        Process process = processBuilder.start();
    
        System.out.println("Capturing process events...");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
    
            int exitCode = process.waitFor();
            System.out.println("Process terminated with exit code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void captureNetworkEvents() throws IOException {
        // Code for capturing network events
        System.out.println("Capturing network events...");

        // Monitor network connections
        Thread connectionMonitorThread = new Thread(() -> {
            try {
                while (true) {
                    // Your network monitoring logic goes here
                    // Example: Print the active network connections
                    System.out.println("Active network connections:");
                    Process netstatProcess = Runtime.getRuntime().exec("netstat -ano");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(netstatProcess.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                    reader.close();

                    Thread.sleep(5000); // Wait for 5 seconds before checking again
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        connectionMonitorThread.start();

        // Capture incoming/outgoing packets
        // Your packet capturing logic goes here
    }


    private static void captureLogEntries() throws IOException {
        // Code for capturing log entries
        System.out.println("Capturing log entries...");

        Logger logger = Logger.getLogger("com.asquare.hidsmonitoring");
        logger.setLevel(Level.ALL);

        // Create a console handler to print log entries to the console
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        logger.addHandler(consoleHandler);

        // Create a file handler to write log entries to a log file
        Handler fileHandler = new FileHandler("C:/Users/Admin/Documents/hids/file.log");
        fileHandler.setLevel(Level.ALL);
        logger.addHandler(fileHandler);

        // Log a sample message
        logger.info("Sample log entry");

        // Remember to close the file handler when done
        fileHandler.close();
    }
}
