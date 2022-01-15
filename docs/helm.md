# VAuthenticator Helm

In this section I will go to explain how customize your value.yml file. The blueprint is something like below

```yaml

in-namespace:
  redis:
    enabled: true

replicaCount: 1

image:
  repository: mrflick72/vauthenticator-k8s
  pullPolicy: Always
  tag: "latest"

lables: {}

selectorLabels:
  app: vauthenticator

podAnnotations: {}

aws:
  region: xxxxxxxxx
  iamUser:
    accessKey: xxxxxxxxx
    secretKey: xxxxxxxxx
    enabled: false
  eks:
    serviceAccount:
      enabled: false
      iamRole:
        arn: arn:aws:iam::ACCOUNT_ID:role/IAM_ROLE_NAME

logging:
  enabled: false
  kibana:
    host: kibana.host:5601
  elasticSearch:
    host: elasticsearch.host:9200

application:
  redis:
    database: 0
    host: vauthenticator-redis-master.auth.svc.cluster.local

  server:
    port: 8080
    servlet:
      contextPath: /vauthenticator

  host: http://application-example-host.com

  dynamoDb:
    account:
      tableName: your_VAuthenticator_Account_table_name
      role:
        tableName: your_VAuthenticator_Account_Role_table_name
    role:
      tableName: your_VAuthenticator_Role_table_name
    clientApplication:
      tableName: your_VAuthenticator_ClientApplication_table_name
    keys:
      tableName: your_VAuthenticator_Keys_table_name

  accountSync:
    listener:
      sleeping: 1m
      queueUrl: your_vauthenticator-account_updates_queue_name
      maxNumberOfMessages: 5
      visibilityTimeout: 20
      waitTimeSeconds: 20

imagePullSecrets: []
nameOverride: ""
fullnameOverride: ""

pod:
  probes:
    liveness:
      initialDelaySeconds: 10
      periodSeconds: 30
    rediness:
      initialDelaySeconds: 10
      periodSeconds: 30

service:
  type: ClusterIP

ingress:
  enabled: true
  class: nginx

istio:
  enabled: false
  gateway:
    prefix: /vauthenticator
    port: 80
    host: application-example-host.com

resources:
  requests:
    cpu: "256m"
    memory: "256Mi"
  limits:
    cpu: "512m"
    memory: "512Mi"

redis:
  auth:
    enabled: false
  replica:
    replicaCount: 1
```

by default the helm chart will provide no support for distributed logging on kibana, and istio. In order to enable those feature it is required to enable explicitly via ``` istio.enabled: ture``` for istio and ```logging.enabled: true``` for logging, 
of course if istio is enabled should be better to disable ingress configuration with ingress.enabled: false. 
This helm comes with the possibility to configure as dependency a redis bitnami helm in the same vauthenticator namespace via ```in-namespace.redis.enabled: true```, pay attention that it is the default behaviour.