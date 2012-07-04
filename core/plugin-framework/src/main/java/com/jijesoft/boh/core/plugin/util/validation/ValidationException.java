package com.jijesoft.boh.core.plugin.util.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.Validate;

import com.jijesoft.boh.core.plugin.PluginParseException;

/**
 * Exception for a validation error parsing DOM4J nodes
 *
 */
public class ValidationException extends PluginParseException
{
    private final List<String> errors;
    public ValidationException(String msg, List<String> errors)
    {
        super(msg);
        
        Validate.notNull(errors);
        this.errors = Collections.unmodifiableList(new ArrayList<String>(errors));
    }

    /**
     * @return a list of the original errors
     */
    public List<String> getErrors()
    {
        return errors;
    }
}
