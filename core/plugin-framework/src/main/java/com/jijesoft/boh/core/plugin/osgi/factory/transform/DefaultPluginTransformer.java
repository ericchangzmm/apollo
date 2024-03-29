package com.jijesoft.boh.core.plugin.osgi.factory.transform;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;

import com.jijesoft.boh.core.plugin.JarPluginArtifact;
import com.jijesoft.boh.core.plugin.PluginArtifact;
import com.jijesoft.boh.core.plugin.osgi.container.OsgiPersistentCache;
import com.jijesoft.boh.core.plugin.osgi.factory.transform.model.SystemExports;
import com.jijesoft.boh.core.plugin.osgi.factory.transform.stage.AddBundleOverridesStage;
import com.jijesoft.boh.core.plugin.osgi.factory.transform.stage.ComponentImportSpringStage;
import com.jijesoft.boh.core.plugin.osgi.factory.transform.stage.ComponentSpringStage;
import com.jijesoft.boh.core.plugin.osgi.factory.transform.stage.GenerateManifestStage;
import com.jijesoft.boh.core.plugin.osgi.factory.transform.stage.HostComponentSpringStage;
import com.jijesoft.boh.core.plugin.osgi.factory.transform.stage.ModuleTypeSpringStage;
import com.jijesoft.boh.core.plugin.osgi.factory.transform.stage.ScanDescriptorForHostClassesStage;
import com.jijesoft.boh.core.plugin.osgi.hostcomponents.HostComponentRegistration;

/**
 * Default implementation of plugin transformation that uses stages to convert a plain JAR into an OSGi bundle.
 */
public class DefaultPluginTransformer implements PluginTransformer
{
    private static final Logger log = Logger.getLogger(DefaultPluginTransformer.class);

    private final String pluginDescriptorPath;
    private final List<TransformStage> stages;
    private final File bundleCache;
    private final SystemExports systemExports;
    private final Set<String> applicationKeys;

    /**
     * Constructs a transformer with the default stages
     *
     * @param cache The OSGi cache configuration for transformed plugins
     * @param systemExports The packages the system bundle exports
     * @param pluginDescriptorPath The path to the plugin descriptor
     */
    public DefaultPluginTransformer(OsgiPersistentCache cache, SystemExports systemExports, Set<String> applicationKeys, String pluginDescriptorPath)
    {
        this(cache, systemExports, applicationKeys, pluginDescriptorPath, new ArrayList<TransformStage>()
        {{
            add(new AddBundleOverridesStage());
            add(new ComponentImportSpringStage());
            add(new ComponentSpringStage());
            add(new HostComponentSpringStage());
            add(new ModuleTypeSpringStage());
            add(new ScanDescriptorForHostClassesStage());
            add(new GenerateManifestStage());
        }});
    }

    /**
     * Constructs a transformer and its stages
     *
     * @param cache The OSGi cache configuration for transformed plugins
     * @param systemExports The packages the system bundle exports
     * @param pluginDescriptorPath The descriptor path
     * @param stages A set of stages
     */
    public DefaultPluginTransformer(OsgiPersistentCache cache, SystemExports systemExports, Set<String> applicationKeys, String pluginDescriptorPath, List<TransformStage> stages)
    {
        Validate.notNull(pluginDescriptorPath, "The plugin descriptor path is required");
        Validate.notNull(stages, "A list of stages is required");
        this.pluginDescriptorPath = pluginDescriptorPath;
        this.stages = Collections.unmodifiableList(new ArrayList<TransformStage>(stages));
        this.bundleCache = cache.getTransformedPluginCache();
        this.systemExports = systemExports;
        this.applicationKeys = applicationKeys;

    }

    /**
     * Transforms the file into an OSGi bundle
     *
     * @param pluginJar The plugin jar
     * @param regs      The list of registered host components
     * @return The new OSGi-enabled plugin jar
     * @throws PluginTransformationException If anything goes wrong
     */
    public File transform(File pluginJar, List<HostComponentRegistration> regs) throws PluginTransformationException
    {
        return transform(new JarPluginArtifact(pluginJar), regs);
    }

