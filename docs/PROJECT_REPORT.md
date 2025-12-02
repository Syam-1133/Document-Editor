# Document Editor - Design Patterns Implementation Report

## Project Overview
This document editor application demonstrates the practical implementation of multiple design patterns working together in a cohesive system. The application allows users to create, edit, and export documents containing different types of elements (paragraphs, headlines, and images).

---

## 1. Singleton Pattern

### Why It Was Chosen
The Singleton pattern was chosen for the `Logger` class to ensure that there is only one logging instance throughout the entire application. This solves the problem of multiple logger instances potentially writing to the same log file concurrently, which could cause data corruption, file locking issues, and inconsistent log entries. Having a single logger instance also ensures consistent formatting and centralized control over logging behavior.

### How It Was Implemented
The `Logger` class implements the Singleton pattern using thread-safe double-checked locking. The class has a private constructor to prevent direct instantiation, a static volatile `instance` variable, and a public `getInstance()` method that provides the global point of access. The implementation uses synchronization with a lock object to ensure thread safety when creating the singleton instance. The logger writes to both console and a file (`document_editor.log`) with timestamped entries.

**Key classes and roles:**
- **Singleton:** `Logger` class in `util/Logger.java`
- **Global access point:** `Logger.getInstance()` method
- **Private constructor:** Prevents external instantiation

### Alternative Considered
Without the Singleton pattern, we would have to pass a logger instance to every class that needs logging functionality, or create multiple logger instances throughout the application. This would lead to several problems: different parts of the application might log with different formats, multiple file writers could compete for access to the log file causing I/O conflicts, and the code would be cluttered with logger initialization boilerplate. Using dependency injection without Singleton would require passing the logger through many constructors, making the code more complex and tightly coupled to logging infrastructure.

---

## 2. Factory Method Pattern

### Why It Was Chosen
The Factory Method pattern was chosen to encapsulate the creation logic for different types of document elements (paragraphs, headlines, images). This solves the problem of having client code directly instantiate concrete element classes, which would violate the Dependency Inversion Principle and make it difficult to add new element types. The factory pattern centralizes object creation, making the code more maintainable and extensible when new document element types need to be added.

### How It Was Implemented
The pattern is implemented with an abstract `DocumentElementFactory` base class that defines the factory method `createElement()`. Concrete factory classes (`ParagraphFactory`, `HeadlineFactory`, `ImageFactory`) extend this base and implement the creation logic for specific element types. A static `getFactory(String type)` method serves as a simple factory to return the appropriate factory instance based on the requested element type. This two-level approach combines the Factory Method pattern with a Simple Factory for convenience.

**Key classes and roles:**
- **Creator (abstract):** `DocumentElementFactory` class
- **ConcreteCreators:** `ParagraphFactory`, `HeadlineFactory`, `ImageFactory`
- **Product interface:** `DocumentElement` interface
- **ConcreteProducts:** `Paragraph`, `Headline`, `Image` classes

### Alternative Considered
Without the Factory Method pattern, client code would have to use direct instantiation with `new Paragraph()`, `new Headline()`, or `new Image()` throughout the application. This would scatter element creation logic across multiple classes, making it harder to change initialization logic or add validation. Adding a new element type would require finding and modifying all places where elements are created. The code would also be more tightly coupled to concrete classes, making testing and mocking more difficult. The factory pattern provides a clear extension point for new element types through the creation of new factory subclasses.

---

## 3. Command Pattern

### Why It Was Chosen
The Command pattern was chosen to implement undo/redo functionality for document editing operations. This solves the problem of having to implement separate undo logic in each operation class and provides a clean way to maintain a history of actions. The pattern encapsulates actions as objects, allowing them to be stored, queued, logged, and most importantly, reversed. This is essential for a document editor where users expect to undo and redo their modifications.

### How It Was Implemented
The pattern uses a `Command` interface with `execute()`, `undo()`, and `getDescription()` methods. Concrete command classes like `AddElementCommand` and `RemoveElementCommand` implement this interface, storing references to the document and the element to be added or removed. The `CommandHistory` class acts as the invoker, maintaining stacks of executed and undone commands. When a command is executed, it performs its action and is pushed onto the history stack. Undo operations pop commands and call their `undo()` method, while redo operations reverse this process.

**Key classes and roles:**
- **Command interface:** `Command` interface with execute(), undo(), and getDescription()
- **ConcreteCommands:** `AddElementCommand`, `RemoveElementCommand`
- **Receiver:** `Document` class (receives the actual operations)
- **Invoker:** `CommandHistory` class (manages command execution and history)

