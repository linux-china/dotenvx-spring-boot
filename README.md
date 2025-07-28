dotenvx-spring-boot
=======================

**[Dotenvx](https://dotenvx.com)** integration for Spring Boot 3.x to load encrypted items from `application.properties`
or `application.yml`.

# Get Started

Download dotenvx-cli from [dotenvx-rs](https://github.com/linux-china/dotenvx-rs),
and execute `dotenvx init` to add `.env` and `.env.keys` files to your project.

Copy encrypted env variable from `.env` to `application.properties` or `application.yml`, example as follows:

```properties
# dotenvx public key
dotenv.public.key=02e8d78f0da7fc3b529d503edd933ed8cdc79dbe5fd5d9bd480f1e63a09905f3b3
nick=encrypted:BFpnkHl81r6SFJlzpuCNFe70zOezu3vzkOygmRsAqy0H8zsklDBThtgVl6XDKpZOWq+qHimszEusev2xKXgG2ISdYDbcayNZB2Dd2q5qpo2RqUD0AT9XPrJqPT7DVFBw+hFCZwwqdg==
```

Start your Spring Boot application, and add `-Ddotenv.private.key=your_private_key` to the command line,
and dotenvx start will automatically decrypt the `encrypted:` prefixed items in your properties or YAML files.
If you are using [Spring Debugger](https://www.jetbrains.com/help/idea/spring-debugger.html),
and you will notice the decrypted value as hints in the configuration files.

**Note**: The `dotenv.public.key` is from `.env.keys` file.

**Important**: Make sure to keep your `.env.keys` secure and do not commit it to version control.

# Credits

* jasypt-spring-boot: https://github.com/ulisesbocchio/jasypt-spring-boot

# References

* [Dotenvx](https://dotenvx.com/): encrypts your .env files, limiting their attack vector while retaining their
  benefits.
* [dotenvx-rs](https://github.com/linux-china/dotenvx-rs): Dotenvx Rust SDK/CLI