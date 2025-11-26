package com.documenteditor.cloudstorage;

import com.documenteditor.model.Document;
import java.io.IOException;
import java.util.List;

/**
 * Target interface for cloud storage operations.
 * Defines common operations that all cloud storage providers must support.
 * Part of the Adapter Pattern.
 */
public interface CloudStorageService {
    /**
     * Upload a document to cloud storage.
     * @param document The document to upload
     * @param filename The name to save the file as
     * @return The cloud file ID or URL
     * @throws IOException if upload fails
     */
    String uploadDocument(Document document, String filename) throws IOException;
    
    /**
     * Download a document from cloud storage.
     * @param fileId The cloud file ID or name
     * @return The loaded document
     * @throws IOException if download fails
     */
    Document downloadDocument(String fileId) throws IOException;
    
    /**
     * List all documents in cloud storage.
     * @return List of file names/IDs
     * @throws IOException if listing fails
     */
    List<String> listDocuments() throws IOException;
    
    /**
     * Delete a document from cloud storage.
     * @param fileId The cloud file ID or name
     * @return true if deletion was successful
     * @throws IOException if deletion fails
     */
    boolean deleteDocument(String fileId) throws IOException;
    
    /**
     * Get the name of the storage service (e.g., "Google Drive", "Dropbox").
     * @return Service name
     */
    String getServiceName();
    
    /**
     * Check if the service is authenticated and ready.
     * @return true if authenticated
     */
    boolean isAuthenticated();
}
