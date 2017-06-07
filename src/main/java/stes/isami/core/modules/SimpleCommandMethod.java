package stes.isami.core.modules;

import com.sshtools.ssh.SshClient;
import com.sshtools.ssh.SshException;
import stes.isami.core.parameters.parametertypes.StringParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

/**
 * This method runs any batch system command.
 */
public class SimpleCommandMethod extends SshSessionMethod implements Method {

    private static final String METHOD_NAME = "SimpleCommandMethod";
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    private final String command;

    public SimpleCommandMethod(SshClient sshClient, String command) {
        super(sshClient);
        this.command = command;
    }

    @Override
    public String getName() {
        return METHOD_NAME;
    }

    @Override
    public MethodResult execute() {

        try {
            String outString = executeSessionCommand(createCommand(command));
            StringParameter commandOutput = new StringParameter("SimpleCommandMethod","Output from SimpleCommandMethod", "stes/isami/core",outString);
            StandardMethodResult result = new StandardMethodResult("stes/isami/core",METHOD_NAME, UUID.randomUUID(), StandardMethodResult.OK, "");
            result.addParameter(commandOutput);
            return  result;
        } catch (SshException e) {
            logger.error(e.getMessage());
            return new StandardMethodResult("stes/isami/core",METHOD_NAME, UUID.randomUUID(), StandardMethodResult.ERROR,e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
            return new StandardMethodResult("stes/isami/core",METHOD_NAME, UUID.randomUUID(), StandardMethodResult.ERROR,e.getMessage());

        }
    }

    @Override
    public void cancel() {

    }

    private String createCommand(String command) {
        StringBuilder sb = new StringBuilder();
        sb.append(". /opt/sge/default/common/settings.sh;").append(command);

        return sb.toString();
    }
}
