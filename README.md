# Generates procedures stub

Maven plugin for generating procedures stubs for all interfaces with AStoreProcedure annotation.

## How to use

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.payneteasy.jdbc-proc</groupId>
            <artifactId>jdbc-proc-maven-plugin</artifactId>
            <version>1.0-1-SNAPSHOT</version>
            <configuration>
                <metaLoginRoleName>role_name</metaLoginRoleName>
                <metaLoginUsername>username</metaLoginUsername>
                <targetDir>target/procedures</targetDir>
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <goal>generate</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

## Configuration parameters

| Name              | Default value     | Description                              |
| ----------------- | ----------------- | ---------------------------------------- |
| metaLoginUsername | username          | Username field name for @AMetaLoginInfo  |
| metaLoginRoleName | role_name         | Role name field name for @AMetaLoginInfo |
| targetDir         | target/procedures | Where to generate stored procedures      |

