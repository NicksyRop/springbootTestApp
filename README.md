
Unit tests - small method that you write to test some part of a code
it invokes the method you are testing with specific input parameters

Junit - provides us with an API to write code snipets to test java methods


Integration Tests - Application code is tested
without mocking database or
HTTP connections (slowe since they need to communiucate with external systems)

Unit tests - Testing isolated small
pieces of code with Fake or
Mock dependencies


Why write unit tests
1. make sure our code works
2. code works with valid and invalid  input params
3. code works now and in future - so incase of enhancements we dont break anything
4. Other working code still works even after changes (regression)

F.I.R.S.T
Fast since they are small i.e test just a small functinality - they do not communiucate over the network
, and do not perfom db opertion
Independent - one unit test should not depend on the output of another unit test
Repeatable - if one unit test is run should produce the same results
Self validating - unit test validates itsef ( no manual checks involved)
Thouough - cover edges - consider happy path as well as negative scenario

Testing code in Isolation
We can have a test for a method A , that depends on a different method say B in a different class
method A tests can fail because of B , hence we need to tets method A in isolation  without dependency to method B

Dependency Injection
is a programming technique in which an object or function receives other objects or functions that it requires,
as opposed to creating them internally (ready to use objects)

We will then inject a ready to use method B into A instead of creating a new one (same to objects )
Thia thus then isolate code in method  B / obeject B from A


Junit 5
Testing framework that allows us to write tests in java

Naming convention
test<method_under_test>_<condition or state_change>_<Expected_result>

i.e testIntegerDivision_WhenDividendIsDividedByZero_ShouldThrowArithmeticExceptio();

it is a good practice to have dispalay name describing what test class does / even the methods
i.e @DisplayName("Test math operations in Calculator class.")


// sample structure  
AAA ---Arange Act Assert-----
@Test
void testIntegerDivision_whenValidValuesProvided_shouldReturnExpectedResult() {
}
//Arrange - prepare all the needed variables and objects that are needed by system under test
Calculator calculator = new Calculator();

//Act -- invoke the method under test
int result = calculator.integerDivision(4,2);

//Assert -- validate return value from method under test
assertEquals(2, result, "4/2 should have returned 2");


We can have unit tests for
1. All parameters are valid
2.  Invalid Response from HTTP Client
3. One parameter has invalid value

life cycle
Annotations
@BeforeAll - static mthod.execute this method before any test cases run , i.e create a database
@AfterAll - method executes after all test cases run i.e for clean up i.e delete db
@BeforeEach - method will execute before each test case i.e if all the method need to create same object
then this logic moves here to prevent repetition
@AfterEach - executes after each unit tes i.e use it for clean up close db connection

To disable unit tets comment the @Test annotation  or use @Disable annotation


Test for Exceptions
use assertThrows(class,executable) -  expects an class and an executable which is a lambda function

@Test - run only once
@ParameterizedTest - run multiple times depending on the set of arguments
@RepeatedTest(6) - test if we repeat same mthod multiple times will still pass


@TestMethodOrder(MethodOrderer.Random.class) // allow us to specify order using the
MethodOrderer interface can accept DisplayName ,methodName , orderAnnotation or Random class

@TestMethodOrder(MethodOrderer.OrderAnnotation.class) will be used at class level and use
@Order(1) to order execution time

If we want to order classes execution we just pass the Order(1) at the class level
Junit allows configiration for the entire project globally
Go to resources folder of Test package , crete a resourceBundle fine name it like junit-platform
then add below line


junit.jupiter.testclass.order.default=org.junit.jupiter.api.ClassOrderer.OrderAnnotation  , ClassName  or
junit.jupiter.testclass.order.default=org.junit.jupiter.api.ClassOrderer.Random if you want them random


we can also configure method execution globally same way

junit.jupiter.testmethod.order.default=.order.default=org.junit.jupiter.api.MethodOrderer.Random

This will execute tests as they are ordered in the classes

TDD - Test Driven Development

Lifecycle
Red - Unit test with no Application code , automatically fails -- also write minimum code to make sure test fails
Green - Write Application code to make unit test pass
Refactor - clean up unit test , Application code to make it orgarnised
Repeate - repeat the above steps until functinality is implemented


Mockito - open source test framework created for java , allows us to easily create 'test double' objects in
unit tests

Test double - is an object that can stand in for a real object  in a test
various Test Doubles incude
- Stub
- Mock
- Fake
- Spy

    we use them to temporarily replace a real world object i.e we do not want test to insert test data to a database 
so we use mockito to create a mock for mysql data access

i.e we have  UserRepository userRepository = new UserRepositoryImpl();  
that stores user on memory
when runing test and we call the createUser the test data will also be stored , and we dont want this
and this wont be a unit test but intergration test
and we are not testing code under the save method of the userRepository , hence we need to mock


what is the purpose of doCallRealMethod().when(emailVerificationService).scheduleEmailConfirmation(any(User.class));?

