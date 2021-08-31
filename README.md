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

