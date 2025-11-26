# Design Patterns Report
## CPSC7700 Application Architecture - Document Editor Project

---

## Table of Contents
1. [Composite Pattern](#1-composite-pattern)
2. [Factory Method Pattern](#2-factory-method-pattern)
3. [Visitor Pattern](#3-visitor-pattern)
4. [Command Pattern](#4-command-pattern)
5. [Strategy Pattern](#5-strategy-pattern)
6. [Adapter Pattern](#6-adapter-pattern)
7. [Decorator Pattern](#7-decorator-pattern)
8. [Singleton Pattern](#8-singleton-pattern)
9. [Observer Pattern](#9-observer-pattern)

---

## 1. Composite Pattern

### Why It Was Chosen

The Composite pattern was chosen to represent the hierarchical structure of a document. A document naturally consists of multiple elements (paragraphs, headlines, images) that need to be treated uniformly. This pattern solves the problem of:
- **Treating individual elements and collections uniformly**: Both a single paragraph and an entire document can be operated on through the same interface
- **Building tree structures**: Documents can theoretically contain nested sections
- **Simplifying client code**: Operations work on any element without type checking

Without the Composite pattern, we would need conditional logic everywhere to distinguish between individual elements and collections, violating the Open/Closed Principle.

### How It Was Implemented

**Key Classes and Roles:**

- **Component** (`DocumentElement` interface): Defines the common interface for all objects in the composition
  - Methods: `accept()`, `render()`, `add()`, `remove()`, `getChildren()`, `isComposite()`
  
- **Composite** (`Document` class): Represents complex components that have children
  - Stores child elements in `List<DocumentElement>`
  - Implements child management operations
  - Delegates operations to children
  
- **Leaf** (`Paragraph`, `Headline`, `Image` classes): Represents leaf objects with no children
  - Implements basic operations
  - Throws `UnsupportedOperationException` for child management

**Code Example:**
```java
public interface DocumentElement {
    void accept(Visitor visitor);
    String render();
    default void add(DocumentElement element) {
        throw new UnsupportedOperationException();
    }
}

public class Document implements DocumentElement {
    private List<DocumentElement> elements = new ArrayList<>();
    
    public void add(DocumentElement element) {
        elements.add(element);
    }
}
```

### Alternative Considered

**Without Composite Pattern:**

We could have used separate collections and type-specific handling:

```java
public class Document {
    private List<Paragraph> paragraphs;
    private List<Headline> headlines;
    private List<Image> images;
    
    public void render() {
        for (Paragraph p : paragraphs) p.render();
        for (Headline h : headlines) h.render();
        for (Image i : images) i.render();
    }
}
```

**Problems with this approach:**
- ❌ Adding new element types requires modifying the Document class
- ❌ Cannot preserve element ordering
- ❌ Violates Open/Closed Principle
- ❌ Code duplication for each operation
- ❌ Cannot support nested structures

The Composite pattern elegantly solves all these issues by providing a unified interface.

---

## 2. Factory Method Pattern

### Why It Was Chosen

The Factory Method pattern was chosen to encapsulate the creation logic for different document element types. This addresses several problems:
- **Decoupling object creation**: Client code doesn't need to know concrete class names
- **Centralized creation logic**: Configuration and initialization in one place
- **Extensibility**: New element types can be added without changing client code
- **Preventing invalid object states**: Factories ensure proper initialization

Without a factory, client code would use `new Paragraph()`, `new Headline()`, etc., creating tight coupling and making the system harder to extend.

### How It Was Implemented

**Key Classes and Roles:**

- **Creator** (`DocumentElementFactory` abstract class): Declares the factory method
  - Provides static method `getFactory(String type)` for factory selection
  
- **Concrete Creators** (`ParagraphFactory`, `HeadlineFactory`, `ImageFactory`): Implement element creation
  - Each factory creates a specific element type
  - Use fluent API for configuration: `setText()`, `setLevel()`, etc.
  
- **Products** (`Paragraph`, `Headline`, `Image`): The created objects

**Code Example:**
```java
public abstract class DocumentElementFactory {
    public abstract DocumentElement createElement();
    
    public static DocumentElementFactory getFactory(String type) {
        switch (type.toLowerCase()) {
            case "paragraph": return new ParagraphFactory();
            case "headline": return new HeadlineFactory();
            case "image": return new ImageFactory();
        }
    }
}

// Usage in CLI:
DocumentElementFactory factory = DocumentElementFactory.getFactory("paragraph");
ParagraphFactory pFactory = (ParagraphFactory) factory;
DocumentElement element = pFactory.setText("Hello").createElement();
```

### Alternative Considered

**Direct Instantiation:**

```java
// In ConsoleInterface
public void addElement() {
    System.out.println("Select type: 1.Paragraph 2.Headline 3.Image");
    int choice = scanner.nextInt();
    
    DocumentElement element;
    if (choice == 1) {
        String text = scanner.nextLine();
        element = new Paragraph(text);
    } else if (choice == 2) {
        String text = scanner.nextLine();
        int level = scanner.nextInt();
        element = new Headline(text, level);
    } else {
        // ... more cases
    }
    document.add(element);
}
```

**Problems with this approach:**
- ❌ Client code tightly coupled to concrete classes
- ❌ Cannot change creation logic without modifying client
- ❌ Difficult to add new element types
- ❌ Violates Dependency Inversion Principle
- ❌ No centralized place for default values

The Factory Method pattern centralizes creation and keeps client code clean and extensible.

---

## 3. Visitor Pattern

### Why It Was Chosen

The Visitor pattern was chosen to separate operations (word counting, HTML export, rendering) from the element structure. This solves several critical problems:
- **Adding new operations without modifying elements**: New visitors can be created without changing `Paragraph`, `Headline`, or `Image` classes
- **Keeping element classes focused**: Elements only handle their data, not operations
- **Type-safe double dispatch**: Correct operation is called based on element type
- **Grouping related operations**: All HTML export logic is in one visitor class

Without the Visitor pattern, we would need to add methods like `exportToHTML()`, `countWords()`, `exportToMarkdown()` to every element class, violating the Open/Closed Principle.

### How It Was Implemented

**Key Classes and Roles:**

- **Visitor** (`Visitor` interface): Declares visit methods for each element type
  ```java
  void visitDocument(Document document);
  void visitParagraph(Paragraph paragraph);
  void visitHeadline(Headline headline);
  void visitImage(Image image);
  ```
  
- **Concrete Visitors**: 
  - `WordCountVisitor`: Counts words using a pluggable strategy
  - `HTMLExportVisitor`: Generates HTML representation
  - `RenderVisitor`: Creates console-friendly output
  
- **Elements** (all `DocumentElement` implementations): Accept visitors
  ```java
  public void accept(Visitor visitor) {
      visitor.visitParagraph(this);
  }
  ```

**Double Dispatch Mechanism:**
1. Client calls `element.accept(visitor)`
2. Element calls `visitor.visitParagraph(this)` with correct type
3. Visitor performs operation with full type information

**Code Example:**
```java
// Visitor implementation
public class WordCountVisitor implements Visitor {
    private int wordCount = 0;
    
    public void visitParagraph(Paragraph p) {
        wordCount += countWords(p.getText());
    }
    
    public void visitImage(Image img) {
        // Images don't contribute to word count
    }
}

// Usage
WordCountVisitor visitor = new WordCountVisitor(new BasicWordCountStrategy());
document.accept(visitor);
System.out.println("Words: " + visitor.getWordCount());
```

### Alternative Considered

**Adding Methods to Elements:**

```java
public class Paragraph implements DocumentElement {
    private String text;
    
    public String toHTML() {
        return "<p>" + escapeHtml(text) + "</p>";
    }
    
    public String toMarkdown() {
        return text + "\n\n";
    }
    
    public int countWords() {
        return text.split("\\s+").length;
    }
    
    public String toPlainText() {
        return text + "\n";
    }
    
    // More methods for each new operation...
}
```

**Problems with this approach:**
- ❌ Element classes become bloated with unrelated operations
- ❌ Adding new operations requires modifying all element classes
- ❌ Violates Single Responsibility Principle
- ❌ Cannot easily swap operation implementations
- ❌ Difficult to maintain related operation code (scattered across classes)

The Visitor pattern keeps operations organized and element classes clean, making the system much more maintainable.

---

## 4. Command Pattern

### Why It Was Chosen

The Command pattern was essential for implementing undo/redo functionality. It addresses these problems:
- **Encapsulating actions as objects**: Actions can be stored, passed around, and executed later
- **Undo/Redo support**: Each command knows how to reverse itself
- **Action history**: Commands can be logged and replayed
- **Decoupling sender and receiver**: The CLI doesn't need to know how document modifications work

Without the Command pattern, implementing undo/redo would require complex state management and manual tracking of all changes.

### How It Was Implemented

**Key Classes and Roles:**

- **Command** (`Command` interface): Defines the command interface
  ```java
  void execute();
  void undo();
  String getDescription();
  ```
  
- **Concrete Commands**:
  - `AddElementCommand`: Adds an element to the document
    - `execute()`: Calls `document.add(element)`
    - `undo()`: Calls `document.remove(element)`
  - `RemoveElementCommand`: Removes an element
    - `execute()`: Removes and stores position
    - `undo()`: Re-inserts at original position
    
- **Invoker** (`CommandHistory`): Manages command execution and history
  - Maintains `undoStack` and `redoStack`
  - `executeCommand()`: Executes and pushes to undo stack
  - `undo()`: Pops from undo stack, undoes, pushes to redo stack
  - `redo()`: Reverses the undo operation
  
- **Receiver** (`Document`): The object commands operate on

**Code Example:**
```java
public class AddElementCommand implements Command {
    private Document document;
    private DocumentElement element;
    
    public void execute() {
        document.add(element);
    }
    
    public void undo() {
        document.remove(element);
    }
}

// Usage
Command cmd = new AddElementCommand(document, paragraph);
commandHistory.executeCommand(cmd);  // Execute
commandHistory.undo();               // Undo
commandHistory.redo();               // Redo
```

### Alternative Considered

**Direct State Saving:**

```java
public class Document {
    private List<DocumentElement> elements;
    private Stack<List<DocumentElement>> history;
    
    public void add(DocumentElement element) {
        // Save current state before modification
        history.push(new ArrayList<>(elements));
        elements.add(element);
    }
    
    public void undo() {
        if (!history.isEmpty()) {
            elements = history.pop();
        }
    }
}
```

**Problems with this approach:**
- ❌ Memory intensive: Copies entire document state for each change
- ❌ Cannot redo after undo
- ❌ No action descriptions for logging
- ❌ Doesn't scale with document size
- ❌ Couples undo logic with document class
- ❌ Cannot implement selective undo or action replay

The Command pattern uses much less memory by storing only the action, not the entire state, and provides much more flexibility.

---

## 5. Strategy Pattern

### Why It Was Chosen

The Strategy pattern was chosen to make the word counting algorithm pluggable and extensible. This addresses:
- **Algorithm flexibility**: Different counting rules can be swapped at runtime
- **Testing**: Easy to test different strategies independently
- **Extensibility**: New counting strategies can be added without modifying existing code
- **Separation of concerns**: Counting logic is separate from visitor logic

Even though we currently have only one strategy, the pattern makes it trivial to add others (e.g., counting only content words, multilingual counting, etc.).

### How It Was Implemented

**Key Classes and Roles:**

- **Strategy** (`WordCountStrategy` interface): Defines the algorithm interface
  ```java
  int countWords(String text);
  ```
  
- **Concrete Strategy** (`BasicWordCountStrategy`): Implements word counting
  - Splits text on whitespace
  - Handles null and empty strings
  - Can be extended for more sophisticated counting
  
- **Context** (`WordCountVisitor`): Uses the strategy
  - Holds reference to strategy
  - Delegates counting to strategy
  - Accumulates counts across elements

**Code Example:**
```java
public class BasicWordCountStrategy implements WordCountStrategy {
    public int countWords(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        return text.trim().split("\\s+").length;
    }
}

// Usage
WordCountStrategy strategy = new BasicWordCountStrategy();
WordCountVisitor visitor = new WordCountVisitor(strategy);
document.accept(visitor);
```

### Alternative Considered

**Hardcoded in Visitor:**

```java
public class WordCountVisitor implements Visitor {
    private int wordCount = 0;
    
    public void visitParagraph(Paragraph p) {
        String text = p.getText();
        if (text != null && !text.trim().isEmpty()) {
            wordCount += text.trim().split("\\s+").length;
        }
    }
}
```

**Problems with this approach:**
- ❌ Cannot change counting algorithm at runtime
- ❌ Adding new counting rules requires modifying visitor
- ❌ Cannot reuse counting logic elsewhere
- ❌ Difficult to test counting logic in isolation
- ❌ Violates Open/Closed Principle

**Future Extensions Made Easy:**
With the Strategy pattern, we can easily add:
- `SmartWordCountStrategy`: Ignores punctuation, contractions
- `MultilingualWordCountStrategy`: Handles different languages
- `ContentWordStrategy`: Counts only content words (nouns, verbs, adjectives)

---

## 6. Adapter Pattern

### Why It Was Chosen

The Adapter pattern was chosen to integrate a simulated "third-party" JSON serialization library with our document persistence needs. This solves:
- **Interface incompatibility**: The library's interface doesn't match our needs
- **Reusing existing code**: Can use the library without modifying it
- **Abstraction**: Our code depends on our interface, not the library
- **Easy library swapping**: Can replace the library without changing application code

This demonstrates a real-world scenario where you need to integrate external libraries that don't fit your application's architecture.

### How It Was Implemented

**Key Classes and Roles:**

- **Target** (`DocumentPersistence` interface): The interface our application expects
  ```java
  boolean save(Document document, String filename);
  Document load(String filename);
  ```
  
- **Adaptee** (`JSONSerializationLibrary`): The third-party library
  - Provides `stringify(Object)` and `parse(String)` methods
  - Has its own interface that doesn't match our needs
  
- **Adapter** (`DocumentPersistenceAdapter`): Bridges the gap
  - Implements `DocumentPersistence`
  - Uses `JSONSerializationLibrary` internally
  - Translates between interfaces
  - Adds file I/O operations
  - Handles conversion to/from serializable format

**Code Example:**
```java
public class DocumentPersistenceAdapter implements DocumentPersistence {
    private JSONSerializationLibrary jsonLibrary;
    
    public boolean save(Document document, String filename) {
        Object serializable = document.toSerializable();
        String json = jsonLibrary.stringify(serializable);
        // Write to file...
        return true;
    }
    
    public Document load(String filename) {
        String json = // Read from file...
        // Parse and reconstruct document...
        return document;
    }
}
```

### Alternative Considered

**Direct Usage of Library:**

```java
public class ConsoleInterface {
    private JSONSerializationLibrary jsonLib = new JSONSerializationLibrary();
    
    private void saveDocument() {
        Object data = currentDocument.toSerializable();
        String json = jsonLib.stringify(data);
        
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(json);
        }
    }
    
    private void loadDocument() {
        String json = // Read file...
        Map<String, Object> data = jsonLib.parse(json);
        // Manually reconstruct document from map...
    }
}
```

**Problems with this approach:**
- ❌ Client code coupled to specific library
- ❌ Cannot easily switch to different serialization library
- ❌ Serialization logic scattered throughout application
- ❌ Difficult to test without the library
- ❌ Cannot provide additional functionality (logging, error handling)
- ❌ Violates Dependency Inversion Principle

The Adapter pattern provides a clean abstraction layer, making the system more flexible and testable.

---

## 7. Decorator Pattern

### Why It Was Chosen

The Decorator pattern was chosen to add different export format capabilities dynamically without modifying the base exporter. This addresses:
- **Dynamic behavior addition**: Export formats can be combined at runtime
- **Avoiding class explosion**: Don't need separate classes for every combination
- **Open/Closed Principle**: Add new formats without modifying existing code
- **Flexibility**: Decorators can be stacked and combined

Without the Decorator pattern, we would need separate exporter classes for each format, leading to duplication and inflexibility.

### How It Was Implemented

**Key Classes and Roles:**

- **Component** (`Exporter` interface): Common interface for all exporters
  ```java
  void export(Document document, String filename);
  String getContent(Document document);
  ```
  
- **Concrete Component** (`BasicHTMLExporter`): Base functionality
  - Uses `HTMLExportVisitor` to generate basic HTML
  - Provides minimal HTML structure
  
- **Decorator** (`ExporterDecorator` abstract class): Base decorator
  - Implements `Exporter` interface
  - Holds reference to wrapped `Exporter`
  - Delegates to wrapped exporter by default
  
- **Concrete Decorators**:
  - `CSSDecorator`: Injects CSS styles into HTML
  - `MarkdownDecorator`: Converts to Markdown format
  - `PlainTextDecorator`: Converts to plain text

**Code Example:**
```java
public class CSSDecorator extends ExporterDecorator {
    public String getContent(Document document) {
        String basicHTML = wrappedExporter.getContent(document);
        String css = "<style>/* CSS rules */</style>";
        return basicHTML.replace("</head>", css + "</head>");
    }
}

// Usage - decorators can be stacked
Exporter exporter = new CSSDecorator(new BasicHTMLExporter());
exporter.export(document, "output.html");
```

### Alternative Considered

**Subclassing for Each Format:**

```java
public class BasicHTMLExporter {
    public void export(Document doc, String file) { }
}

public class HTMLWithCSSExporter extends BasicHTMLExporter {
    @Override
    public void export(Document doc, String file) {
        // Generate HTML with CSS
    }
}

public class MarkdownExporter {
    public void export(Document doc, String file) {
        // Generate Markdown
    }
}

public class PlainTextExporter {
    public void export(Document doc, String file) {
        // Generate plain text
    }
}

// For combinations:
public class HTMLWithCSSAndMinificationExporter extends HTMLWithCSSExporter {
    // Even more code duplication
}
```

**Problems with this approach:**
- ❌ Class explosion: N formats × M enhancements = N×M classes
- ❌ Code duplication across similar exporters
- ❌ Cannot combine behaviors at runtime
- ❌ Rigid hierarchy limits flexibility
- ❌ Adding new enhancement requires modifying all exporters
- ❌ Violates Open/Closed Principle

**Benefits of Decorator Pattern:**
- ✅ Add new formats by creating one decorator class
- ✅ Combine decorators: `new CompressDecorator(new CSSDecorator(new BasicHTMLExporter()))`
- ✅ Reuse existing functionality
- ✅ Client chooses combinations at runtime

---

## 8. Singleton Pattern

### Why It Was Chosen

The Singleton pattern was chosen for the `Logger` class to provide a global point of access to logging functionality. This addresses:
- **Global access**: Any part of the application can log messages
- **Resource management**: Single file handle instead of multiple
- **Consistency**: All logs go to the same file with consistent formatting
- **Thread safety**: Ensures safe concurrent access

Logging is a classic use case for Singleton because having multiple logger instances writing to the same file would cause problems.

### How It Was Implemented

**Key Classes and Roles:**

- **Singleton** (`Logger` class): Single instance with global access
  - Private constructor prevents direct instantiation
  - Static `getInstance()` method provides access
  - Thread-safe using double-checked locking
  - Manages single file writer instance

**Thread-Safe Implementation:**
```java
public class Logger {
    private static volatile Logger instance;
    private static final Object lock = new Object();
    private PrintWriter fileWriter;
    
    private Logger() {
        // Private constructor
        fileWriter = new PrintWriter(new FileWriter("app.log", true));
    }
    
    public static Logger getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new Logger();
                }
            }
        }
        return instance;
    }
    
    public synchronized void log(String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        String logEntry = String.format("[%s] %s", timestamp, message);
        System.out.println(logEntry);
        fileWriter.println(logEntry);
    }
}
```

**Usage:**
```java
Logger logger = Logger.getInstance();
logger.log("Document created");
logger.error("Failed to save", exception);
```

### Alternative Considered

**Static Methods:**

```java
public class Logger {
    private static PrintWriter fileWriter;
    
    static {
        try {
            fileWriter = new PrintWriter(new FileWriter("app.log", true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void log(String message) {
        // Logging code
    }
}

// Usage
Logger.log("Message");
```

**Problems with this approach:**
- ❌ Cannot control instantiation timing
- ❌ Difficult to test (cannot mock static methods easily)
- ❌ Cannot use inheritance or interfaces
- ❌ Less flexible for future changes
- ❌ Static initialization can cause issues

**Dependency Injection Alternative:**

Some argue against Singleton in favor of dependency injection:

```java
public class ConsoleInterface {
    private Logger logger;
    
    public ConsoleInterface(Logger logger) {
        this.logger = logger;
    }
}
```

**Trade-offs:**
- ✅ More testable (can inject mock)
- ✅ More flexible
- ❌ More boilerplate (pass logger everywhere)
- ❌ Still need single instance somewhere

For a simple application like this, Singleton is appropriate. For larger applications, dependency injection might be better.

---

## 9. Observer Pattern

### Why It Was Chosen

The Observer pattern was chosen to implement real-time document change notifications. This addresses:
- **Loose coupling**: Document doesn't need to know about UI details
- **Multiple observers**: Can have multiple UI components observing
- **Real-time updates**: UI automatically updates when document changes
- **Extensibility**: Easy to add new observers (e.g., auto-save, status bar)

This pattern enables the "document modified" flag and other reactive behaviors without tight coupling between model and view.

### How It Was Implemented

**Key Classes and Roles:**

- **Subject** (`Document` class): Maintains list of observers
  ```java
  private List<DocumentObserver> observers;
  
  public void attach(DocumentObserver observer) {
      observers.add(observer);
  }
  
  private void notifyObservers() {
      for (DocumentObserver observer : observers) {
          observer.update(this);
      }
  }
  ```
  
- **Observer** (`DocumentObserver` interface): Defines update interface
  ```java
  void update(Document document);
  ```
  
- **Concrete Observer** (`ConsoleObserver`): Responds to changes
  - Logs document modifications
  - Displays status messages
  - Could trigger auto-save, update status bar, etc.

**Code Example:**
```java
public class ConsoleObserver implements DocumentObserver {
    private Logger logger;
    
    public void update(Document document) {
        if (document.isModified()) {
            logger.log("Document modified: " + document.getTitle());
            System.out.println("[DOCUMENT MODIFIED] " + 
                document.getTitle() + 
                " (Elements: " + document.getElementCount() + ")");
        }
    }
}

// Usage
Document doc = new Document("My Doc");
ConsoleObserver observer = new ConsoleObserver();
doc.attach(observer);
doc.add(paragraph);  // Observer is automatically notified
```

### Alternative Considered

**Direct Method Calls:**

```java
public class Document {
    private ConsoleInterface ui;
    
    public void setUI(ConsoleInterface ui) {
        this.ui = ui;
    }
    
    public void add(DocumentElement element) {
        elements.add(element);
        if (ui != null) {
            ui.onDocumentModified(this);
        }
    }
}

public class ConsoleInterface {
    public void onDocumentModified(Document doc) {
        System.out.println("Document modified: " + doc.getTitle());
    }
}
```

**Problems with this approach:**
- ❌ Tight coupling between Document and ConsoleInterface
- ❌ Document depends on UI class (wrong direction)
- ❌ Can only notify one observer
- ❌ Cannot add new observer types without modifying Document
- ❌ Violates Dependency Inversion Principle
- ❌ Makes Document harder to test

**Benefits of Observer Pattern:**
- ✅ Document knows nothing about ConsoleObserver
- ✅ Can have multiple observers (console, auto-save, status bar)
- ✅ Easy to add new observers without changing Document
- ✅ Document is easier to test in isolation
- ✅ Proper dependency direction (model doesn't depend on view)

---

## Summary

All nine patterns work together to create a flexible, maintainable, and extensible document editor:

1. **Composite**: Document structure
2. **Factory Method**: Element creation
3. **Visitor**: Operations on structure
4. **Command**: Undo/redo
5. **Strategy**: Pluggable algorithms
6. **Adapter**: Third-party integration
7. **Decorator**: Dynamic behavior addition
8. **Singleton**: Global resource access
9. **Observer**: Change notification

Each pattern solves a specific problem and integrates seamlessly with the others through well-defined interfaces. The resulting architecture is:
- ✅ **Extensible**: New features can be added without modifying existing code
- ✅ **Maintainable**: Each class has a single, clear responsibility
- ✅ **Testable**: Components can be tested in isolation
- ✅ **Flexible**: Behaviors can be combined and configured at runtime

This project demonstrates that design patterns are not just theoretical concepts, but practical tools that solve real software design problems.
