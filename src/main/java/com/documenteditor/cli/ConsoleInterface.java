package com.documenteditor.cli;

import com.documenteditor.adapter.DocumentPersistence;
import com.documenteditor.adapter.DocumentPersistenceAdapter;
import com.documenteditor.command.*;
import com.documenteditor.exporter.*;
import com.documenteditor.factory.*;
import com.documenteditor.model.*;
import com.documenteditor.observer.ConsoleObserver;
import com.documenteditor.strategy.BasicWordCountStrategy;
import com.documenteditor.util.Logger;
import com.documenteditor.visitor.RenderVisitor;
import com.documenteditor.visitor.WordCountVisitor;
import com.documenteditor.cloudstorage.*;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Console-based user interface for the Document Editor.
 * Integrates all design patterns into a cohesive application.
 */
public class ConsoleInterface {
    private Document currentDocument;
    private CommandHistory commandHistory;
    private DocumentPersistence persistence;
    private CloudStorageService cloudStorage;
    private Logger logger;
    private Scanner scanner;
    private ConsoleObserver observer;

    public ConsoleInterface() {
        this.commandHistory = new CommandHistory();
        this.persistence = new DocumentPersistenceAdapter();
        this.logger = Logger.getInstance();
        this.scanner = new Scanner(System.in);
        this.observer = new ConsoleObserver();

        // Initialize cloud storage with Proxy pattern for caching
        CloudStorageService mockStorage = new MockCloudStorageAdapter("Cloud Storage");
        this.cloudStorage = new CloudStorageProxy(mockStorage);
    }

    public void run() {
        displayWelcome();

        boolean running = true;
        while (running) {
            displayMenu();
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> createDocument();
                    case "2" -> addElement();
                    case "3" -> renderDocument();
                    case "4" -> performWordCount();
                    case "5" -> exportDocument();
                    case "6" -> undo();
                    case "7" -> redo();
                    case "8" -> saveDocument();
                    case "9" -> loadDocument();
                    case "10" -> saveToCloud();
                    case "11" -> loadFromCloud();
                    case "12" -> listCloudDocuments();
                    case "0" -> running = false;
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                logger.error("Error processing command", e);
                System.out.println("An error occurred: " + e.getMessage());
            }

            System.out.println();
        }

