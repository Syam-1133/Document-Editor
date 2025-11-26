package com.documenteditor.cloudstorage;

import com.documenteditor.model.Document;
import com.documenteditor.util.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Proxy Pattern: Adds caching and lazy loading to cloud storage operations.
 * Reduces API calls by caching downloaded documents.
 * Provides additional control over access to the real cloud storage service.
 */
public class CloudStorageProxy implements CloudStorageService {
    private CloudStorageService realService;
    private Map<String, Document> cache;
    private Logger logger;
    private boolean initialized;
    
    public CloudStorageProxy(CloudStorageService realService) {
        this.realService = realService;
        this.cache = new HashMap<>();
        this.logger = Logger.getInstance();
        this.initialized = false;
    }
    
    /**
     * Lazy initialization - only connect when first operation is performed.
     */
    private void ensureInitialized() {
        if (!initialized) {
            logger.log("Initializing cloud storage connection: " + realService.getServiceName());
            initialized = true;
        }
    }
    
    @Override
    public String uploadDocument(Document document, String filename) throws IOException {
        ensureInitialized();
        
        logger.log("Proxy: Uploading document (cache will be invalidated)");
        String fileId = realService.uploadDocument(document, filename);
        
        // Cache the uploaded document
        cache.put(fileId, document);
        logger.log("Proxy: Document cached after upload");
        
        return fileId;
    }
    
    @Override
    public Document downloadDocument(String fileId) throws IOException {
        ensureInitialized();
        
        // Check cache first
        if (cache.containsKey(fileId)) {
            logger.log("Proxy: Document found in cache (avoiding cloud API call)");
            return cache.get(fileId);
        }
        
        // Cache miss - download from cloud
        logger.log("Proxy: Cache miss, downloading from cloud");
        Document document = realService.downloadDocument(fileId);
        
        // Add to cache
        cache.put(fileId, document);
        logger.log("Proxy: Document cached after download");
        
        return document;
    }
    
    @Override
    public List<String> listDocuments() throws IOException {
        ensureInitialized();
        
        logger.log("Proxy: Listing documents (delegating to real service)");
        return realService.listDocuments();
    }
    
    @Override
    public boolean deleteDocument(String fileId) throws IOException {
        ensureInitialized();
        
        logger.log("Proxy: Deleting document and removing from cache");
        boolean result = realService.deleteDocument(fileId);
        
        // Remove from cache
        cache.remove(fileId);
        logger.log("Proxy: Document removed from cache");
        
        return result;
    }
    
    @Override
    public String getServiceName() {
        return realService.getServiceName() + " (Cached)";
    }
    
    @Override
    public boolean isAuthenticated() {
        return realService.isAuthenticated();
    }
    
    /**
     * Clear the entire cache.
     */
    public void clearCache() {
        logger.log("Proxy: Clearing cache (" + cache.size() + " documents)");
        cache.clear();
    }
    
    /**
     * Get cache statistics.
     */
    public int getCacheSize() {
        return cache.size();
    }
}