### Alternative Considered
Without the Command pattern, we would have to implement undo/redo logic directly within each document operation method, likely using a complex state-tracking mechanism. This would require storing the entire document state after each operation or implementing custom undo logic for each action type. The code would become tightly coupled and difficult to extend with new operations. Adding a new operation type would require not only implementing the forward action but also carefully crafting the corresponding undo logic. The Command pattern elegantly separates the request for an action from its execution, making the system more modular and easier to test.

---

## 4. Composite Pattern

### Why It Was Chosen
The Composite pattern was chosen to represent the document structure where a `Document` can contain multiple `DocumentElement` objects. This solves the problem of treating individual elements and collections of elements uniformly. The pattern allows clients to work with single elements and compositions of elements through the same interface, simplifying client code and making it easy to build complex nested document structures if needed in the future.

### How It Was Implemented
The `DocumentElement` interface serves as the component, defining operations that both leaf and composite objects must implement. The `Document` class acts as the composite, containing a list of child elements and implementing methods like `add()`, `remove()`, and `getChildren()`. Leaf classes (`Paragraph`, `Headline`, `Image`) represent individual elements that cannot have children. All elements implement the `accept(Visitor)` method and `render()` method, allowing uniform treatment. The composite's `isComposite()` method helps distinguish between leaves and composites.

**Key classes and roles:**
- **Component:** `DocumentElement` interface
- **Composite:** `Document` class (can contain children)
- **Leaf:** `Paragraph`, `Headline`, `Image` classes (cannot contain children)
- **Common operations:** render(), accept(Visitor), toSerializable()

### Alternative Considered
Without the Composite pattern, we would need separate interfaces or classes for individual elements and document containers, requiring client code to treat them differently. This would lead to extensive `instanceof` checks and type casting throughout the codebase. Operations that work on the document structure (like rendering or exporting) would need separate code paths for handling individual elements versus collections. The pattern enables uniform treatment through the component interface, making operations like rendering, exporting, and traversing the document structure much simpler and more elegant.

---

## 5. Visitor Pattern

### Why It Was Chosen
The Visitor pattern was chosen to separate document traversal and export operations from the document element classes themselves. This solves the problem of adding new operations (like different export formats or analysis tools) without modifying the existing element classes, adhering to the Open/Closed Principle. The pattern allows us to add new visitors for HTML export, PDF export, word counting, and rendering without touching the core document model.

### How It Was Implemented
The `Visitor` interface defines visit methods for each element type: `visitDocument()`, `visitParagraph()`, `visitHeadline()`, and `visitImage()`. Each document element class implements an `accept(Visitor)` method that calls the appropriate visit method on the visitor. Concrete visitors like `HTMLExportVisitor`, `SimplePDFExportVisitor`, `WordCountVisitor`, and `RenderVisitor` implement the logic for their specific operations. The double-dispatch mechanism ensures that the correct visit method is called based on both the visitor type and the element type.

**Key classes and roles:**
- **Visitor interface:** `Visitor` interface
- **ConcreteVisitors:** `HTMLExportVisitor`, `SimplePDFExportVisitor`, `WordCountVisitor`, `RenderVisitor`
- **Element interface:** `DocumentElement` with accept(Visitor) method
- **ConcreteElements:** `Document`, `Paragraph`, `Headline`, `Image` (each implements accept())

### Alternative Considered
Without the Visitor pattern, we would have to add an `exportToHTML()`, `exportToPDF()`, `countWords()`, and `render()` method to every element class. This would violate the Single Responsibility Principle (elements would need to know about multiple export formats) and the Open/Closed Principle (adding a new export format would require modifying all element classes). The element classes would become bloated with operation-specific code, making them harder to maintain. The Visitor pattern cleanly separates operations from the object structure, allowing new operations to be added simply by creating new visitor classes.

---

## 6. Decorator Pattern

### Why It Was Chosen
The Decorator pattern was chosen to dynamically add features to document exporters without creating a complex inheritance hierarchy. This solves the problem of supporting multiple export enhancements (CSS styling, Markdown conversion, PDF metadata) that can be combined in various ways. Instead of creating separate classes for every combination (HTMLExporter, HTMLWithCSSExporter, HTMLWithCSSAndMarkdownExporter, etc.), decorators allow flexible runtime composition of features.

