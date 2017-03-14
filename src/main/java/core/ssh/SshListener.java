package core.ssh;

/**
 * Created by tctupangiu on 14/02/2017.
 */
public interface SshListener {

    /**
     * Main channel is closed
     */
    void channelClosed();

    /**
     * Main channel is closing
     */
    void channelClosing();

    /**
     * A connection to remote host has been established
     */
    void connected();

    /**
     * The user has been authenticated
     */
    void authenticated();

}
