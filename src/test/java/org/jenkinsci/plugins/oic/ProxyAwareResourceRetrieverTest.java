package org.jenkinsci.plugins.oic;

import hudson.ProxyConfiguration;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@WithJenkins
class ProxyAwareResourceRetrieverTest {

    @Test
    void testOpenConnection_WithoutProxy(JenkinsRule r) throws Exception {
        r.jenkins.setProxy(null);

        ProxyAwareResourceRetriever retreiver = ProxyAwareResourceRetriever.createProxyAwareResourceRetriver(false);
        HttpURLConnection conn = retreiver.openHTTPConnection(r.getURL());
        assertNotNull(conn.getContent());
    }

    @Test
    void testOpenConnection_WithProxy(JenkinsRule r) throws Exception {
        r.jenkins.setProxy(new ProxyConfiguration("ignored.invalid", 8000));

        ProxyAwareResourceRetriever retreiver = ProxyAwareResourceRetriever.createProxyAwareResourceRetriver(false);
        HttpURLConnection conn = retreiver.openHTTPConnection(r.getURL());
        // should attempt to connect to the proxy which is ignored.invalid which can not be resolved
        // This throws UnknownHostException in some environments and ConnectException in others (e.g., Docker)
        assertThrows(IOException.class, conn::getContent);
    }

    @Test
    void testOpenConnection_WithProxyAndExclusion(JenkinsRule r) throws Exception {
        r.jenkins.setProxy(new ProxyConfiguration(
                "ignored.invalid", 8000, null, null, r.getURL().getHost()));

        ProxyAwareResourceRetriever retreiver = ProxyAwareResourceRetriever.createProxyAwareResourceRetriver(false);
        HttpURLConnection conn = retreiver.openHTTPConnection(r.getURL());
        assertNotNull(conn.getContent());
    }
}
