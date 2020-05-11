
spring:
  application:
    name: gateway-application-demo2-registry

  cloud:
    # Nacos 作为注册中心的配置项
    nacos:
      config:
        namespace: e1dce615-2e24-4cfe-97e1-4829fb72e435
        server-addr: localhost:8848
        prefix: ${spring.application.name}
        file-extension: yaml
  profiles:
    active: dev
