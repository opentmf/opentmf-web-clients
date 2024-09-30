# pia-web-clients
General purpose WebClient libraries that includes Logbook, configurable connection properties, fixed headers support, and token retrieval with implicit access token caching.

Currently, two providers have been provided:

1. Openid Auth WebClient
2. Basic Auth WebClient

## Usage

### A) Openid Auth WebClient

#### Maven Dependency
```xml
    <dependency>
      <groupId>com.pia.commons</groupId>
      <artifactId>pia-openid-webclient-provider</artifactId>
      <version>1.0.2</version>
    </dependency>
```

#### Sample Configuration (Minimal)
```yaml
pia.webclient:
  openid:
    default:
      connection-provider-name: client1-provider
      token-config:
        token-url: http://localhost:1080/token
        cache-expiry-seconds: 3600
        cache-name: client1-token-cache
        form-data:
          username: user
          password: pass
          scope: openid
          grant_type: password
```

#### Sample Configuration (Full)
```yaml
pia.webclient:
  openid:
    default:
      connection-provider-name: client2-provider
      max-connections: 100
      request-timeout-millis: 50_000
      response-timeout-millis: 50_000
      num-retries: 3
      retry-wait-millis: 5_000
      fixed-headers:
        Accept: application/json
        AnotherHeader: AnotherValue
      paths:
        getCatalog:
          path: /catalog
          scope: GET_CATALOG_SCOPE
        postCatalog:
          path: /catalog
          scope: POST_CATALOG_SCOPE
      proxy-config:
        proxy-host: http://localhost
        proxy-port: 1234
        non-proxy-hosts:
          - mockserver
          - camunda7
      token-config:
        token-url: http://localhost:1080/token
        basic-auth-username: user
        basic-auth-password: pass
        cache-name: client2-token-cache
        cache-expiry-seconds: 3600
        token-field: access_token
        form-data:
          username: user
          password: pass
          scope: openid
          grant_type: password
```
### Configure Beans
Applications must configure their own WebClient and TokenService beans through the exposed providers.

```java
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(OpenidClients.class)
public class OpenidAuthClientsConfig {

  private final OpenidWebClientProvider openidWebClientProvider;
  private final OpenidClients openidClients;

  @Bean
  public OpenidClientProperties defaultClientProperties() {
    return openidClients.getOpenid().get("default");
  }

  @Bean
  public WebClient defaultWebClient(
      @Qualifier("defaultClientProperties") OpenidClientProperties defaultClientProperties) {
    return openidWebClientProvider.buildWebClient(defaultClientProperties);
  }

  @Bean
  public OpenidTokenService defaultTokenService(
      @Qualifier("defaultClientProperties") OpenidClientProperties defaultClientProperties) {
    return openidWebClientProvider.buildTokenService(defaultClientProperties);
  }
}
``` 
And then you can use those beans via autowiring within your application.

```java
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SampleClientImpl() {

  private final WebClient defaultWebClient;
  private final OpenidTokenService defaultTokenService;
  private final OpenidClientProperties defaultClientProperties;
  ...
}
```
### B) Basic Auth WebClient

#### Maven Dependency
```xml
    <dependency>
      <groupId>com.pia.commons</groupId>
      <artifactId>pia-basic-webclient-provider</artifactId>
      <version>1.0.2</version>
    </dependency>
```

#### Sample Configuration (Minimal)
```yaml
pia.webclient:
  basic:
    client1:
      connection-provider-name: client1-provider
      token-config:
        username: user
        password: pass
```

#### Sample Configuration (Full)
```yaml
pia.webclient:
  basic:
    client2:
      connection-provider-name: client2-provider
      max-connections: 100
      request-timeout-millis: 50_000
      response-timeout-millis: 50_000
      num-retries: 3
      retry-wait-millis: 5_000
      fixed-headers:
        Accept: application/json
        AnotherHeader: AnotherValue
      proxy-config:
        proxy-host: http://localhost
        proxy-port: 1234
        non-proxy-hosts:
          - mockserver
          - camunda7
      token-config:
        username: user
        password: pass
        charset: UTF-8
```
### Configure Beans
Applications must configure their own WebClient and TokenService beans through the exposed providers.

```java
@Configuration
@RequiredArgsConstructor
public class BasicAuthClientsConfig {

  private final BasicWebClientProvider basicWebClientProvider;
  private final BasicAuthClients basicAuthClients;

  @Bean
  public BasicClientProperties basicClientProperties() {
    return basicAuthClients.getOpenid().get("client1");
  }
  
  @Bean
  public WebClient basicWebClient(
      @Qualifier("basicClientProperties") BasicClientProperties basicClientProperties) {
    return basicWebClientProvider.buildWebClient(basicClientProperties);
  }

  @Bean
  public BasicTokenService basicTokenService(
      @Qualifier("basicClientProperties") BasicClientProperties basicClientProperties) {
    return basicWebClientProvider.buildTokenService(basicClientProperties);
  }
}

``` 
And then you can use those beans via autowiring within your application.

```java
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SampleClientImpl() {

  private final WebClient basicWebClient;
  private final BasicTokenService basicTokenService;
  private final BasicClientProperties basicClientProperties;
  ...
}
```

## Version History
### 1.0.0
- Initial Version
### 1.0.1
- Fixes the BasicWebClientProviderAutoConiguration class name.
### 1.0.2
- moves getToken(scope) method to generic layer