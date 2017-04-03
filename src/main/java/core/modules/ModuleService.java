package core.modules;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

/**
 * Created by cosmin on 01/04/2017.
 */
public class ModuleService {

    private static ModuleService service;
    private ServiceLoader<LocalModule> loader;

    private ModuleService() {

    }

    public static synchronized ModuleService getInstance() {
        if (service == null) {
            service = new ModuleService();
        }
        return service;
    }

    public void loadPlugin(String folderPath) {
        File pluginsDir = new File(folderPath);
        File[] jlist = pluginsDir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.getPath().toLowerCase().endsWith(".jar");
            }
        });

        URL[] urls = new URL[jlist.length];
        for (int i = 0; i < jlist.length; i++)
            try {
                urls[i] = jlist[i].toURI().toURL();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        URLClassLoader ucl = new URLClassLoader(urls);
        loader = ServiceLoader.load(LocalModule.class, ucl);

    }

    public <ModuleType extends Module> ModuleType getModuleInstance(String moduleName) {
        ModuleType module = null;

        try {
            Iterator<LocalModule> modules = loader.iterator();
            while (modules.hasNext()) {
                Module m = modules.next();
                return (ModuleType) m;
            }
        } catch (ServiceConfigurationError serviceError) {
            module = null;
            serviceError.printStackTrace();

        }
        return module;
    }

}
