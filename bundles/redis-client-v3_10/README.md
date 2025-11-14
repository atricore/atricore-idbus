# Redis Client Bundle for JOSSO

This bundle provides Redis client support for JOSSO in OSGi/Karaf 2 environments.

## Features

- Redis client based on Jedis 3.10.0 (Java 8 compatible)
- Connection pooling with Apache Commons Pool 2
- Java serialization support for JOSSO objects
- Spring integration with lifecycle management
- OSGi/Karaf 2 compatible

## Configuration

### Spring Bean Configuration

```xml
<bean id="redisClientFactory" 
      class="org.atricore.idbus.bundles.redis.SpringRedisClientFactoryImpl">
    <property name="host" value="localhost"/>
    <property name="port" value="6379"/>
    <property name="database" value="0"/>
    <property name="maxTotal" value="50"/>
    <property name="maxIdle" value="10"/>
</bean>

<bean id="redisSerializer" 
      class="org.atricore.idbus.bundles.redis.serialization.JavaRedisSerializer"/>
```

### Configuration Properties

| Property | Default | Description |
|----------|---------|-------------|
| host | localhost | Redis server hostname |
| port | 6379 | Redis server port |
| password | (empty) | Redis password (optional) |
| database | 0 | Redis database number |
| connectionTimeout | 2000 | Connection timeout (ms) |
| socketTimeout | 2000 | Socket timeout (ms) |
| maxTotal | 50 | Maximum pool connections |
| maxIdle | 10 | Maximum idle connections |
| minIdle | 2 | Minimum idle connections |
| testOnBorrow | true | Test connection before use |
| testWhileIdle | true | Test idle connections |

## Usage

### Basic Usage

```java
@Autowired
private RedisClientFactory redisClientFactory;

@Autowired
private JavaRedisSerializer serializer;

public void storeObject(String key, Object value) {
    Jedis jedis = null;
    try {
        jedis = redisClientFactory.getConnection();
        byte[] data = serializer.serialize(value);
        jedis.setex(key.getBytes(), 3600, data); // TTL: 1 hour
    } finally {
        if (jedis != null) {
            jedis.close(); // Returns to pool
        }
    }
}

public Object retrieveObject(String key) {
    Jedis jedis = null;
    try {
        jedis = redisClientFactory.getConnection();
        byte[] data = jedis.get(key.getBytes());
        return serializer.deserialize(data);
    } finally {
        if (jedis != null) {
            jedis.close();
        }
    }
}
```

## Building

```bash
cd bundles/redis-client-v3_10
mvn clean install
```

## Installation in Karaf

```
karaf@root> install -s mvn:org.atricore.idbus.bundles/org.atricore.idbus.bundles.redis-client-v3_10/1.7.0-SNAPSHOT
```

## Dependencies

- Jedis 3.10.0
- Apache Commons Pool 2.11.1
- Spring Framework 3.1.0 (provided)
- SLF4J 1.7.25 (provided)

## License

LGPL 2.1 - See LICENSE file for details