        System.out.println("Thank you for using Document Editor!");
        logger.log("Application closed by user");
    }

    private void displayWelcome() {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║        DOCUMENT EDITOR - Design Patterns Project          ║");
        System.out.println("║                                                           ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private void displayMenu() {
        System.out.println("┌───────────────────────────────────────────────────────────┐");
        System.out.println("│                      MAIN MENU                            │");
        System.out.println("├───────────────────────────────────────────────────────────┤");
        System.out.println("│  1. Create New Document                                   │");
        System.out.println("│  2. Add Element (Paragraph/Headline/Image)                │");
        System.out.println("│  3. Render Document to Console                            │");
        System.out.println("│  4. Perform Word Count                                    │");
        System.out.println("│  5. Export Document (HTML/PDF/Markdown/Plain Text)        │");
        System.out.println("│  6. Undo Last Action                                      │");
        System.out.println("│  7. Redo Last Action                                      │");
        System.out.println("│  8. Save Document (Local)                                 │");
        System.out.println("│  9. Load Document (Local)                                 │");
        System.out.println("│ 10. Save to Cloud Storage                                 │");
        System.out.println("│ 11. Load from Cloud Storage                               │");
        System.out.println("│ 12. List Cloud Documents                                  │");
        System.out.println("│  0. Exit                                                  │");
        System.out.println("└───────────────────────────────────────────────────────────┘");

        if (currentDocument != null) {
            System.out.printf("Current Document: %s %s\n",
                    currentDocument.getTitle(),
                    currentDocument.isModified() ? "[MODIFIED]" : "");
        }

        System.out.printf("Undo Available: %d | Redo Available: %d\n",
                commandHistory.getUndoCount(),
                commandHistory.getRedoCount());

        System.out.print("\nEnter your choice: ");
    }

    private void createDocument() {
        System.out.print("Enter document title: ");
        String title = scanner.nextLine().trim();

        if (title.isEmpty()) {
            title = "Untitled Document";
        }

        currentDocument = new Document(title);
        currentDocument.attach(observer);

        System.out.println("✓ Document created: " + title);
        logger.log("New document created: " + title);
    }

    private void addElement() {
        if (currentDocument == null) {
            System.out.println("✗ No document is currently open. Please create a document first.");
            return;
        }

        System.out.println("\nSelect element type:");
        System.out.println("1. Paragraph");
        System.out.println("2. Headline");
        System.out.println("3. Image");
        System.out.print("Choice: ");

        String choice = scanner.nextLine().trim();
        DocumentElement element = null;

        switch (choice) {
            case "1" -> element = createParagraph();
            case "2" -> element = createHeadline();
            case "3" -> element = createImage();
            default -> {
                System.out.println("Invalid choice.");
                return;
            }
        }

        if (element != null) {
            Command command = new AddElementCommand(currentDocument, element);
            commandHistory.executeCommand(command);
            System.out.println("✓ Element added successfully.");
        }
    }

    private DocumentElement createParagraph() {
        System.out.print("Enter paragraph text: ");
        String text = scanner.nextLine();

        ParagraphFactory factory = new ParagraphFactory();
        return factory.setText(text).createElement();
    }

    private DocumentElement createHeadline() {
        System.out.print("Enter headline text: ");
        String text = scanner.nextLine();

        System.out.print("Enter level (1-3): ");
        int level = 1;
        try {
            level = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid level, using 1.");
        }

        HeadlineFactory factory = new HeadlineFactory();
        return factory.setText(text).setLevel(level).createElement();
    }

    private DocumentElement createImage() {
        System.out.print("Enter image filename: ");
        String filename = scanner.nextLine();

        System.out.print("Enter width: ");
        int width = 100;
        try {
            width = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid width, using 100.");
        }

        System.out.print("Enter height: ");
        int height = 100;
        try {
            height = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid height, using 100.");
        }

        ImageFactory factory = new ImageFactory();
        return factory.setFilename(filename).setWidth(width).setHeight(height).createElement();
    }

    private void renderDocument() {
        if (currentDocument == null) {
            System.out.println("✗ No document is currently open.");
            return;
        }

        RenderVisitor visitor = new RenderVisitor();
        currentDocument.accept(visitor);
        System.out.println("\n" + visitor.getOutput());
    }

    private void performWordCount() {
        if (currentDocument == null) {
            System.out.println("✗ No document is currently open.");
            return;
        }

        WordCountVisitor visitor = new WordCountVisitor(new BasicWordCountStrategy());
        currentDocument.accept(visitor);

        System.out.println("═══════════════════════════════════════");
        System.out.println("  Word Count: " + visitor.getWordCount());
        System.out.println("═══════════════════════════════════════");

        logger.log("Word count performed: " + visitor.getWordCount() + " words");
    }

    private void exportDocument() {
        if (currentDocument == null) {
            System.out.println("✗ No document is currently open.");
            return;
        }

        System.out.println("\nSelect export format:");
        System.out.println("1. Basic HTML");
        System.out.println("2. HTML with CSS (Fancy)");
        System.out.println("3. Markdown");
        System.out.println("4. Plain Text");
        System.out.println("5. PDF-Style Format [BONUS] (formatted text file)");
        System.out.print("Choice: ");

        String choice = scanner.nextLine().trim();

        System.out.print("Enter output filename: ");
        String filename = scanner.nextLine().trim();

        if (filename.isEmpty()) {
            System.out.println("✗ Invalid filename.");
            return;
        }

        try {
            Exporter exporter = createExporter(choice);
            if (exporter != null) {
                exporter.export(currentDocument, filename);
                System.out.println("✓ Document exported to: " + filename);
            } else {
                System.out.println("✗ Invalid export format.");
            }
        } catch (IOException e) {
            logger.error("Export failed", e);
            System.out.println("✗ Export failed: " + e.getMessage());
        }
    }

    private Exporter createExporter(String choice) {
        return switch (choice) {
            case "1" -> new BasicHTMLExporter();
            case "2" -> new CSSDecorator(new BasicHTMLExporter());
            case "3" -> new MarkdownDecorator(new BasicHTMLExporter());
            case "4" -> new PlainTextDecorator(new BasicHTMLExporter());
            case "5" -> new PDFDecorator(new BasicHTMLExporter());
            default -> null;
        };
    }

    private void undo() {
        if (commandHistory.undo()) {
            System.out.println("✓ Last action undone.");
        } else {
            System.out.println("✗ Nothing to undo.");
        }
    }

    private void redo() {
        if (commandHistory.redo()) {
            System.out.println("✓ Last action redone.");
        } else {
            System.out.println("✗ Nothing to redo.");
        }
    }

    private void saveDocument() {
        if (currentDocument == null) {
            System.out.println("✗ No document is currently open.");
            return;
        }

        System.out.print("Enter filename to save: ");
        String filename = scanner.nextLine().trim();

        if (filename.isEmpty()) {
            System.out.println("✗ Invalid filename.");
            return;
        }

        if (persistence.save(currentDocument, filename)) {
            System.out.println("✓ Document saved successfully to: " + filename);
        } else {
            System.out.println("✗ Failed to save document.");
        }
    }

    private void loadDocument() {
        System.out.print("Enter filename to load: ");
        String filename = scanner.nextLine().trim();

        if (filename.isEmpty()) {
            System.out.println("✗ Invalid filename.");
            return;
        }

        Document loadedDoc = persistence.load(filename);
        if (loadedDoc != null) {
            currentDocument = loadedDoc;
            currentDocument.attach(observer);
            System.out.println("✓ Document loaded successfully: " + currentDocument.getTitle());
        } else {
            System.out.println("✗ Failed to load document.");
        }
    }


    /**
     * Save document to cloud storage using Adapter and Proxy patterns.
     */
    private void saveToCloud() {
        if (currentDocument == null) {
            System.out.println("✗ No document is currently open.");
            return;
        }

        if (!cloudStorage.isAuthenticated()) {
            System.out.println("✗ Cloud storage service not available.");
            return;
        }

        System.out.print("Enter cloud filename: ");
        String filename = scanner.nextLine().trim();

        if (filename.isEmpty()) {
            System.out.println("✗ Invalid filename.");
            return;
        }

        try {
            String fileId = cloudStorage.uploadDocument(currentDocument, filename);
            System.out.println("✓ Document saved to " + cloudStorage.getServiceName() + ": " + fileId);
            logger.log("Document uploaded to cloud: " + filename);
        } catch (IOException e) {
            logger.error("Cloud save failed", e);
            System.out.println("✗ Failed to save to cloud: " + e.getMessage());
        }
    }

    /**
     * Load document from cloud storage using Adapter and Proxy patterns.
     * Proxy provides caching to avoid repeated downloads.
     */
    private void loadFromCloud() {
        if (!cloudStorage.isAuthenticated()) {
            System.out.println("✗ Cloud storage service not available.");
            return;
        }

        System.out.print("Enter cloud file ID or name: ");
        String fileId = scanner.nextLine().trim();

        if (fileId.isEmpty()) {
            System.out.println("✗ Invalid file ID.");
            return;
        }

        try {
            Document loadedDoc = cloudStorage.downloadDocument(fileId);
            if (loadedDoc != null) {
                currentDocument = loadedDoc;
                currentDocument.attach(observer);
                System.out.println(
                        "✓ Document loaded from " + cloudStorage.getServiceName() + ": " + currentDocument.getTitle());
                logger.log("Document downloaded from cloud: " + fileId);
            } else {
                System.out.println("✗ Failed to load document from cloud.");
            }
        } catch (IOException e) {
            logger.error("Cloud load failed", e);
            System.out.println("✗ Failed to load from cloud: " + e.getMessage());
        }
    }

    /**
     * List all documents in cloud storage.
     */
    private void listCloudDocuments() {
        if (!cloudStorage.isAuthenticated()) {
            System.out.println("✗ Cloud storage service not available.");
            return;
        }

        try {
            List<String> documents = cloudStorage.listDocuments();

            System.out.println("\n═══════════════════════════════════════");
            System.out.println("  Documents in " + cloudStorage.getServiceName());
            System.out.println("═══════════════════════════════════════");

            if (documents.isEmpty()) {
                System.out.println("  (No documents found)");
            } else {
                for (int i = 0; i < documents.size(); i++) {
                    System.out.println("  " + (i + 1) + ". " + documents.get(i));
                }
            }

            System.out.println("═══════════════════════════════════════");

            logger.log("Listed " + documents.size() + " documents from cloud");
        } catch (IOException e) {
            logger.error("Cloud list failed", e);
            System.out.println("✗ Failed to list cloud documents: " + e.getMessage());
        }
    }
}
