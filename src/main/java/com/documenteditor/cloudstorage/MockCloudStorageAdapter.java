package com.documenteditor.cloudstorage;

import com.documenteditor.adapter.DocumentPersistence;
import com.documenteditor.adapter.DocumentPersistenceAdapter;
import com.documenteditor.model.Document;
import com.documenteditor.util.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mock implementation of cloud storage for demonstration purposes.
 * Simulates cloud storage operations using local file system.
 * In production, this would be replaced with actual cloud APIs.
 * Part of Adapter Pattern - adapts file system to cloud storage interface.
 */
public class MockCloudStorageAdapter implements CloudStorageService {
    private static final String CLOUD_STORAGE_DIR = "cloud_storage";
    private final DocumentPersistence persistence;
    private final Logger logger;
    private final String serviceName;
    private boolean authenticated;
    
    public MockCloudStorageAdapter(String serviceName) {
        this.serviceName = serviceName;
        this.persistence = new DocumentPersistenceAdapter();
        this.logger = Logger.getInstance();
        this.authenticated = true; // Mock is always authenticated
        
        try {
            // Create cloud storage directory if it doesn't exist
            Path cloudDir = Paths.get(CLOUD_STORAGE_DIR);
            if (!Files.exists(cloudDir)) {
                Files.createDirectory(cloudDir);
                logger.log("Created cloud storage directory: " + CLOUD_STORAGE_DIR);
            }
        } catch (IOException e) {
            logger.error("Failed to create cloud storage directory", e);
        }
    }
    
    @Override
    public String uploadDocument(Document document, String filename) throws IOException {
        if (!authenticated) {
            throw new IOException(serviceName + " service not authenticated");
        }
        
        logger.log("Uploading document to " + serviceName + ": " + filename);
        
        try {
            String cloudPath = CLOUD_STORAGE_DIR + "/" + filename + ".json";
            persistence.save(document, cloudPath);
            
            logger.log("Document uploaded successfully to " + serviceName + ": " + cloudPath);
            return cloudPath;
            
        } catch (Exception e) {
            logger.error("Failed to upload document to " + serviceName, e);
            throw new IOException("Upload failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Document downloadDocument(String fileId) throws IOException {
        if (!authenticated) {
            throw new IOException(serviceName + " service not authenticated");
        }
        
        logger.log("Downloading document from " + serviceName + ": " + fileId);
        
        try {
            // If fileId doesn't include path, prepend cloud storage directory
            String cloudPath = fileId.contains(CLOUD_STORAGE_DIR) ? 
                fileId : CLOUD_STORAGE_DIR + "/" + fileId;
            
            if (!cloudPath.endsWith(".json")) {
                cloudPath += ".json";
            }
            
            Document document = persistence.load(cloudPath);
            logger.log("Document downloaded successfully from " + serviceName);
            return document;
            
        } catch (Exception e) {
            logger.error("Failed to download document from " + serviceName, e);
            throw new IOException("Download failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<String> listDocuments() throws IOException {
        if (!authenticated) {
            throw new IOException(serviceName + " service not authenticated");
        }
        
        logger.log("Listing documents from " + serviceName);
        
        try {
            Path cloudDir = Paths.get(CLOUD_STORAGE_DIR);
            
            if (!Files.exists(cloudDir)) {
                return new ArrayList<>();
            }
            
            List<String> documentList = Files.list(cloudDir)
                .filter(path -> path.toString().endsWith(".json"))
                .map(path -> path.getFileName().toString().replace(".json", ""))
                .collect(Collectors.toList());
            
            logger.log("Found " + documentList.size() + " documents in " + serviceName);
            return documentList;
            
        } catch (Exception e) {
            logger.error("Failed to list documents from " + serviceName, e);
            throw new IOException("List failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean deleteDocument(String fileId) throws IOException {
        if (!authenticated) {
            throw new IOException(serviceName + " service not authenticated");
        }
        
        logger.log("Deleting document from " + serviceName + ": " + fileId);
        
        try {
            String cloudPath = fileId.contains(CLOUD_STORAGE_DIR) ? 
                fileId : CLOUD_STORAGE_DIR + "/" + fileId;
            
            if (!cloudPath.endsWith(".json")) {
                cloudPath += ".json";
            }
            
            Files.deleteIfExists(Paths.get(cloudPath));
            logger.log("Document deleted successfully from " + serviceName);
            return true;
            
        } catch (Exception e) {
            logger.error("Failed to delete document from " + serviceName, e);
            throw new IOException("Delete failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String getServiceName() {
        return serviceName;
    }
    
    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }
}
