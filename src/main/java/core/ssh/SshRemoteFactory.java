package core.ssh;

import com.sshtools.net.SocketTransport;
import com.sshtools.sftp.SftpClient;
import com.sshtools.sftp.SftpStatusException;
import com.sshtools.ssh.*;
import com.sshtools.ssh.components.SshPublicKey;
import com.sshtools.ssh2.Ssh2Client;
import com.sshtools.ssh2.Ssh2Context;
import core.CoreEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * Implementation of the SshFactory interface.
 */
public class SshRemoteFactory implements SshFactory {

    private final Logger logger = LoggerFactory.getLogger(CoreEngine.class);

    /**
     * Events values
     */
    private final int CLIENT_CONNECTED = 1;
    private final int CLIENT_DISCONNECTED = 2;
    private final int CLIENT_CLOSING = 4;
    private final int USER_AUTHENTICATED = 3;

    private SshConnector con;

    /**
     * UUID is the job ID
     */
    private Map<UUID,SshSession> initializedSessions = Collections
            .synchronizedMap(new Hashtable<>());

    /**
     *  used to listen to closing events in order to notigy the GUI
     */
    private SshSession mainSession;

    private static SshClient ssh;

    private transient Vector listeners;

    public SshRemoteFactory() {
    }

    @Override
    public void connect(String hostname, String user, String password) throws SshException {

        //create the connector instance
        con = SshConnector.createInstance();

        // Lets do some host key verification
        HostKeyVerification hkv = new HostKeyVerification() {
            public boolean verifyHost(String hostname, SshPublicKey key) throws SshException{
                   logger.info("The connected host's key ("
                            + key.getAlgorithm() + ") is");
                    logger.info(key.getFingerprint());
                return true;
            }
        };

        con.getContext().setHostKeyVerification(hkv);
        con.getContext().setPreferredPublicKey(
                Ssh2Context.PUBLIC_KEY_SSHDSS);

        /**
         * Connect to the host
         *
         * IMPORTANT: We must use buffered mode so that we have a background
         * thread to fire data events back to us.
         */

        try {
            ssh = con.connect(new SocketTransport(hostname,
                    22), user, true);
            fireEvent(CLIENT_CONNECTED);
        } catch (IOException e) {
            throw new SshException("Cannot create the socket",SshException.CONNECT_FAILED);
        }

        /**
         * Authenticate the user using password authentication
         */
        PasswordAuthentication pwd = new PasswordAuthentication();
        pwd.setPassword(password);
        ssh.authenticate(pwd);

        if (ssh.isAuthenticated()) {
            fireEvent(USER_AUTHENTICATED);
            try {
                logger.debug("Main session created");
                mainSession = ssh.openSessionChannel();
                ChannelAdapter eventListener = new ChannelAdapter() {

                    public synchronized void channelClosed(SshChannel channel) {
                        fireEvent(CLIENT_DISCONNECTED);
                    }

                    public void channelClosing(SshChannel channel) {
                        fireEvent(CLIENT_CLOSING);
                    }
                };

                mainSession.addChannelEventListener(eventListener);
                } catch (ChannelOpenException e) {
               //cannot open channel;
                throw new SshException("Error opening main channel",SshException.CHANNEL_FAILURE);
            }
        }
    }

    @Override
    public void disconnect() {
        ssh.disconnect();
    }

    @Override
    public boolean isConnected() {

        if (ssh == null) {
            return false;
        }

        return ssh.isConnected();
    }

    @Override
    public boolean isAuthenticated() {
        return ssh.isAuthenticated();
    }

    public synchronized static SshClient getSshClient() throws SshException {
        if (ssh != null) {
            if (ssh.isAuthenticated()) {
                return ssh;
            }
        }
       throw new SshException("The client is either not connected or not autenticated",SshException.CONNECT_FAILED);
    }

    /**
     * Register a listener for JobEvents
     */
    synchronized public void addSshEventListener(SshListener l) {
        if (listeners == null) {
            listeners = new Vector();
        }
        listeners.addElement(l);
    }

    /**
     * Remove a listener for JobEvents
     */
    synchronized public void removeSshEventListener(SshListener l) {
        if (listeners == null) {
            listeners = new Vector();
        }
        listeners.removeElement(l);
    }

    /**
     * Fire JobEvent to all registered listeners
     */
    protected void fireEvent(int action) {
        // if we have no listeners, do nothing...
        if (listeners != null && !listeners.isEmpty()) {

            // make a copy of the listener list in case
            //   anyone adds/removes listeners
            Vector targets;
            synchronized (this) {
                targets = (Vector) listeners.clone();
            }

            // walk through the listener list and
            //   call the sunMoved methods in each
            Enumeration e = targets.elements();
            while (e.hasMoreElements()) {
                SshListener l = (SshListener) e.nextElement();
                switch (action) {
                    case USER_AUTHENTICATED:
                        l.authenticated();
                        break;
                    case CLIENT_CONNECTED:
                        l.connected();
                        break;
                }

            }
        }
    }
}
