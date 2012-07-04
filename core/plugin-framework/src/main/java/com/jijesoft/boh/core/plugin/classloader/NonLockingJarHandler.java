package com.jijesoft.boh.core.plugin.classloader;


import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * This is copied from Classwords 1.1 org.codehaus.classworlds.uberjar.protocol.jar.Handler
 * so that an additional dependency does not need to be added to plugins.  The formatting is left as is to reduce
 * the diff.
 */
public class NonLockingJarHandler
    extends URLStreamHandler
{
    // ----------------------------------------------------------------------
    //     Class members
    // ----------------------------------------------------------------------

    /**
     * Singleton instance.
     */
    private static final NonLockingJarHandler INSTANCE = new NonLockingJarHandler();

    // ----------------------------------------------------------------------
    //     Class methods
    // ----------------------------------------------------------------------

    /**
     * Retrieve the singleton instance.
     *
     * @return The singleton instance.
     */
    public static NonLockingJarHandler getInstance()
    {
        return INSTANCE;
    }

    // ----------------------------------------------------------------------
    //     Constructors
    // ----------------------------------------------------------------------


    /**
     * Construct.
     */
    public NonLockingJarHandler()
    {
        // intentionally left blank
    }

    // ----------------------------------------------------------------------
    //     Instance methods
    // ----------------------------------------------------------------------

    /**
     * @see java.net.URLStreamHandler
     */
    public URLConnection openConnection( URL url )
        throws IOException
    {
        return new NonLockingJarUrlConnection(url);
    }

    /**
     * @see java.net.URLStreamHandler
     */
    public void parseURL( URL url,
                          String spec,
                          int start,
                          int limit )
    {
        String specPath = spec.substring( start,
                                          limit );

        String urlPath = null;

        if ( specPath.charAt( 0 ) == '/' )
        {
            urlPath = specPath;
        }
        else if ( specPath.charAt( 0 ) == '!' )
        {
            String relPath = url.getFile();

            int bangLoc = relPath.lastIndexOf( "!" );

            if ( bangLoc < 0 )
            {
                urlPath = relPath + specPath;
            }
            else
            {
                urlPath = relPath.substring( 0,
                                             bangLoc ) + specPath;
            }
        }
        else
        {
            String relPath = url.getFile();

            if ( relPath != null )
            {
                int lastSlashLoc = relPath.lastIndexOf( "/" );

                if ( lastSlashLoc < 0 )
                {
                    urlPath = "/" + specPath;
                }
                else
                {
                    urlPath = relPath.substring( 0,
                                                 lastSlashLoc + 1 ) + specPath;
                }
            }
            else
            {
                urlPath = specPath;
            }
        }

        setURL( url,
                "jar",
                "",
                0,
                null,
                null,
                urlPath,
                null,
                null );
    }
}
