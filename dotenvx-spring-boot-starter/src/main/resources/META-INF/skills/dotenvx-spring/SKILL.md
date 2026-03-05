---
name: dotenvx-spring
description: Dotenvx with Spring Boot - easy to load encrypted application.properties, application.yaml in Spring Boot
project
---

## dotenvx installation

If you use IntelliJ IDEA, please download [Dotenvx JetBrains Plugin](https://plugins.jetbrains.com/plugin/28148-dotenvx)
first, and all operations could be finished in IDE.

Or download the last version of dotenvx-cli from [dotenvx-rs](https://github.com/linux-china/dotenvx-rs),
and follow the steps below to add encrypted items.

## Integration
               
### Dependency
Add the following dependency to your `pom.xml`:

```xml

<dependency>
    <groupId>org.mvnsearch</groupId>
    <artifactId>dotenvx-spring-boot-starter</artifactId>
    <version>0.1.5</version>
</dependency>
```

### Configuration

Start your Spring Boot application, and add `-Ddotenv.private.key=your_private_key` to the command line,
or add `DOTENV_PRIVATE_KEY=your_private_key` to your environment variables.
and dotenvx start will automatically decrypt the `encrypted:` prefixed items in your properties or YAML files.

### Profile support

Please add `dotenv.public.key.profile-name` in `application-profile.properties`.

```
# ---
# id: 019881d9-39b0-7ec1-a623-5829d8480774
# name: project_ame
# group: group_name
# ---
dotenv.public.key.test=03f23142c47684e0eecda5bad9c2a6a32e461e55d5db1359948aee9e169d5aed4d
### spring boot configuration
nick2=encrypted:BMVDgpuPNebbj1NIHxJocBLOxBBxZM3oDqBJ8laGYYso1slYeNJcZs/7Qy1NKDsO+SPmnUd5UDV/LfEEctiyr2I81IGQfuuE8iZwVgqGq12KCa7CouLWH6cm/NRyzSr9PuqVtGdmfAk=
```

Start your Spring Boot application, and add `-Ddotenv.private.key.test=your_private_key` to the command line,
or add `DOTENV_PRIVATE_KEY_TEST=your_private_key_test` to your environment variables.
            

## FAQ

### How dotenvx-spring-boot reads private keys

- Read `dotenv.private.key` from `ConfigurableEnvironment`
- Read `DOTENV_PRIVATE_KEY` in your environment variables
- Read private key from `$HOME/.dotenvx/.env.keys.json` file by the public key
- Read private key from `.env.keys` or `$HOME/.env.keys` file

### Jackson Integration

If you want to use Dotenvx to protect some fields with JSON output, you can use the following code:

```java

@Configuration
public class DotenvxJacksonConfig {
    @Bean
    public SimpleModule dotenvxJacksonModule(@Value("${dotenvx.public.key}") String publicKey, @Value("${dotenvx.private.key}") String privateKey) {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(new DotenvxGlobalJsonSerializer(publicKey));
        simpleModule.addDeserializer(String.class, new DotenvxGlobalJsonDeserializer(privateKey));
        return simpleModule;
    }
}
```

For encryption, make sure the field's value with `private:` prefix.
For decryption, make sure the field's value with `encrypted:` prefix.
