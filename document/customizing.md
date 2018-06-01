# TB_WORKER 데이터 연동 관련 패키지

`lts-iam/src/main/java/org/uengine/iam/provider` 패키지의,

 - WorkerEntity : 엔티티
 - WorkerEntityRepository :  레파지토리 클래스
 - MyUserRepositoryImpl :  서비스 클래스

# IAM 의 Dynamic Database configuration 관련

 - lts-iam/src/main/java/org/uengine/iam/JpaConfig.java
 - lts-iam/src/main/java/org/uengine/iam/MultitenantRoutingDataSource.java

# 주요 용어 및 도메인

 - Authentication: 인증
 - Authorization: 허가
 - User: 사용자
 - Client: 어플리케이션 (프론트 엔드) 또는 써드파티 어플리케이션
 - Scope: 리소스 권한
 - AccessToken: Authorization 과정을 거친 후 발급된 토큰
 - Resource Server: REST API 서버 (백엔드)




