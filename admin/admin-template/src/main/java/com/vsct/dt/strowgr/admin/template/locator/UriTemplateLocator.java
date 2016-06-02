package com.vsct.dt.strowgr.admin.template.locator;

import com.vsct.dt.strowgr.admin.core.TemplateLocator;
import com.vsct.dt.strowgr.admin.core.configuration.EntryPoint;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class UriTemplateLocator implements TemplateLocator {
    private static final Logger LOGGER = LoggerFactory.getLogger(UriTemplateLocator.class);

    public static final String URI_FIELD = "templateUri";
    private final CloseableHttpClient client;

    public UriTemplateLocator() {
        this.client = HttpClients.createDefault();
    }

    @Override
    public Optional<String> readTemplate(EntryPoint configuration) {
        return readTemplate(configuration.getContext().get(URI_FIELD));
    }

    public Optional<String> readTemplate(String uri) {
        try {
            HttpGet getTemplate = new HttpGet(uri);
            getTemplate.addHeader("Content-Type", "text/plain; charset=utf-8");
            LOGGER.debug("get template {}", uri);
            return client.execute(getTemplate, (response) -> {
                Optional<String> result = Optional.empty();
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    String entitySer = EntityUtils.toString(entity, "UTF-8");
                    if (entitySer == null) {
                        throw new IllegalStateException("template from " + uri + " has null content.");
                    } else {
                        LOGGER.debug("template from " + uri + " starts with " + entitySer.substring(0, Math.max(20, entitySer.length())));
                    }
                    result = Optional.of(entitySer);
                } else if (status != 404) {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
                return result;
            });
        } catch (IOException e) {
            LOGGER.error("Can't retrieve template from ", e);
            throw new RuntimeException(e);
        }
    }
}