doCallRealMethod().when() is used to stub a method to call its real implementation when it's invoked. In this case,
it's making the scheduleEmailConfirmation() method of the emailVerificationService execute its actual code when it's called with any User object. This is useful when testing
interactions with partially-mocked objects where some methods are mocked while others retain their original behavior.

Code Coverage
For test report -
Go to maven repo and add Maven Surefire Report Plugin to the plugin section
this will generate test report if all unit test pass

if you want to geenerate test report even if test fails add one more configiration to
the plugin

    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-report-plugin</artifactId>
        <version>3.5.2</version>
         <configuration>
                    <testFailureIgnore>true</testFailureIgnore>
                </configuration>
        <executions>
            <execution>
                <phase>test</phase>
                <goals>
                    <goal>report</goal>
                </goals>
            </execution>
        </executions>
    </plugin>


run mvn clean test on the root dir     
the report will be in the target directory , reports folder

- for code coverage
  search for jacoco  -(java code coverage) plugin in maven repo

and paste it like below
<plugin>
<groupId>org.jacoco</groupId>
<artifactId>jacoco-maven-plugin</artifactId>
<version>0.8.12</version>
<executions>
<execution>
<id>prepare-agent</id>
<goals>
<goal>
prepare-agent
</goal>
</goals>
</execution>
<execution>
<id>report</id>
<phase>test</phase>
<goals>
<goal>report</goal>
</goals>
</execution>
</executions>
</plugin>

then run mvn clean test
go to target folder , site then jacoco index.html

Spring boot testing
- we use @SpringBootTest annotation - creates Application context that is very similar to the one
  in production , tho it will not start a webserver

To be able to test Spring we need to have
spring-boot-starter-test and spring-security-test dependencies which gets added by
security and web dependencies


Test Rest controller

annotatate the restcontroller test class with @WebMvcTest - to tell SpringBoot to create
Application context for only beans that are related to application web layer  
Depending on the annotation we have used.
- hence spring will scan classes that are related to web layer only i.e controller bean but not data layer beans
  making our class run faster than full intergration test
- to limit the controllers we are interested i.e @WebMvcTest(controllers = UsersController.class)
  now web mvc is only limited to UsersController class and it is the only class that gets loaded to application context
- Otherwise if we do not specify all the controller classes will be loaded to application context.
- If the application has spring security , we have security filters in place but we only testing web layer
  hence we need to exclude them @AutoConfigureMockMvc(addFilters = false) or  just add
  @WebMvcTest(controllers = UsersController.class , excludeAutoConfiguration = SecurityAutoConfiguration.class)


We are not going to send actual http request to test controller
Spring provides a class  to configure http request called MockMvcRequestBuilders

@MockBean annotation - create a new object for the object that implements the interface i.e userService
and the class that implements it is the UsersServiceImpl class

you can do it on class level to mock all the objects you want i.e then autowire the service

i.e @MockBean({UsersServiceImpl.class})

then @Autowired        
UsersService usersService;

     @MockBean - will create mock objects and automatically put it to spring application context 
     @Mock - will not automatically put mock objects into application context

nb @SpringBootTest loads the entire application context,
making it suitable for more comprehensive integration tests

     @WebMvcTest is designed for testing a specific slice of the application, 
     usually the web layer, creating a narrow context for efficient testing.

- @MockBean annotation, when used with @WebMvcTest, creates mock implementations for
  specific beans in the ApplicationContext. This approach is useful in testing scenarios
  where you need to isolate the controller's behavior from its dependencies, making the tests more focused and controlled.

  @SpringBootTest annotation is designed to load the complete application context for integration testing.
  It allows you to test the entire application in a "realistic" environment that closely resembles the production environment.
  This is useful when you want to test how different components of your application interact with each other, and
  with external services like databases or APIs. By using the @SpringBootTest annotation, you can test your application as a whole,
  which can help you identify issues that may not be apparent when testing individual components.

Intergration Tests - tesing all the layers (web,service and data)    
Here we do not mock or fake any object behavior

When we pass the environemt to @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
spring will create web application context with mocked servlet environment - meaning you wont have
entire app context loaded only web layer beans
This will also not start a real web embeded server container -  hence need to use mockmvc to test

Various test environments
- @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = {"server.port=9090"})  - then go to application props
  and add port number or add a seperate port number for test

