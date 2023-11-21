package com.adekunle;

import com.adekunle.customer.Customer;
import com.adekunle.customer.CustomerRepository;
import com.adekunle.customer.Gender;
import com.adekunle.s3.S3Buckets;
import com.adekunle.s3.S3Service;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);

    }


//    @GetMapping("/greet")
//    public GreetResponse greet(@RequestParam(value = "name", required = false) String name) {
//        String greetMessage = name == null || name.isBlank() ? "hello" : "hello " + name;
//        return new GreetResponse(
//                greetMessage,
//                List.of("Java", "Python", "TypeScript"),
//                new Person("Adekunle", 32 , 30_000),
//                true
//        );
//    }
//
//    record Person(String name, int age, double savings) {}
//    record GreetResponse(
//            String greet,
//            List<String> favProgrammingLanguages,
//            Person person,
//            Boolean isHuman
//            ) {}

    //Creating a bean Manually
    // To make the class Foo available in our Application context. We add @Bean
    @Bean("foo")
    public Foo getFoo() {
        return new Foo("bar");
    }
    record Foo(String name) {}

    //We want to save customers into the database when the application starts
    // we use the command line runner and annotate with Bean
    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        Faker faker = new Faker();
        return args -> {

            //testing customer creation
           // faker.internet().emailAddress();
            String firstname = faker.name().firstName();
            String emailAddress = firstname + "@gmail.com";
            Integer age  = faker.random().nextInt(20,100);
            Customer customer = new Customer(
                    firstname,
                    emailAddress,
                    age,
                    Gender.MALE,
                    passwordEncoder.encode(UUID.randomUUID().toString()));
            customerRepository.save(customer);

            // Test Bucket Upload and Download
            // Don't forget to put S3Service s3Service, S3Buckets s3Buckets in the runner arguemen
//            s3Service.putObject(s3Buckets.getCustomer(), "foo",
//                    "Hello World".getBytes());
//
//            byte [] obj = s3Service.getObject(s3Buckets.getCustomer(), "foo");
//
//            System.out.println("Hooray: " + new String(obj));


            // without using faker
//            Customer alex = new Customer("Alex","alex@gmail.com",21);
//            Customer jamila = new Customer("jamila","jamila@gmail.com",19);
//
//            List<Customer> customers = List.of(alex,jamila);
//            customerRepository.saveAll(customers);

        };
    }

}
