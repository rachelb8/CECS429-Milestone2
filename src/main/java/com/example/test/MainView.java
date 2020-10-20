package com.example.test;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import java.util.concurrent.TimeUnit;


/**
 * A MainView class.
 * <p>
 * To implement a Vaadin view just extend any Vaadin component and
 * use @Route annotation to announce it in a URL as a Spring managed
 * bean.
 * Use the @PWA annotation make the application installable on phones,
 * tablets and some desktop browsers.
 * <p>
 * A new instance of this class is created for every new user and every
 * browser tab/window.
 */
@SuppressWarnings("serial")
@Route
@PWA(name = "Vaadin Application",
        shortName = "Vaadin App",
        description = "This is an example Vaadin application.",
        enableInstallPrompt = false)
@CssImport("./styles/shared-styles.css")
@CssImport(value = "./styles/vaadin-text-field-styles.css", themeFor = "vaadin-text-field")
public class MainView extends VerticalLayout {
	
	private IndexerService service;
	
    /**
     * Construct a new Vaadin view
     * Build the initial UI state for the user accessing the application.
     * @throws InterruptedException 
     */
    public MainView() throws InterruptedException {
    	service = new IndexerService();

        // Use TextField for standard text input for directory selection
        TextField dirField = new TextField("Please Enter a Directory:");
        dirField.addThemeName("bordered");

        // Initialize the index button
        Button indexButton = new Button("Index",
                e -> {

                	// Directory field null check 
                	if (dirField.getValue() != "") {
                		long result = service.run(dirField.getValue());	
                 		String indexTime = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(result));
                 		
                 		// Send the index time to the next view
                        ComponentUtil.setData(UI.getCurrent(), String.class, indexTime);
                        ComponentUtil.setData(UI.getCurrent(), IndexerService.class, service);
                        
                        // Move to SearchView
                        UI.getCurrent().navigate(SearchView.class);
                	} else {
                		Span warning = new Span("No directory has been selected.");
                		warning.getElement().getStyle().set("font-size", "12px");
                		add(warning);
                	}    

                });

        // Theme variants give you predefined extra styles for components
        indexButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        
        //Align Items to the center
        setAlignItems(Alignment.CENTER);

        // Pressing enter in this view clicks the index button
        indexButton.addClickShortcut(Key.ENTER);

        // Use custom CSS classes to apply styling
        addClassName("centered-content");
        
        add(dirField, indexButton);	
    }
}
