package core.plugin;

import core.creator.Creator;
import core.modules.Module;

import java.util.List;

/**
 * Created by cosmin on 01/04/2017.
 */
public interface Plugin {

    public String getName();

    public  <T extends Module> T getModule(String moduleName);

    public <T> List<T> getModuleList();

    public <T extends Creator> T getCreator();

}
