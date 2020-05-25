# fenxlib-tuktukfx
Fenxlib extension to support the TukTukFx task-running framework
## Table of Contents
- [Installation](#installation)
- [Features](#features)
- [Features](#example)
- [License](#licensing)
## Installation
### Built With
* OpenJDK8
* OpenJFX8
* fenxlib
* [TestFX](https://github.com/TestFX/TestFX)
* [JUnit](https://junit.org/junit4/)
* [Hamcrest](http://hamcrest.org/JavaHamcrest/)
### Prerequisites
* A Java8 JDK with JavaFX (Amazon Corretto, ZuluFX, etc)
 
### Clone and install into mavenLocal
```shell
git clone https://github.com/Legyver/fenxlib-tuktukfx.git
cd fenxlib-tuktukfx/
gradlew install
```
The last command installs the library into mavenLocal

### Importing fenxlib-tuktukfx
 ```build.gradle
repositories {
    mavenLocal()
}
dependencies {
    compile group: 'com.legyver', name: 'fenxlib-tuktukfx', version: '1.0.0.0'
}
```

### Running Tests
```shell
gradlew test
```

## Features
### TukTukFxApplicationOptions
ApplicationOptions that allows for exiting the app to
 * Set a flag to notify any tasks that the app is shutting down
 * Shutdown the thread-pool

## Example
### ApplicationOptions
Base fenxlib hook for
 * Where to load your settings from (Here, "${user.home}/.appDir")
 * Conversion to for the Map to your config
 * JavaFX primary stage
 * UIModel 
 
```java
public class ApplicationOptions extends TukTukFxApplicationOptions<ApplicationUIModel> {
    public ApplicationOptions(Stage primaryStage) throws IOException {
        super(".appDir", map -> new AppConfig(map), primaryStage, new ApplicationUIModel());
    }
}
```

### BindingFactory
Binding factory for registering UI Components extends the TaskBindingFactory to leverage the abort functionality
```java
public class BindingFactory extends TaskBindingFactory {
    public BindingFactory(ApplicationOptions applicationOptions) {
    	super(applicationOptions);
    }
}
```

### ExampleAbortableTask
Extends AbortableTask so the framework can notify it on cancel.  This extends javafx.concurrent.Task and ultimately Runnable so it can be submitted to the thread-pool
```java
public class ExampleAbortableTask<T> extends AbortableTask<T> { 
    public NamedAbortableTask(ITask<T> exampleTaskWorker) { 
        super(wrappedTask);
    }
}
```

### ExampleTaskWorker
The wrappedClass that contains the executable code.
The responsibility of this class is to allow the TaskProcessor to leverage the ProtoTaskFlow of the TukTukFX framework.
The TaskProcessor here is the thing that will actually do the work. 
```java
public class ExampleTaskWorker extends AbstractObservableTask<Void, ExampleTaskArgs> { 

    @Override
    public Void execute(TaskStatusAdapter taskStatusAdapter) throws CoreException {
        process(taskStatusAdapter, 100);//100 just an example domain value
        return null;
    }
   
    @Override
    public TaskProcessor getTaskProcessor() {
        return new ExampleTaskProcessor();
    }
    
    @Override
    public ExampleTaskArgs getTaskProcessorArgs() {
        return new ExampleTaskArgs();
    }
}
```

### ExampleTaskProcessor
The work to be done in the task.  The example below has a long-running loop that shuts down if the application exits.
```java
public class ExampleTaskProcessor implements TaskProcessor<ExampleTaskArgs> { 
    @Override
    public void process(TaskStatusAdapter taskStatusAdapter, ExampleTaskArgs exampleTaskArgs) throws CoreException {
        AbortableTask abortableTask = (AbortableTask) taskStatusAdapter;
        while (!abortableTask.isCancelled()) {
        	//do something
        }
    }
}
```

### ExampleApplication
```java
public class ExampleApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        ApplicationOptions applicationOptions = new ApplicationOptions(primaryStage);
        GuiUtil.init(applicationOptions);
        BindingFactory bindingFactory = new BindingFactory(applicationOptions);

        ExampleAbortableTask exampleAbortableTask = new ExampleAbortableTask(new ExampleTaskWorker());
        bindingFactory.taskAbortObservesShutdown(exampleAbortableTask);
        TaskExecutor.INSTANCE.submitTask(exampleAbortableTask);
    }
}
```

## Licensing
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/Legyver/fenxlib-tuktukfx/blob/master/LICENSE)
