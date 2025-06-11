# 概览

| 依赖                       | 用途          |
|--------------------------|-------------|
| Spring Boot Starter Web	 | REST API 开发 |
| Caffeine	                | 高性能内存缓存     |
| Lombok	                  | 减少样板代码      |
| Hibernate Validator	     | 请求验证        |
| Spring Boot Test	        | 测试支持        |
| JMeter	                  | 压力测试        |

## 运行说明

### 构建项目

```bash
mvn clean package
```

### 本地运行

```bash
java -jar target/TradeManagerSystem-1.0.0.jar
```

### Docker 运行

```bash
docker build -t trade-system .
docker run -p 8080:8080 trade-system
```

### 使用 docker-compose

```bash
docker-compose up
```

### 访问应用：

- 前端页面：http://localhost:8080/index.html


