# Kakai >_

### Beta Release v1.03

Kakai standalone runnable server contains everything you need to create a full stack java application. Kakai handles dependency injection, http routing, data persistence and page data binding much like the jsp spec.

#### Quick Start
Kakai can be bootstrapped in a matter of seconds. A quick walk through using gradle.

#### build.gradle

```
plugins {
    id 'java'
    id 'application'
}

group 'foo'
version '1.0'

application{
    mainClass.set("foo.Main")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation "io.kakai:kakai:1.03"
}
```

     
A main entry point for our application.

#### Main.java

```    
import io.kakai.Kakai;
import io.kakai.annotate.Application;
import io.kakai.resources.Environments;

@Application(Environments.DEVELOPMENT)
public class Main {
	public static void main(String[] args){
		new Kakai(8080).start();
	}
}
```
        
A Router is synonymous with Controller in the MVC world. A router can handle many endpoints. Example:

#### HelloRouter.java

```        
import io.kakai.annotate.http.Get
import io.kakai.annotate.Text    
import io.kakai.annotate.Router;

@Router
public class HelloRouter {

     @Text
     @Get("/")
     public String hello(){
        return "hello world.";
     }

}
```       
 
Then its as simple as running the following command.

#### Run it!
            
    $ gradle run
        
#### Browse:
            
    $ http://localhost:8080/
     
        
#### Empower.
The engine is built on the HttpServer by Sun. Thank you! Thank you for making Java complete with everything you could ever wish for in a programming language, object orientation, concurrency, meta programming with annotations, reflection, and its free. Earth's greatest philanthropists.

Thank you OpenJDK for continuing to make it free! You guys are the greatest!

Thank you to IntelliJ for making such a great IDE. I strongly recommend!

Thank you Maven Central for hosting my artifact! I can't wait for the new site to be released.

Thank you GitHub where this site and project is hosted! Ive successfully developed many projects on GitHub. Now they provide site support as well. Fantastic.

#### People.

It may be ridiculous to say thank you, but because of all of these generous people I have been able to enjoy the art of programming and paint my technical ideas. My computer, the easel, the IDE a canvas and java a paint brush. I am so very grateful for the Free & Community Driven World!

Now people in Baltimore, Inglewood, Compton, Oakland, Memphis, the Brooklyn, Yonkers, Sun Valley Trailer Park Nevada, Mexico, Philippines, India... or any other struggling nation or people have an opportunity to ideate, dream & build. Thank you!

For support contact croteau.mike@gmail.com


### Sample Projects

Example projects can be found in the `examples/` directory.
To run the `polygon-crm` example project:

    $ ./gradlew examples:polygon-crm:run
    
Then browse:

    $ http://localhost:8080/

