package no.microservices.typeahead.rest.validator;


import no.microservices.typeahead.core.exception.InvalidFieldException;

public class FieldValidator {
    private static final String[] allowedFields = {"namecreators"};

    public static void validate(String field) {
        boolean isValid = false;
        for (String allowedField : allowedFields) {
            if (field.equalsIgnoreCase(allowedField)) {
                isValid = true;
                break;
            }
        }

        if (!isValid) {
            throw new InvalidFieldException(field);
        }
    }
}
