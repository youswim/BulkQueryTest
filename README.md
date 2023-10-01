# BULK QUERY 실험 예제 코드

## 설명

- BULK 쿼리의 성능을 측정하기 위해 사용한 자바 코드

## 사용 DB

- PostgreSQL

## 사용 방법

`TestCase` 클래스에 있는 아래 설정값들을 사용자의 DB 환경에 맞추어 변경

`System.getenv(String str)` 메서드로 가져오는 값들은 환경변수로 등록해둔 값을 불러오는 것임.  
인텔리제이에서 제공하는 환경변수 등록 기능을 사용하면 편리하게 등록-관리할 수 있음.

```java
private static final String url = "jdbc:postgresql://localhost:5432/postgres";

private static final String user = System.getenv("USERNAME"); // 환경변수로 등록!

private static final String password = System.getenv("PASSWORD"); // 환경변수로 등록!
```
