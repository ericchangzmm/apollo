package com.jijesoft.boh.core.plugin.util.resource;

import java.net.URL;
import java.io.InputStream;

/**
 * Resource loader that always returns null
 *
 */
public class NoOpAlternativeResourceLoader implements AlternativeResourceLoader
{
    public URL getResource(String path)
    {
        return null;
    }

    public InputStream getResourceAsStream(String name)
    {
        return null;
    }
}