### How It Was Implemented
The `Exporter` interface serves as the component, defining `export()` and `getContent()` methods. The `BasicHTMLExporter` is the concrete component providing base HTML export functionality. The abstract `ExporterDecorator` class implements the Exporter interface and contains a reference to a wrapped exporter. Concrete decorators (`CSSDecorator`, `MarkdownDecorator`, `PDFDecorator`, `PlainTextDecorator`) extend the base decorator, adding their specific enhancements before or after delegating to the wrapped exporter.

**Key classes and roles:**
- **Component interface:** `Exporter` interface
- **ConcreteComponent:** `BasicHTMLExporter` (provides core functionality)
- **Decorator base:** `ExporterDecorator` abstract class
- **ConcreteDecorators:** `CSSDecorator`, `MarkdownDecorator`, `PDFDecorator`, `PlainTextDecorator`

### Alternative Considered
Without the Decorator pattern, we would need to create a class for every possible combination of export features: `HTMLExporter`, `HTMLWithCSSExporter`, `HTMLWithCSSAndMarkdown`, `HTMLWithCSSAndPDF`, etc. This would lead to a class explosion problem as the number of features grows exponentially. Inheritance-based solutions would be inflexible, as decorations would be fixed at compile time. The Decorator pattern allows clients to build custom export pipelines at runtime by wrapping exporters: `new CSSDecorator(new MarkdownDecorator(new BasicHTMLExporter()))`, providing maximum flexibility with minimal code.

---

## 7. Observer Pattern

### Why It Was Chosen
The Observer pattern was chosen to implement a notification system that alerts interested parties when the document is modified. This solves the problem of keeping the UI, logging system, and other components synchronized with document changes without creating tight coupling between the document and these observers. The pattern enables a publish-subscribe relationship where the document (subject) doesn't need to know the concrete types of its observers.

### How It Was Implemented
The `DocumentObserver` interface defines the observer contract with an `update(Document)` method. The `Document` class acts as the subject, maintaining a list of observers and providing `attach()` and `detach()` methods for observer management. When the document is modified (elements added/removed, title changed), it calls `notifyObservers()`, which iterates through all registered observers and calls their `update()` method. The `ConsoleObserver` is a concrete observer that logs document changes and updates the console UI.

**Key classes and roles:**
- **Observer interface:** `DocumentObserver` interface
- **ConcreteObserver:** `ConsoleObserver` class
- **Subject:** `Document` class (maintains observer list and notifies them)
- **Notification methods:** attach(), detach(), notifyObservers()

### Alternative Considered
Without the Observer pattern, the `Document` class would need to directly call methods on specific UI components, loggers, or other interested parties whenever it changes. This would create tight coupling between the document model and various subsystems, making the code harder to maintain and test. Adding a new component that needs to be notified of changes would require modifying the Document class. The Observer pattern decouples the subject from its observers, allowing new observers to be added without changing the document code, and enabling the document to be tested in isolation.

---

## 8. Adapter Pattern

### Why It Was Chosen
The Adapter pattern was chosen to integrate a third-party JSON serialization library (`JSONSerializationLibrary`) with our document persistence system. This solves the problem of incompatible interfaces where our application expects a specific `DocumentPersistence` interface for saving and loading documents, but the third-party library provides different method signatures. The adapter makes the external library work seamlessly with our code without modifying either the library or our existing persistence interface.

### How It Was Implemented
The `DocumentPersistence` interface serves as the target interface, defining `save(Document, filename)` and `load(filename)` methods that our application expects. The `JSONSerializationLibrary` is the adaptee with incompatible methods like `stringify()` and `parse()`. The `DocumentPersistenceAdapter` class implements the target interface and wraps the JSON library, translating calls between the two interfaces. The adapter converts documents to serializable format, uses the library's stringify method, writes to file, and performs the reverse process for loading.

**Key classes and roles:**
- **Target interface:** `DocumentPersistence` interface
- **Adapter:** `DocumentPersistenceAdapter` class
- **Adaptee:** `JSONSerializationLibrary` (third-party library with incompatible interface)
- **Translation methods:** save(), load(), toSerializable()

### Alternative Considered
Without the Adapter pattern, we would have three bad options: (1) modify the third-party library to implement our interface (not recommended as it breaks with library updates), (2) change our entire application to use the library's interface directly (causes widespread changes and tight coupling), or (3) scatter adapter code throughout the application wherever we need persistence. The Adapter pattern centralizes the interface translation in one place, isolating the rest of the application from the details of the third-party library. If we later want to switch to a different JSON library or storage mechanism, we only need to create a new adapter.

