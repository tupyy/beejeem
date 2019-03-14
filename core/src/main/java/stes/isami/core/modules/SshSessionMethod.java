package stes.isami.core.modules;

import com.sshtools.ssh.SshClient;
import com.sshtools.ssh.SshException;
import com.sshtools.ssh.SshSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Implement the basic method for executing a command on the remote host
 */
public class SshSessionMethod {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    private final SshClient sshClient;

    public SshSessionMethod(SshClient sshClient) {
        this.sshClient = sshClient;
    }

    public String executeSessionCommand (String command) throws SshException, IOException {

        SshSession sshSession = null;
        try {
            //init the term
            sshSession = sshClient.openSessionChannel();
            if (sshSession.requestPseudoTerminal("vt100", 80,
                    24, 0, 0)) {

                sshSession.executeCommand(command);
                InputStream in = sshSession.getInputStream();

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                int read;
                while ((read = in.read()) > -1) {
                    if (read > 0)
                        out.write(read);
                }

                in.close();
                sshSession.close();
                logger.debug("Output command: {}", new String(out.toByteArray()));
                return new String(out.toByteArray());
            }
            else {
                return "Failed to allocate pseudo terminal";
            }
        }
        catch (Throwable t) {
            throw new SshException(t);
        } finally {
            if (sshSession != null)
                sshSession.close();
        }
    }
}
