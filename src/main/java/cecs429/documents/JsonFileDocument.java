package cecs429.documents;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
//import java.nio.file.Paths;

import com.google.gson.Gson;

/**
 * Represents a document that is saved as a JSON file in the local file system.
 */
public class JsonFileDocument implements FileDocument {
	private int mDocumentId;
	private Path mFilePath;
	private String mDocumentTitle;
	private JsonDocument jsonDoc;

	/**
	 * Constructs a JsonFileDocument with the given document ID representing the file at the given
	 * absolute file path.
	 */
	public JsonFileDocument(int id, Path absoluteFilePath) {
		mDocumentId = id;
		mFilePath = absoluteFilePath;
	}
	
	@Override
	public int getId() {
		return mDocumentId;
	}

	@Override
	public Reader getContent() {
		try {
			// Create Gson instance
		    Gson gson = new Gson();
		    
		    // Create a reader for the JSON file
		    Reader reader = Files.newBufferedReader(mFilePath);
		    
		    // Convert JSON file to JsonDocument object
		    jsonDoc = gson.fromJson(reader, JsonDocument.class);
		    
		    Reader targetReader = new StringReader(jsonDoc.body);
		    
		    // Close the reader
		    reader.close();
		    jsonDoc = null;
		    
			return targetReader;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getTitle() {
		try {
			// Create Gson instance
		    Gson gson = new Gson();
		    
		    // Create a reader for the JSON file
		    Reader reader = Files.newBufferedReader(mFilePath);
		    
		    // Convert JSON file to JsonDocument object
		    jsonDoc = gson.fromJson(reader, JsonDocument.class);
		    
		    mDocumentTitle = jsonDoc.title + " (\"" + mFilePath.getFileName().toString() + "\")";
		    
		    // Close the reader
		    reader.close();
		    jsonDoc = null;
		    
		    return mDocumentTitle;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}

	@Override
	public Path getFilePath() {
		return mFilePath;
	}
	
	public static FileDocument loadJsonFileDocument(Path absolutePath, int documentId) {
		return new JsonFileDocument(documentId, absolutePath);
	}
	
	/**
	 * This class instantiates a JsonDocument object
	 */
	public static class JsonDocument { 
		private String body; 
		private String url;
		private String title; 
		public JsonDocument(){} 
	   
	  
	   
		public String toString() { 
			return "Document [ body: "+ body + ", url: " + url +  ", title: " + title + " ]"; 
		}

		public String getBody() {
			return body;
		}

		public void setBody(String body) {
			this.body = body;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		} 
		
		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}
	}
	
}
