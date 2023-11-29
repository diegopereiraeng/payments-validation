# scanPay

How to Kick off a Harness Pipeline

edit the file [PaymentValidation.java](https://github.com/diegopereiraeng/payments-validation/edit/master/src/main/java/io/harness/payments/api/PaymentValidation.java)

go to the line: 126
```
max = 4000;
min = 3900;
```
increase the max variable to 5000
like this:
```
max = 5000;
min = 3900;
```
then propose a PR

How to start the scanPay application
---

1. Run `mvn clean install` to build your application
1. Start application with `java -jar target/scanPay-1.0-SNAPSHOT.jar server config.yml`
1. To check that your application is running enter url `http://localhost:8080`

Health Check
---

To see your applications health enter url `http://localhost:8081/healthcheck`

