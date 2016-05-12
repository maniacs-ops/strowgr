package com.vsct.dt.haas.admin.gui.healthcheck;

import com.codahale.metrics.health.HealthCheck;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class NsqHealthcheck extends HealthCheck {

    private final HttpGet httpGet;
    private final CloseableHttpClient httpClient;

    public NsqHealthcheck(String host, int port) {
        httpClient = HttpClients.createDefault();
        httpGet = new HttpGet("http://" + host + ":" + port+"/ping");

    }

    @Override
    protected Result check() throws Exception {
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() != 200)
                throw new IllegalStateException("http request on " + httpGet.getURI().toString() + " returns status " + response.getStatusLine());
            return Result.healthy();
        } catch (Exception e) {
            return Result.unhealthy("can't execute " + httpGet.getURI().toString());
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }
}
