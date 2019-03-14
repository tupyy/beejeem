package stes.isami.core.tasks;


import stes.isami.core.modules.Method;
import stes.isami.core.modules.MethodResult;

import java.util.function.Supplier;

/**
 * Simple implementation of the Callable which execute a methods
 */
public class ModuleTask implements Supplier<MethodResult> {

    private Method method;
    private String title;

    public ModuleTask(String title, Method method) {
        this.title = title;
        this.method = method;
    }

    @Override
    public MethodResult get()  {

        MethodResult result = getMethod().execute();

        return result;

    }


    public Method getMethod() {
        return method;
    }

    public String getTitle() {
        return title;
    }
}
