package com.documenteditor.adapter;

import java.util.HashMap;
import java.util.Map;

/**
 * Simulated third-party JSON serialization library.
 * This represents an external library with its own interface.
 */
public class JSONSerializationLibrary {
    
    /**
     * Converts an object to JSON string representation.
     * Simulates a third-party library method.
     * @param obj The object to serialize
     * @return JSON string representation
     */
    public String stringify(Object obj) {
        if (obj == null) {
            return "null";
        }
        
        if (obj instanceof String) {
            return "\"" + escapeJson((String) obj) + "\"";
        }
        
        if (obj instanceof Number || obj instanceof Boolean) {
            return obj.toString();
        }
        
        if (obj instanceof Map) {
            return stringifyMap((Map<?, ?>) obj);
        }
        
        if (obj instanceof Iterable) {
            return stringifyIterable((Iterable<?>) obj);
        }
        
        return "\"" + obj.toString() + "\"";
    }
    
    private String stringifyMap(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) {
                sb.append(",");
            }
            first = false;
            
            sb.append("\"").append(entry.getKey()).append("\":");
            sb.append(stringify(entry.getValue()));
        }
        
        sb.append("}");
        return sb.toString();
    }
    
    private String stringifyIterable(Iterable<?> iterable) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        
        for (Object item : iterable) {
            if (!first) {
                sb.append(",");
            }
            first = false;
            sb.append(stringify(item));
        }
        
        sb.append("]");
        return sb.toString();
    }
    
    /**
     * Parse JSON string to a basic object structure (Map/List).
     * Simulates a third-party library method.
     * @param json The JSON string to parse
     * @return The parsed object
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> parse(String json) {
        // Simplified parser - in real scenario, would use a proper JSON library
        json = json.trim();
        if (!json.startsWith("{") || !json.endsWith("}")) {
            throw new IllegalArgumentException("Invalid JSON object");
        }
        
        Map<String, Object> result = new HashMap<>();
        
        // Very basic parsing (this is a simulation)
        // In a real implementation, you'd use a proper JSON parser
        String content = json.substring(1, json.length() - 1);
        
        return result;
    }
    
    private String escapeJson(String str) {
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