when we have many different test configs the best way is to use the
@TestPropertySource("/application-test.properties) annotation  and add all the test configs

it is good to use webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT - so we do not have port conflicts

To know the port number we use @LocalServerPort annotation to instead of @Value annotation
i.e @LocalServerPort
private int localPort;


- nb ( For ordered methods that will share member variables we need to provide test instance at class
  level so we do not instantiate new objects for each method )
  i.e @TestInstance(TestInstance.Lifecycle.PER_CLASS) by default jnit creates new instace per method i.e PER_METHOD

Testing Data layer - Entity and Reposiory

To do data layer intergration test we use @DataJpaTest
what it does
- disable auto configuration and load only JPA related components to application context i.e User entity and userRepository
- make test methods transactional , once test are complete all the changes made to db  will rollback
- use in memory db by default


To persist an object to the db we need test entity manager obeject (hence we autowire)
i.e

    @Autowired
    private TestEntityManager entityManager;

    then 

    testEntityManager.persistAndFlush(userEntity); 


When you call persistAndFlush(entity) using the TestEntityManager in a test context, it does the following:

persist(entity):
Adds the entity to the persistence context (first-level cache) and marks it for insertion into the database.
However, at this point, the actual SQL INSERT statement may not yet be executed.
It's deferred until the persistence context is flushed (either automatically or manually).

flush():
Forces the persistence context to flush all pending changes (e.g., the INSERT statement for the entity)
to the database immediately. This ensures that the database state is updated and consistent with the changes made in the persistence context.


Calling flush ensures that the database has the updated state immediately, which is important when:

-Verifying database constraints like uniqueness or not-null constraints.
-Testing queries to ensure that they return the expected results after changes.
-Avoiding lazy loading or delayed execution of SQL statements during the test

nb
transactional behavior provided by @DataJpaTest automatically rolls back any database changes made
during testing after the test completes. This ensures that the tests are isolated and do not affect
the actual data in the database, maintaining data integrity and allowing for consistent and repeatable testing.

Testing JPA repositories
Here we only test our own methods and not the once provided by JPA (save , deleteBy , deleteAll etc ) - Trust them since they are provided by framework
We have a custom query method in the userRepository  - this is what we are going to test

UserEntity findByEmail(String email);

Most devs prefer to test JPQL (Jakarta Persistence Query Language )
it is is good to test all of them to see if they return the expected response ( do not just assume )

Test containers
is a java library that helps us run docker containers for application's dependencies i.e db directly  
from our test code. This will allow us use tools that are as close as production so we don't have problems on production
i.e if we use h2 to test and our prod db use postgres, some tests will fail on prod since test were done on H2

Hence we can start postgres container directly from our test code
Spring framework automatically configures the app to use this test containers

Go to https://testcontainers.com/ to see available containers then go to modules link
filter by languege and category i.e java/relational db

Go to spring initializer and add testcontainer dependency,
spring initializer will automaticlly add test containsers  for all your dependencies like mysql ,rabbitMq etc

we add @Testcontainer annotation to our test class
what it does
- enables test containers
- Tells test framework this class will use test containers
- Tell test container manage lifecycle of containers i.e start mysql before test methods run

Mysql will expose a random port with default username and password of 'test'
we can specify our own creds like below

    @Container  //start container before test class executed
    private static MySQLContainer mySQLContainer = new MySQLContainer("mysql:latest")
            .withDatabaseName("photo_app")
            .withUsername("root")
            .withPassword("rootpassword"); 

Application will try to connect to mysql at port 3306 , we will need to override this at runtime
We will need to use @DynamicPropertySource to allow us override env values
You can also override other values same way

    @DynamicPropertySource
    private static void overrideProperties(DynamicPropertyRegistry registry){
        //replace the value from props with the value from test container
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
    }

we can also autoconfigure this using the @ServiceCnnection annotation and remove the  manual props

and spring will configure connection for us

withDatabaseName("photo_app")
.withUsername("root")
.withPassword("rootpassword");


            and remove the @DynamicPropertySource 


You may end up getting below error
Caused by: java.lang.IllegalStateException: Mapped port can only be obtained after the container is started
This means springboot is trying to connect to mysql server before container is started
we will need to add static block to make sure container starts before test after the container configiration

static {
mySQLContainer.start();
}


Postgres Test containers

POM
<dependency>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-testcontainers</artifactId>
<scope>test</scope>
</dependency>
<dependency>
<groupId>org.testcontainers</groupId>
<artifactId>junit-jupiter</artifactId>
<scope>test</scope>
</dependency>
<dependency>
<groupId>org.testcontainers</groupId>
<artifactId>postgresql</artifactId>
<scope>test</scope>
</dependency>

CONNECTION
@Container
@ServiceConnection
private static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest");


APACHE KAFKA

<dependency>
   <groupId>org.springframework.kafka</groupId>
   <artifactId>spring-kafka-test</artifactId>
   <scope>test</scope>
</dependency>
<dependency>
   <groupId>org.testcontainers</groupId>
   <artifactId>junit-jupiter</artifactId>
   <scope>test</scope>
</dependency>
<dependency>
   <groupId>org.testcontainers</groupId>
   <artifactId>kafka</artifactId>
   <scope>test</scope>
</dependency>

CONNECTION

@Container
@ServiceConnection
private static KafkaContainer kafkaContainer = new KafkaContainer(
DockerImageName.parse("confluentinc/cp-kafka:latest"));

@Test
@DisplayName("The test container is created and is running")
void testContainerIsRunning() {
assertTrue(kafkaContainer.isCreated(), "kafka Container has not been created");
assertTrue(kafkaContainer.isRunning(), "kafka Container is not running");
}