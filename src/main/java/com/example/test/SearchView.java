package com.example.test;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import org.apache.commons.io.IOUtils;
import cecs429.documents.*;
import cecs429.query.RankedRetrieval.DocumentScore;
import cecs429.text.*;

/**
 * A SearchView class
 * */
@SuppressWarnings("serial")
@Route(value = "search")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class SearchView extends VerticalLayout {
	
	VerticalLayout displayLayout;
	Grid<Document> documentGrid;
	Grid<DocumentScore> documentScoreGrid;
	
	public SearchView() {
		
		// Retrieve the service instance from previous view
		IndexerService service = ComponentUtil.getData(UI.getCurrent(), IndexerService.class);
		
		// Initialize icons
	    Icon searchIcon = new Icon(VaadinIcon.SEARCH);
	    searchIcon.setSize("32px");
	    searchIcon.setColor("white");
	    
	    Icon stemmerIcon = new Icon(VaadinIcon.BOLT);
	    stemmerIcon.setSize("32px");
	    stemmerIcon.setColor("white");
		
	    // Initialize layouts
		HorizontalLayout hLayoutSearch = new HorizontalLayout();
		hLayoutSearch.setSpacing(true);
		
		
		VerticalLayout vLayoutSearch = new VerticalLayout();
		vLayoutSearch.setAlignItems(FlexComponent.Alignment.CENTER);
		vLayoutSearch.setSpacing(false);
		
		HorizontalLayout hLayoutButtons = new HorizontalLayout();
		hLayoutButtons.setPadding(true);
	   
		
	    // Use TextField for standard text input for the query
	    TextField queryField = new TextField("Enter a Query:");
	    queryField.addThemeName("bordered");
	    
	    // Retrieve index time from previous view
	    String indexTime = ComponentUtil.getData(UI.getCurrent(), String.class);
	    Span indexTimeLabel = new Span("Time to Index Corpus: \n" + indexTime + " seconds");
	    indexTimeLabel.getElement().getStyle().set("font-size", "10px");
	    
	    // Initialize radio button group for the different query modes
	    RadioButtonGroup<String> queryModeRadioGroup = new RadioButtonGroup<>();
	    queryModeRadioGroup.setLabel("Select a Query Mode:");
	    queryModeRadioGroup.setItems("Boolean", "Ranked");
	    queryModeRadioGroup.setValue("Boolean");
	    queryModeRadioGroup.getStyle().set("white-space","nowrap");

	    // Initialize directory button
	    // Move back to MainView
	    Button dirButton = new Button("New Directory",
	            e -> UI.getCurrent().navigate(MainView.class));
	    
	    // Initialize vocab button
	    Button vocabButton = new Button("Display Vocab",
	            e -> {
	            	
	            	cleanUpUI();
	            	
	            	if(displayLayout == null) {
		            	displayLayout = new VerticalLayout();
		            	displayLayout.setAlignItems(FlexComponent.Alignment.CENTER);
		            	Span vocabLabel = new Span("Vocabulary (1st 1000 Terms)");
		            	vocabLabel.getElement().getStyle().set("font-size", "32px");
		            	vocabLabel.getElement().getStyle().set("font-weight", "bold");
		            	displayLayout.add(vocabLabel);
		            	ArrayList<String> vocabList = new ArrayList<>();
		            	vocabList = service.getVocab();
		            	for(int i = 0; i < 1000; i++) {
		            		  displayLayout.add(new Span(vocabList.get(i)));
		            	}
		            	Span vocabNum = new Span("Total Number of Vocabulary Terms: " + vocabList.size());
		            	vocabNum.getElement().getStyle().set("font-size", "10px");
		            	displayLayout.add(vocabNum);
		            	add(displayLayout);
	            	}
	            	
	    
	            });
	    
	    // Initialize search button
	    Button searchButton = new Button(searchIcon,
	            e -> {
	            	
	            	cleanUpUI();
	            	
	            	// Query field empty check
	            	if (queryField.getValue().isEmpty()) {
	            		displayLayout = new VerticalLayout();
	    		        displayLayout.setAlignItems(FlexComponent.Alignment.CENTER);
	            		Span noQueryLabel = new Span("Please Enter a Query.");
	            		displayLayout.add(noQueryLabel);
	            		add(displayLayout);
	            		
	            	} else {
	            		
	            		if(queryModeRadioGroup.getValue().equals("Boolean")) {
	            			List<Document> documents = new ArrayList<>();
			            	documents = service.searchBoolean(queryField.getValue());
			            	if(!documents.isEmpty()) {
			            		displayLayout = new VerticalLayout();
			    		        displayLayout.setAlignItems(FlexComponent.Alignment.START);
			    		        displayLayout.setPadding(false);
			            		documentGrid = new Grid<>();
				            	documentGrid.setItems(documents);
			    		        Span numDocsLabel = new Span("Number of Documents: " + String.valueOf(documents.size()));	
			            		displayLayout.add(numDocsLabel);
				            	documentGrid.addColumn(Document::getTitle).setHeader("Document Title");
				            	documentGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER,
				            	        GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);
				            	
				            	// Add a listener for the document grid
				            	// If the user selects a document, a dialog box will display its content
				            	documentGrid.asSingleSelect().addValueChangeListener(event -> {
				            		Dialog dialog = new Dialog();

				                    try {
				                    	VerticalLayout dialogDisplay = new VerticalLayout();
				                    	Span documentTitle = new Span(event.getValue().getTitle());
				                    	documentTitle.getElement().getStyle().set("font-weight", "bold");
										String content = IOUtils.toString(event.getValue().getContent());
										Span documentContent = new Span(content);
										
										dialogDisplay.add(documentTitle, documentContent);
										dialog.add(dialogDisplay);
										dialog.setWidth("1000px");
					            		dialog.setHeight("750px");
					            		dialog.open();
									} catch (IOException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
				            	});
				            	add(documentGrid, displayLayout);
			            	} else {
			            		displayLayout = new VerticalLayout();
			    		        displayLayout.setAlignItems(FlexComponent.Alignment.CENTER);
			    		        Span notFoundLabel = new Span("Not Found. Please try entering another query.");	
			            		displayLayout.add(notFoundLabel);
			            		add(displayLayout);
			            	}
	            		} else if (queryModeRadioGroup.getValue().equals("Ranked")) {
	            			PriorityQueue<DocumentScore> documents = new PriorityQueue<>();
	            			documents = service.searchRanked(queryField.getValue());
	            			
	            			if(!documents.isEmpty()) {
	            				
	            				displayLayout = new VerticalLayout();
	            				displayLayout.setAlignItems(FlexComponent.Alignment.START);
			    		        displayLayout.setPadding(false);
			    		        
			            		documentScoreGrid = new Grid<>();
				            	documentScoreGrid.setItems(documents);
			    		        Span numDocsLabel = new Span("Number of Documents: " + String.valueOf(documents.size()));	
			            		displayLayout.add(numDocsLabel);
				            	documentScoreGrid.addColumn(DocumentScore::getTitle).setHeader("Document Title");
				            	documentScoreGrid.addColumn(new NumberRenderer<>(DocumentScore::getScore, "%.5f")).setHeader("Accumulator Value");
				            	documentScoreGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER,
				            	        GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_ROW_STRIPES);
				            	
				            	// Add a listener for the document grid
				            	// If the user selects a document, a dialog box will display its content
				            	documentScoreGrid.asSingleSelect().addValueChangeListener(event -> {
				            		Dialog dialog = new Dialog();

				                    try {
				                    	VerticalLayout dialogDisplay = new VerticalLayout();
				                    	Span documentTitleScore = new Span(event.getValue().getTitle() + " (Score: " + event.getValue().getScore() + ")");
				                    	documentTitleScore.getElement().getStyle().set("font-weight", "bold");
										String content = IOUtils.toString(event.getValue().getContent());
										Span documentContent = new Span(content);
										
										dialogDisplay.add(documentTitleScore, documentContent);
										dialog.add(dialogDisplay);
										dialog.setWidth("1000px");
					            		dialog.setHeight("750px");
					            		dialog.open();
									} catch (IOException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
				            	});
				            	
				            	add(documentScoreGrid, displayLayout);
			            	} else {
			            		displayLayout = new VerticalLayout();
			    		        displayLayout.setAlignItems(FlexComponent.Alignment.CENTER);
			    		        Span notFoundLabel = new Span("Not Found. Please try entering another query.");	
			            		displayLayout.add(notFoundLabel);
			            		add(displayLayout);
			            	}
	    			        
	            		}
	            		
	            	}
	            	
	            });
	    
	    // Initialize stemmer button
	    Button stemButton = new Button(stemmerIcon, 
	    		e -> {
	    			
	    			cleanUpUI();
	            	
	            	displayLayout = new VerticalLayout();
    		        displayLayout.setAlignItems(FlexComponent.Alignment.CENTER);
	    			if (queryField.getValue().isEmpty()) {
	            		Span noTokenLabel = new Span("Please Enter a Token String to be Stemmed.");
	            		displayLayout.add(noTokenLabel);
	            		add(displayLayout);
	    			} else {
	    				MSOneTokenProcessor processer = new MSOneTokenProcessor();
		    			
		    			Span stemmedLabel = new Span("Stemmed Term");
		    			stemmedLabel.getElement().getStyle().set("font-size", "32px");
		            	stemmedLabel.getElement().getStyle().set("font-weight", "bold");
		            	displayLayout.add(stemmedLabel);
		            	List<String> processedTokens = processer.processToken(queryField.getValue());
		    			for(String lToken: processedTokens) {
		    				displayLayout.add(new Span(queryField.getValue() + " -> " + lToken));
		    			}
		    			add(displayLayout);
	    			}
	    			
	    		});

	    // Add theme to buttons
	    dirButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
	    vocabButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
	    searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
	    stemButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
	    
	    // Add description for buttons
	    searchButton.getElement().setProperty("title", "Search");
	    stemButton.getElement().setProperty("title", "Stem Token");
	    searchButton.addClickShortcut(Key.ENTER);
	   
	    // Formatting
	    hLayoutSearch.setDefaultVerticalComponentAlignment(
	            FlexComponent.Alignment.END);
	    

	    // Add components to their respective layouts
	    hLayoutButtons.add(dirButton, vocabButton);
	    hLayoutSearch.add(queryField, searchButton, stemButton, queryModeRadioGroup);
	    vLayoutSearch.add(hLayoutSearch, indexTimeLabel, hLayoutButtons);

	    // Add layout to the view
	    add(vLayoutSearch);
	}
	
	/**
	 * Remove existing layouts/grids before placing new ones
	 * */
	public void cleanUpUI() {
		if(displayLayout != null) {
    		remove(displayLayout);
    		displayLayout = null;
    	}
    	
    	if(documentGrid != null) {
    		remove(documentGrid);
    		documentGrid = null;
    	}
    	
    	if(documentScoreGrid != null) {
    		remove(documentScoreGrid);
    		documentScoreGrid = null;
    	}
	}
	
}
