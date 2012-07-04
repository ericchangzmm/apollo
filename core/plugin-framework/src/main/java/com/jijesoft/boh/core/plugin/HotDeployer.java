package com.jijesoft.boh.core.plugin;


/**
 * A simple class that starts a hot deploy thread for scanning for new plugins
 *
 */
public class HotDeployer
{
    private final PluginController pluginController;
    private final Thread hotDeploy;
    private boolean running;

    public HotDeployer(PluginController pluginController, final long period)
    {
        this.pluginController = pluginController;

        hotDeploy = new Thread("Plugin Hot Deploy")
        {
            @Override
            public void run()
            {
                running = true;
                while (running)
                {

                    HotDeployer.this.pluginController.scanForNewPlugins();
                    try
                    {
                        Thread.sleep(period);
                    }
                    catch (InterruptedException e)
                    {
                        // ignoring
                        break;
                    }
                }
            }
        };
        hotDeploy.setDaemon(true);
    }

    public void start()
    {
        hotDeploy.start();
    }

    public void stop()
    {
        running = false;
        hotDeploy.interrupt();
    }

    public boolean isRunning()
    {
        return running;
    }
}
