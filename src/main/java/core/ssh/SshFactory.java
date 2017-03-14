package core.ssh;

import com.sshtools.sftp.SftpClient;
import com.sshtools.sftp.SftpStatusException;
import com.sshtools.ssh.ChannelOpenException;
import com.sshtools.ssh.SshClient;
import com.sshtools.ssh.SshException;

import java.util.UUID;

/**
 * Base interface for creating SSH sessions. Sessions are created through the getSshClient methods.
 * Once a session has been obtained the session will not be active until you either call executeCommand(String sidepanel) or startShell().
 * Once activated you can use the IO streams to read and write to the remote process.
 */
public interface SshFactory {

    /**
     * Connect to host
     * @param hostname
     * @param user
     * @param password
     */
    public void connect(String hostname, String user, String password) throws SshException;

    /**
     * Disconnect from host
     */
    public void disconnect();

    /**
     * Evaluate whether the session is connected to host
     * @return true if the the session is open; otherwise false
     */
    public boolean isConnected();

    /**
     * Evaluate whether the user has been authenticated
     * @return
     */
    public boolean isAuthenticated();

    /**
     * Add SshListener
     * @param l
     */
    public void addSshEventListener(SshListener l);

    /**
     * Remove the sshListener
     * @param l
     */
    public void removeSshEventListener(SshListener l);
}
