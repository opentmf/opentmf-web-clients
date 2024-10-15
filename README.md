# pia-web-clients
General purpose WebClient libraries that includes Logbook, configurable connection properties, fixed headers support, and token retrieval with implicit access token caching.

Currently, two providers have been provided:

1. Openid Auth WebClient
2. Basic Auth WebClient

## Usage

### Import pia-commons dependency versions
This will manage the dependencies of the pia-commons libraries
to use their latest compatible version.
```xml
<dependencyManagement>
  <dependency>
    <groupId>com.pia.commons</groupId>
    <artifactId>pia-commons-versions</artifactId>
    <version>RELEASE</version>
    <type>pom</type>
    <scope>import</scope>
  </dependency>
</dependencyManagement>
```

### A) Openid Auth WebClient

#### Maven Dependency
```xml
<dependency>
  <groupId>com.pia.commons</groupId>
  <artifactId>pia-openid-webclient-provider</artifactId>
</dependency>
```

#### Sample Configuration (Minimal)
```yaml
pia.webclient:
  openid:
    simpleOpenId:
      connection-provider-name: simpleOpenId
      token-config:
        token-url: http://localhost:1080/token
        cache-expiry-seconds: 3600
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
    fullOpenId:
      connection-provider-name: fullOpenId
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
        cache-expiry-seconds: 3600
        token-field: access_token
        username-field: username
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
  public OpenidClientProperties fullOpenIdClientProperties() {
    return openidClients.getOpenid().get("fullOpenId");
  }

  @Bean
  public WebClient fullOpenIdWebClient(
      @Qualifier("fullOpenIdClientProperties") OpenidClientProperties fullOpenIdClientProperties) {
    return openidWebClientProvider.buildWebClient(fullOpenIdClientProperties);
  }

  @Bean
  public OpenidTokenService fullOpenIdTokenService(
      @Qualifier("fullOpenIdClientProperties") OpenidClientProperties fullOpenIdClientProperties) {
    return openidWebClientProvider.buildTokenService(fullOpenIdClientProperties);
  }
}
``` 
And then you can use those beans via autowiring within your application.

```java
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SampleClientImpl() {

  private final OpenidClientProperties fullOpenIdClientProperties;
  private final WebClient fullOpenIdWebClient;
  private final OpenidTokenService fullOpenIdTokenService;
  // ...
}
```
### B) Basic Auth WebClient

#### Maven Dependency
```xml
<dependency>
  <groupId>com.pia.commons</groupId>
  <artifactId>pia-basic-webclient-provider</artifactId>
</dependency>
```

#### Sample Configuration (Minimal)
```yaml
pia.webclient:
  basic:
    simpleBasic:
      connection-provider-name: simpleBasic
      token-config:
        username: user
        password: pass
```

#### Sample Configuration (Full)
```yaml
pia.webclient:
  basic:
    fullBasic:
      connection-provider-name: fullBasic
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
  public BasicClientProperties simpleBasicClientProperties() {
    return basicAuthClients.getOpenid().get("simpleBasic");
  }
  
  @Bean
  public WebClient simpleBasicWebClient(
      @Qualifier("simpleBasicClientProperties") BasicClientProperties simpleBasicClientProperties) {
    return basicWebClientProvider.buildWebClient(simpleBasicClientProperties);
  }

  @Bean
  public BasicTokenService simpleBasicTokenService(
      @Qualifier("simpleBasicClientProperties") BasicClientProperties simpleBasicClientProperties) {
    return basicWebClientProvider.buildTokenService(simpleBasicClientProperties);
  }
}

``` 
And then you can use those beans via autowiring within your application.

```java
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SampleClientImpl() {

  private final BasicClientProperties simpleBasicClientProperties;
  private final WebClient simpleBasicWebClient;
  private final BasicTokenService simpleBasicTokenService;
  // ...
}
```

## Version History
### 1.0.0
- Initial Version
### 1.0.1
- Fixes the BasicWebClientProviderAutoConiguration class name.
### 1.0.2
- moves getToken(scope) method to generic layer
### 1.0.3
- Marks PiaWebClientException Serializable
### 1.0.4
- Added new configuration property "usernameField" to openidTokenProperties.
### 1.0.5
- Removes configuration property "cacheName" from OpenidTokenProperties
- Fixes providers' local caching issue if multiple connections are configured
