# Go Continuous Delivery Task API plugin 

> This is a wrapper over the Go CD newest JSON plugin API hiding all the communication details, letting you to 
focus only on writing the task logic 

[![Build Status](https://travis-ci.org/jmnarloch/gocd-task-plugin-api.svg)](https://travis-ci.org/jmnarloch/gocd-task-plugin-api)

## Setup

Add the dependency to your project.

```
<dependency>
  <groupId>io.jmnarloch</groupId>
  <artifactId>go-task-plugin-api</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Usage

Create a class that will extend the `AbstractDispatchingTask` and provide the `ApiRequestDispatcher`.

```java
@Extension
public class GradleTask extends AbstractDispatchingTask {

    @Override
    protected ApiRequestDispatcher buildDispatcher() {
        return ApiRequestDispatcherBuilder.dispatch()
                .toConfiguration(new AnnotatedEnumConfigurationProvider<>(GradleTaskConfig.class))
                .toValidator(new GradleTaskValidator())
                .toView(new GradleTaskView())
                .toExecutor(new GradleTaskExecutor())
                .build();
    }
}
```

## License

Apache 2.0