---

## 9. Proxy Pattern

### Why It Was Chosen
The Proxy pattern was chosen to add caching and lazy initialization to cloud storage operations without modifying the actual cloud service implementation. This solves the problem of expensive and slow cloud API calls by caching frequently accessed documents locally. The proxy also implements lazy initialization, delaying the connection to the cloud service until the first operation is performed. This improves application startup time and avoids unnecessary network operations.

### How It Was Implemented
The `CloudStorageService` interface defines the common interface for both the proxy and the real service. The `CloudStorageProxy` class implements this interface and contains a reference to the real cloud storage service (the subject). The proxy intercepts method calls, checking its cache before delegating to the real service. For downloads, if a document is in the cache, it's returned immediately without a network call. For uploads and deletes, the cache is updated accordingly. The proxy also logs all operations and provides cache management methods like `clearCache()` and `getCacheSize()`.

**Key classes and roles:**
- **Subject interface:** `CloudStorageService` interface
- **RealSubject:** `MockCloudStorageAdapter` (represents actual cloud service)
- **Proxy:** `CloudStorageProxy` class (provides caching and lazy initialization)
- **Additional functionality:** Cache management, operation logging, lazy initialization

### Alternative Considered
Without the Proxy pattern, we would have to either (1) implement caching directly in the cloud service class, violating the Single Responsibility Principle and making the code harder to maintain, or (2) add caching logic in every client that uses the cloud service, leading to code duplication and inconsistent caching behavior. The proxy also enables us to add other cross-cutting concerns like logging, authentication, or rate limiting without touching the core service implementation. Testing becomes easier as the proxy can be easily replaced with a mock implementation.

---

## 10. Strategy Pattern

### Why It Was Chosen
The Strategy pattern was chosen to encapsulate different word counting algorithms, allowing the counting behavior to be selected at runtime. This solves the problem of having multiple word counting approaches (basic whitespace splitting, advanced linguistic analysis, language-specific rules) while keeping the code flexible and maintainable. The pattern allows new counting strategies to be added without modifying existing code or the classes that use word counting.

### How It Was Implemented
The `WordCountStrategy` interface defines the strategy contract with a `countWords(String text)` method. The `BasicWordCountStrategy` class implements this interface, providing a simple whitespace-based counting algorithm. The strategy is used by the `WordCountVisitor`, which accepts a strategy in its constructor and uses it to count words in each document element. Clients can easily switch strategies by passing a different implementation, enabling flexible behavior modification at runtime.

**Key classes and roles:**
- **Strategy interface:** `WordCountStrategy` interface
- **ConcreteStrategy:** `BasicWordCountStrategy` class
- **Context:** `WordCountVisitor` (uses the strategy to perform word counting)
- **Algorithm encapsulation:** Each strategy encapsulates a specific counting algorithm

### Alternative Considered
Without the Strategy pattern, word counting logic would be hardcoded directly in the `WordCountVisitor` or element classes, possibly with conditional statements (`if-else` or `switch`) to select between different algorithms. This would violate the Open/Closed Principle—adding a new counting algorithm would require modifying existing code. The conditional logic would also make the code harder to test and maintain. The Strategy pattern cleanly separates the algorithm from the code that uses it, allowing new strategies to be added as new classes without touching existing code, and making it easy to test each algorithm in isolation.

---

## Summary

This Document Editor application demonstrates how multiple design patterns can work together to create a flexible, maintainable, and extensible system:

- **Creational Patterns** (Singleton, Factory Method) handle object creation and ensure single instances where needed
- **Structural Patterns** (Composite, Adapter, Decorator, Proxy) organize classes and objects into larger structures
- **Behavioral Patterns** (Command, Visitor, Observer, Strategy) define communication between objects and assign responsibilities

Each pattern solves a specific problem while adhering to SOLID principles. The patterns complement each other: the Visitor pattern works with the Composite structure, Commands operate on the Observable document, Factories create elements that accept Visitors, and Decorators enhance Exporters that use Visitors. This demonstrates that design patterns are not just theoretical concepts but practical tools that solve real software engineering challenges.

The result is a codebase that is:
- **Extensible:** New features can be added with minimal changes to existing code
- **Maintainable:** Each class has a single, well-defined responsibility
- **Testable:** Patterns like Strategy and Command make unit testing easier
- **Flexible:** Behavior can be modified at runtime through composition rather than inheritance
