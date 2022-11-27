package com.example.drehomeservice.clients;

import com.example.drehomeservice.interfaces.HubApiInterface;
import com.example.drehomeservice.requests.NewDeviceConnectedCheckerRequest;
import feign.Response;
import lombok.Getter;
import okhttp3.OkHttpClient;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class HubClient extends AbstractClient {

    @Value("${hub.ip}")
    private String url;

    @Value("${token}")
    private String token;

    private OkHttpClient httpClient;
    @Getter
    private Map<Integer, String> connectedDevices;
    private HubApiInterface service;

    @PostConstruct
    private void init() {
        httpClient = createHttpClient();
        service = createHubApiInterface(url);
        connectedDevices = getConnectedDevicesFromHub();
    }

    private Map<Integer, String> getConnectedDevicesFromHub() {
        NewDeviceConnectedCheckerRequest request = new NewDeviceConnectedCheckerRequest(token, service, url);
        request.setDaemon(true);
        request.start();
        return getDevicesIdsFromResponse(request.getResponse());
    }

    private Map<Integer, String> getDevicesIdsFromResponse(Response response) {
        Map<Integer, String> connectedDevices = new HashMap<>();
        try (InputStream inputStream = response.body().asInputStream()) {
            String responseDetails = IOUtils.toString(inputStream, Charsets.toCharset(StandardCharsets.UTF_8));
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            try (StringReader stringReader = new StringReader(responseDetails)) {
                Document doc = builder.parse(new InputSource(stringReader));
                doc.getDocumentElement().normalize();
                NodeList devIds = doc.getElementsByTagName("dev_id");
                NodeList devNames = doc.getElementsByTagName("dev_name");
                for (int i = 0; i < devIds.getLength(); i++) {
                    connectedDevices.put(Integer.valueOf(devIds.item(i).getTextContent()), devNames.item(i).getTextContent());
                }
            } catch (SAXException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException | ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        return connectedDevices;
    }

    private HubApiInterface createHubApiInterface(String url) {
        return createApiService(httpClient, HubApiInterface.class, url);
    }
}
