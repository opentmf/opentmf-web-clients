package org.opentmf.client.util;


import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.Times;
import org.mockserver.model.StringBody;
import org.springframework.http.HttpStatus;

/**
 * @author Yusuf Bozkurt
 */
public class MockServerUtils {

  public static final ClientAndServer clientAndServer = new ClientAndServer();
  public static final String BASE_URL = "http://localhost:" + clientAndServer.getLocalPort();

  public static void expectGet(String path, int count, String responseBody, HttpStatus status) {
    clientAndServer
        .when(
            request()
                .withMethod("GET")
                .withPath(path),
            Times.exactly(count))
        .respond(
            response()
                .withBody(new StringBody(responseBody))
                .withStatusCode(status.value()));
  }
}
