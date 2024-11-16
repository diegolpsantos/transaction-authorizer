# Transaction Authorizer



A simple backend application that simulates a transaction authorization

  

## Installation

  

First of all, it's necessary having java and clojure installed. You can follow the guide of clojure to do the installation.

  

Follow the link: https://clojure.org/guides/install_clojure

  

## Usage

  

Run the project directly:

  

```

$ clojure -M -m transaction-authorizer.core

```

  

  

Run the project's tests:

  

```

$ clojure -T:build test

```

  

Run the project's CI pipeline and build an uberjar:

  

```

$ clojure -T:build ci

```

  

This will produce an updated `pom.xml` file with synchronized dependencies inside the `META-INF`

directory inside `target/classes` and the uberjar in `target`. You can update the version (and SCM tag)

information in generated `pom.xml` by updating `build.clj`.

  

If you don't want the `pom.xml` file in your project, you can remove it. The `ci` task will

still generate a minimal `pom.xml` as part of the `uber` task, unless you remove `version`

from `build.clj`.

  

Run that uberjar:

  

```

$ java -jar target/net.clojars.transaction-authorizer/core-0.1.0-SNAPSHOT.jar

```

  
  

## Examples

  

Example of input json payload:

```

{
	"id": "be70d753-d453-4cc3-82d9-3baa897498dd",
	"account-id": "123",
	"total-amount": 7000.0,
	"mcc": "5811",
	"merchant": "PADARIA DO ZE SAO PAULO BR"
}

```

  

Example of output:

```

{"code": "00"} // Success request
{"code": "05"} // Insuficient funds
{"code": "07"} // Any other problems that happen to the request and not authorizing the transaction 

```
Obs: All the responses will come with status code `200`
  

Example of usage:

  1 - Startup the server
```

$ clojure -M -m transaction-authorizer.core

```

2 - Enter the example of input in your preferred application (e.g curl, postman and etc)
3 - You could use the swagger to interact with the API
Route: http://localhost:8080/

  
  

## Docker Build

  

Before you build the Docker image, make sure Docker is available in your environment.

  

Execute the following command from the directory of the parent project:

  

```

docker build -t transaction-authorizer .

```

  

This should build a Docker image named `transaction-authorizer`.

  

## Docker Run

  
Run the newly created Docker image, `transaction-authorizer`, by executing the

[`docker run`](https://docs.docker.com/engine/reference/run/) command from the terminal:

  

```

docker run -i --rm --name transaction-authorizer transaction-authorizer

```

  

### Bugs
