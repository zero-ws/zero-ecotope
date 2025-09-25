package io.zerows.plugins.store.redis;

import java.io.Serializable;

/*
 * Redis extra configuration
 */
public class RedisExtra implements Serializable {

    private int port = 6379;
    private String host = "localhost";
    private long retryTimeout = 2 * 1000;
    private long timeout = 30 * 100;
    private String auth;

    public int getPort() {
        return this.port;
    }

    public void setPort(final int port) {
        this.port = port;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public long getRetryTimeout() {
        return this.retryTimeout;
    }

    public void setRetryTimeout(final long retryTimeout) {
        this.retryTimeout = retryTimeout;
    }

    public String getAuth() {
        return this.auth;
    }

    public void setAuth(final String auth) {
        this.auth = auth;
    }

    public long getTimeout() {
        return this.timeout;
    }

    public void setTimeout(final long timeout) {
        this.timeout = timeout;
    }

    @Override
    public String toString() {
        return "RedisExtra{" +
            "port=" + this.port +
            ", host='" + this.host + '\'' +
            ", retryTimeout=" + this.retryTimeout +
            ", timeout=" + this.timeout +
            ", auth='" + this.auth + '\'' +
            '}';
    }
}
