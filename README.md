dotenvx-spring-boot
=======================

**[Dotenvx](https://dotenvx.com)** integration for Spring Boot 3.x to load encrypted items from `application.properties`
or `application.yml`.

# Get Started

If you use IntelliJ IDEA, please download [Dotenvx JetBrains Plugin](https://plugins.jetbrains.com/plugin/28148-dotenvx)
first, and all operations could be finished in IDE.

Or download the last version of dotenvx-cli from [dotenvx-rs](https://github.com/linux-china/dotenvx-rs),
and follow the steps below to add encrypted items.

If you want to a real Dotenvx Spring Boot demo, please
check [dotenvx-spring-boot-demo](https://github.com/linux-china/dotenvx-boot-demo).

```bash
$ cd spring-boot-project-dir
$ dotenvx init
$ dotenvx set --encrypt nick Jackie
```

`application.properties` content example:

```properties
# ---
# uuid: 019852dd-8798-7991-b1df-c2b388de0fc4
# sign: 6xEkGwGLig9DuO+iO5jm4RTBG+oQjKZt0XHvVQ28VZIDM8PFaaHMmG+S/xfezoCUJuMvqlFFNOokCg4WIkBWsg==
# ---
dotenv.public.key=02324d763b27358d4229651fd9d0822fb263b07bcc3422f5bd9968cafc194011ff
### spring configuration
nick=encrypted:BDFsggcvh9IiNUIZ66YrEI10sLoUJS6WW+UiUxqfAGyHo6cfg9lQ1DhOy9z18F50aRicFHZ7dXH7CagfhonUnZA5W+l1xldVBzv4b8OJN05qih2PHIcY01spqx6RYrgg76pUsuv2eA==
```

Add the following dependency to your `pom.xml`:

```xml

<dependency>
    <groupId>org.mvnsearch</groupId>
    <artifactId>dotenvx-spring-boot-starter</artifactId>
    <version>0.1.2</version>
</dependency>
```

Start your Spring Boot application, and add `-Ddotenv.private.key=your_private_key` to the command line,
or add `DOTENV_PRIVATE_KEY=your_private_key` to your environment variables.
and dotenvx start will automatically decrypt the `encrypted:` prefixed items in your properties or YAML files.
If you are using [Spring Debugger](https://www.jetbrains.com/help/idea/spring-debugger.html),
and you will notice the decrypted value as hints in the configuration files.

**Note**: The private key for `application.properties` is from `$HOME/.dotenvx/.env.keys.json`.
**Attention**: dotenvx-spring-boot-starter uses Bouncy Castle `bcprov-jdk18on`(JDK 1.8+).
If `bcprov-jdk15on` is in project's dependencies, please pay attention to confliction.

# Profile support

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

# .env.keys for application test

```
# ---
# id: 019852dd-8798-7991-b1df-c2c8b743a0e1
# name: spring-boot-test
# group: demo
# ---

# Private decryption keys. DO NOT commit to source control
DOTENV_PRIVATE_KEY=a7a0006f9136c246937a5ae60f11cfb71541df0dac389015e6916b3ebbe170cd
DOTENV_PRIVATE_KEY_TEST=0c8eac932150e0d51cfc59ccbd2c0613298464b2922d900b96511cf7239b7aa5
```

# How dotenvx-spring-boot reads private keys

- Read `dotenv.private.key` from `ConfigurableEnvironment`
- Read `DOTENV_PRIVATE_KEY` in your environment variables
- Read private key from `$HOME/.dotenvx/.env.keys.json` file by the public key
- Read private key from `.env.keys` or `$HOME/.env.keys` file

# Jackson Integration

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

For encryption, make sure the field value with `private:` prefix.
For decryption, make sure the field value with `encrypted:` prefix.

**Tips**: please use `dotenvx init --stdout` to generate a new key pair for this case. Don't use app config key pair.

# Credits

* jasypt-spring-boot: https://github.com/ulisesbocchio/jasypt-spring-boot

# References

* [Dotenvx JetBrains Plugin](https://plugins.jetbrains.com/plugin/28148-dotenvx/): Dotenvx JetBrains IDE plugin with
  Spring Boot support
* [Dotenvx](https://dotenvx.com/): encrypts your .env files, limiting their attack vector while retaining their
  benefits.
* [dotenvx-rs](https://github.com/linux-china/dotenvx-rs): Dotenvx Rust SDK/CLI
* [dotenvx-java](https://github.com/linux-china/dotenvx-java): Dotenvx Java SDK
