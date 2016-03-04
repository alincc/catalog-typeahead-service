/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.microservices.typeahead.model;


public class SuggestionResponse {
    private String label;
    private String value;
    private int count;

    public SuggestionResponse() {
    }

    public SuggestionResponse(String label, String value, int count) {
        this.label = label;
        this.value = value;
        this.count = count;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
