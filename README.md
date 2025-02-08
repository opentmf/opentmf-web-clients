# pia-web-clients
General purpose WebClient libraries that includes Logbook, configurable connection properties, fixed headers support, and token retrieval with implicit access token caching.

Currently, two providers have been provided:

1. Openid Auth WebClient
2. Basic Auth WebClient

#### Mutual TLS support:
It supports Mutual TLS given you provide at least keyStore certificate. It will complain if both mTls and proxy configurations are provided. Truststore certificate is optional. Passwords for certificates are optional.

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
      certificates:
        key-store:
          password: mypassword
          pk-password: mypassword
          base64-jks: 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
        trust-store:
          password: mypassword
          pk-password: mypassword
          base64-jks: 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
```
#### First Method: Static Configuration
We will need to expose ClientProperties, WebClient and TokenService beans ourselves through configuration.

##### Maven Dependency
```xml
<dependency>
  <groupId>com.pia.commons</groupId>
  <artifactId>pia-openid-webclient-provider</artifactId>
</dependency>
```
##### Configure Beans
In this static configuration approach, applications must configure their own ClientProperties, WebClient and TokenService beans via OpenidWebClientProvider.

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
@RequiredArgsConstructor
public class SampleClientImpl() {

  private final OpenidClientProperties fullOpenIdClientProperties;
  private final WebClient fullOpenIdWebClient;
  private final OpenidTokenService fullOpenIdTokenService;
  // ...
}
```
#### Second (and Easy) Method: Dynamic Configuration
Starting with pia-web-clients version 1.0.5, applications can now directly use the client beans through pia-openid-webclients-starter autoconfiguration library which takes care of traversing the openid client configurations and exposing necessary beans automatically.

##### Maven Dependency
```xml
<dependency>
  <groupId>com.pia.commons</groupId>
  <artifactId>pia-openid-webclients-starter</artifactId>
</dependency>
```
That's it. Now you can autowire them at any point in your application:

```java
@DependsOn("openidWebClientsStarter")
@RequiredArgsConstructor
public class SampleClientImpl() {

  private final OpenidClientProperties fullOpenIdClientProperties;
  private final WebClient fullOpenIdWebClient;
  private final OpenidTokenService fullOpenIdTokenService;
  // ...
}
```
> **Note:** You have to depend on the marker `openIdWebClientsStarter` bean, so that the dynamically exposed webClient beans can be configured before your service. 

### B) Basic Auth WebClient

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
      certificates:
        key-store:
          password: mypassword
          pk-password: mypassword
          base64-jks: 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
        trust-store:
          password: mypassword
          pk-password: mypassword
          base64-jks: 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
```
#### First Method: Static Configuration
We will need to expose ClientProperties, WebClient and TokenService beans ourselves through configuration.

##### Maven Dependency
```xml
<dependency>
  <groupId>com.pia.commons</groupId>
  <artifactId>pia-basic-webclient-provider</artifactId>
</dependency>
```

##### Configure Beans
In this static configuration approach, applications must configure their own WebClient and TokenService beans through the exposed providers.

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
#### Second (and Easy) Method: Dynamic Configuration
Starting with pia-web-clients version 1.0.5, applications can now directly use the client beans through pia-basic-webclients-starter autoconfiguration library which takes care of traversing the basic auth client configurations and exposing necessary beans automatically.

##### Maven Dependency
```xml
<dependency>
  <groupId>com.pia.commons</groupId>
  <artifactId>pia-basic-webclients-starter</artifactId>
</dependency>
```
That's it. Now you can autowire them at any point in your application:
```java
import lombok.RequiredArgsConstructor;

@DependsOn("basicWebClientsStarter")
@RequiredArgsConstructor
public class SampleClientImpl() {

  private final BasicClientProperties simpleBasicClientProperties;
  private final WebClient simpleBasicWebClient;
  private final BasicTokenService simpleBasicTokenService;
  // ...
}
```
> **Note:** You have to depend on the marker `basicWebClientsStarter` bean, so that the dynamically exposed webClient beans can be configured before your service.

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
- Adds pia-basic-webclients-starter that dynamically exposes beans from configuration 
- Adds pia-openid-webclients-starter that dynamically exposes beans from configuration
### 1.0.6
- Started exposing marker beans for starter packages.
### 1.0.7
- Started exposing beans if they are not already exposed, to help test cases run in parallel.
### 1.0.8
- Stopped depending on spring-boot-starter-webflux, to support synchronous spring-web applications (i.e. web-application-type = servlet) with fewer dependencies and getting rid of potential auto configurations of webflux. That way we restrict the dependencies to provide a reactive WebClient, but not the whole reactive webflux server layer.
### 1.0.9
- Supports Mutual TLS protocol.
