/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.microservices.typeahead.model;


public class SuggestionResponse {

    private String sentence;
    private int count;

    public SuggestionResponse(String sentence, int count) {
        this.sentence = sentence;
        this.count = count;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
