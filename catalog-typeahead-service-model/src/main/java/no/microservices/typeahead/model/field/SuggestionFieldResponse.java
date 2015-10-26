/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.microservices.typeahead.model.field;


public class SuggestionFieldResponse {

    private String sentence;

    public SuggestionFieldResponse() {
    }

    public SuggestionFieldResponse(String sentence) {
        this.sentence = sentence;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }
}
