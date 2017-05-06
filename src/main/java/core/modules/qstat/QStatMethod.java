package core.modules.qstat;

import com.sshtools.ssh.SshClient;
import com.sshtools.ssh.SshException;
import com.sshtools.ssh.SshSession;
import core.modules.Method;
import core.modules.MethodResult;
import core.modules.SshSessionMethod;
import core.modules.StandardMethodResult;
import core.parameters.parametertypes.StringParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

/**
 * This method runs the qstat command and return the output
 */
public class QStatMethod extends SshSessionMethod implements Method {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    private static final String METHOD_NAME = "QStatMethod";
    private final String command;

    public QStatMethod(SshClient sshClient, String command) {
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
            String outString = executeSessionCommand(createQStatCommand(command));
            StringParameter qstatOutput = new StringParameter("qstatOutput","Output from qstat","core",outString);

            if (outString.contains("No such file or directory")) {
                return new StandardMethodResult("core",METHOD_NAME,UUID.randomUUID(),StandardMethodResult.ERROR,outString);
            }
            else {
                StandardMethodResult result = new StandardMethodResult("core", METHOD_NAME, UUID.randomUUID(), StandardMethodResult.OK, "");
                result.addParameter(qstatOutput);
                return result;
            }
        } catch (SshException e) {
            logger.error(e.getMessage());
            return new StandardMethodResult("core",METHOD_NAME, UUID.randomUUID(), StandardMethodResult.ERROR,e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
            return new StandardMethodResult("core",METHOD_NAME, UUID.randomUUID(), StandardMethodResult.ERROR,e.getMessage());

        }
    }

    @Override
    public void cancel() {

    }

    private String createQStatCommand(String command) {
        StringBuilder sb = new StringBuilder();
        sb.append(". /opt/sge/default/common/settings.sh;").append(command);

        return sb.toString();
    }
}
