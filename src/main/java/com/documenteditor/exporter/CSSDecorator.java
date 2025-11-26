package com.documenteditor.exporter;

import com.documenteditor.model.Document;

/**
 * DECORATOR PATTERN: Concrete Decorator
 * Adds CSS styling to HTML export.
 */
public class CSSDecorator extends ExporterDecorator {

    public CSSDecorator(Exporter exporter) {
        super(exporter);
    }

    @Override
    public String getContent(Document document) {
        String basicHTML = wrappedExporter.getContent(document);

        // Inject CSS into the HTML
        String css = getCSS();
        String htmlWithCSS = basicHTML.replace("</head>", css + "</head>");

        return htmlWithCSS;
    }

    @Override
    public void export(Document document, String filename) throws java.io.IOException {
        // Get decorated content with CSS and write it
        String content = getContent(document);

        try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(filename))) {
            writer.write(content);
        }

        com.documenteditor.util.Logger.getInstance().log("Document exported to HTML with CSS: " + filename);
    }

    private String getCSS() {
        return """
                <style>
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        max-width: 800px;
                        margin: 0 auto;
                        padding: 20px;
                        line-height: 1.6;
                        background-color: #f5f5f5;
                    }
                    h1, h2, h3 {
                        color: #333;
                        border-bottom: 2px solid #007acc;
                        padding-bottom: 10px;
                    }
                    h1 { font-size: 2.5em; }
                    h2 { font-size: 2em; }
                    h3 { font-size: 1.5em; }
                    p {
                        color: #555;
                        margin: 15px 0;
                        text-align: justify;
                    }
                    img {
                        max-width: 100%;
                        height: auto;
                        display: block;
                        margin: 20px auto;
                        border: 1px solid #ddd;
                        border-radius: 4px;
                        padding: 5px;
                    }
                </style>
                """;
    }
}
