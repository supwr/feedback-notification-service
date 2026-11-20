# notification-service

Serviço Quarkus preparado para AWS Lambda/SQS e envio de notificações por email via SES.

## Arquitetura

O código segue Clean Architecture: `domain` não depende de AWS, `application` contém o evento, o use case e strategies, e `infrastructure` adapta SQS, SES e S3.

- A fila padrão é `notification-request` (`NOTIFICATION_QUEUE_NAME`).
- O campo `type` aceita `EMAIL`; tipos futuros podem ser adicionados por novas strategies.
- Anexos são informados por bucket/key no evento e obtidos internamente no S3 antes da montagem MIME.
- Não são gerados nem compartilhados pre-signed URLs.

Exemplo de anexo:

```json
{
  "bucket": "notifications",
  "key": "feedback/report.pdf",
  "fileName": "report.pdf",
  "contentType": "application/pdf"
}
```

As aliases `bucketName` e `objectKey` também são aceitas.

## Configuração AWS

- `AWS_REGION`: região dos clientes (padrão `us-east-2`).
- `NOTIFICATION_QUEUE_NAME`: nome da fila (padrão `notification-request`).
- `NOTIFICATION_EMAIL_SENDER`: endereço verificado no SES.

O handler configurado é `com.notifier.infrastructure.messaging.SqsNotificationHandler`.

## Lambda

O Lambda recebe `SQSEvent` na camada de infraestrutura, converte a mensagem em `NotificationEvent` e executa o use case. Exceções devolvem a mensagem ao retry da fila.

## Deploy via GitHub Actions/ECR

O workflow `.github/workflows/deploy-ecr.yml` publica uma imagem nativa no ECR e atualiza uma função Lambda.

Configure estas variáveis no repositório GitHub:

- `AWS_REGION`
- `AWS_ROLE_TO_ASSUME`
- `ECR_REPOSITORY`
- `LAMBDA_FUNCTION_NAME`

O workflow executa os testes, cria o executável nativo com Maven em container, publica as tags do commit e `latest` e aguarda a conclusão da atualização da função Lambda.


This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/worker-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Related Guides

- Amazon SES ([guide](https://docs.quarkiverse.io/quarkus-amazon-services/dev/amazon-ses.html)): Connect to Amazon SES
- Amazon SQS ([guide](https://docs.quarkiverse.io/quarkus-amazon-services/dev/amazon-sqs.html)): Connect to Amazon SQS messaging queue service
