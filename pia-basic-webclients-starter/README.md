 # pia-basic-webclients-starter
 This autoconfiguration library uses the client name as the prefix and dynamically exposes the following three beans for all the configured openid clients:

- ${prefix} + "ClientProperties"
- ${prefix} + "WebClient"
- ${prefix} + "TokenService"

For example, it will expose the following beans:

- firstBasicClientProperties (BasicClientProperties)
- firstBasicWebClient (WebClient)
- firstBasicTokenService (BasicTokenService)
- secondBasicClientProperties (BasicClientProperties)
- secondBasicWebClient (WebClient)
- secondBasicTokenService (BasicTokenService)

For the following configuration:
```yaml
pia:
  webclient:
    basic:
      firstBasic:
        connection-provider-name: firstBasic
        fixed-headers:
          header1: value1
        token-config:
          username: user1
          password: pass1
      secondBasic:
        connection-provider-name: secondBasic
        fixed-headers:
          header1: value1
          header2: value2
        token-config:
          username: user2
          password: pass2
```