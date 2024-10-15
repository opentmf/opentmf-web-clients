 # pia-openid-webclients-starter
 This autoconfiguration library uses the client name as the prefix and dynamically exposes the following three beans for all the configured openid clients:

- ${prefix} + "ClientProperties"
- ${prefix} + "WebClient"
- ${prefix} + "TokenService"

For example, it will expose the following beans:

- firstOpenIdClientProperties (OpenidClientProperties)
- firstOpenIdWebClient (WebClient)
- firstOpenIdTokenService (OpenidTokenService)
- secondOpenIdClientProperties (OpenidClientProperties)
- secondOpenIdWebClient (WebClient)
- secondOpenIdTokenService (OpenidTokenService)

For the following configuration:
```yaml
pia:
  webclient:
    openid:
      firstOpenId:
        connection-provider-name: firstOpenId
        fixed-headers:
          header1: value1
        token-config:
          use-mock: false
          token-url: http://localhost:1080/token
          basicAuthUsername: user
          basicAuthPassword: pass
          cache-expiry-seconds: 1
          username-field: username
          form-data:
            username: user
            password: pass
            scope: openid
            grant_type: password
      secondOpenId:
        connection-provider-name: secondOpenId
        fixed-headers:
          header1: value1
          header2: value2
        token-config:
          use-mock: false
          token-url: http://localhost:1080/token
          basicAuthUsername: user
          basicAuthPassword: pass
          cache-expiry-seconds: 1
          username-field: username
          form-data:
            username: user
            password: pass
            scope: openid
            grant_type: password
```