    /**
     * Transforms the file into an OSGi bundle
     *
     * @param pluginArtifact The plugin artifact, usually a jar
     * @param regs      The list of registered host components
     * @return The new OSGi-enabled plugin jar
     * @throws PluginTransformationException If anything goes wrong
     */
    public File transform(PluginArtifact pluginArtifact, List<HostComponentRegistration> regs) throws PluginTransformationException
    {
        Validate.notNull(pluginArtifact, "The plugin artifact is required");
        Validate.notNull(regs, "The host component registrations are required");

        File artifactFile = pluginArtifact.toFile();

        // Look in cache first
        File cachedPlugin = getFromCache(artifactFile);
        if (cachedPlugin != null)
        {
            return cachedPlugin;
        }

        TransformContext context = new TransformContext(regs, systemExports, pluginArtifact, applicationKeys, pluginDescriptorPath);
        for (TransformStage stage : stages)
        {
            stage.execute(context);
        }

        // Create a new jar by overriding the specified files
        try
        {
            if (log.isDebugEnabled())
            {
                StringBuilder sb = new StringBuilder();
                sb.append("Overriding files in ").append(pluginArtifact.toString()).append(":\n");
                for (Map.Entry<String, byte[]> entry : context.getFileOverrides().entrySet())
                {
                    sb.append("==").append(entry.getKey()).append("==\n");

                    // Yes, this doesn't take into account encoding, but since only text files are overridden, that
                    // should be fine
                    sb.append(new String(entry.getValue()));
                }
                log.debug(sb.toString());
            }
            return addFilesToExistingZip(artifactFile, context.getFileOverrides());
        }
        catch (IOException e)
        {
            throw new PluginTransformationException("Unable to add files to plugin jar");
        }
    }

    private File getFromCache(File artifact)
    {
        String name = generateCacheName(artifact);
        for (File child : bundleCache.listFiles())
        {
            if (child.getName().equals(name))
                return child;
        }
        return null;
    }

    /**
     * Generate a cache name that incorporates the timestap and preserves the extension
     * @param file The original file to cache
     * @return The new file name
     */
    static String generateCacheName(File file)
    {
        int dotPos = file.getName().lastIndexOf('.');
        if (dotPos > 0 && file.getName().length() - 1 > dotPos)
        {
            return file.getName().substring(0, dotPos) + "_" + file.lastModified() + file.getName().substring(dotPos);
        }
        else
        {
            return file.getName() + "_" + file.lastModified();
        }
    }


    /**
     * Creates a new jar by overriding the specified files in the existing one
     *
     * @param zipFile The existing zip file
     * @param files   The files to override
     * @return The new zip
     * @throws IOException If there are any problems processing the streams
     */
    File addFilesToExistingZip(File zipFile,
                                      Map<String, byte[]> files) throws IOException
    {
        // get a temp file
        File tempFile = new File(bundleCache, generateCacheName(zipFile));
        // delete it, otherwise you cannot rename your existing zip to it.
        byte[] buf = new byte[1024];


        ZipInputStream zin = null;
        ZipOutputStream out = null;
        try
        {
            zin = new ZipInputStream(new FileInputStream(zipFile));
            out = new ZipOutputStream(new FileOutputStream(tempFile));

            ZipEntry entry = zin.getNextEntry();
            while (entry != null)
            {
                String name = entry.getName();
                if (!files.containsKey(name))
                {
                    // Add ZIP entry to output stream.
                    out.putNextEntry(new ZipEntry(name));
                    // Transfer bytes from the ZIP file to the output file
                    int len;
                    while ((len = zin.read(buf)) > 0)
                        out.write(buf, 0, len);
                }
                entry = zin.getNextEntry();
            }
            // Close the streams
            zin.close();
            // Compress the files
            for (Map.Entry<String, byte[]> fentry : files.entrySet())
            {
                InputStream in = null;
                try
                {
                    in = new ByteArrayInputStream(fentry.getValue());
                    // Add ZIP entry to output stream.
                    out.putNextEntry(new ZipEntry(fentry.getKey()));
                    // Transfer bytes from the file to the ZIP file
                    int len;
                    while ((len = in.read(buf)) > 0)
                    {
                        out.write(buf, 0, len);
                    }
                    // Complete the entry
                    out.closeEntry();
                }
                finally
                {
                    IOUtils.closeQuietly(in);
                }
            }
            // Complete the ZIP file
            out.close();
        }
        finally
        {
            // Close just in case
            IOUtils.closeQuietly(zin);
            IOUtils.closeQuietly(out);
        }
        return tempFile;
    }

    
}
