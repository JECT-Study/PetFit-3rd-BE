### JECT 3팀 서비스 PetFit BackEnd Repository

## 🚀 CI/CD Pipeline

이 프로젝트는 GitHub Actions를 사용하여 자동화된 CI/CD 파이프라인을 구성하고 있습니다.

### 📋 워크플로우 구성

#### 1. Main Branch Pipeline (`ci-cd.yml`)
- **트리거**: `main` 브랜치에 push 또는 PR
- **단계**:
  1. **Test**: OpenJDK 17 환경에서 테스트 실행
  2. **Build & Push**: Docker 이미지 빌드 및 GitHub Container Registry에 푸시
  3. **Deploy**: 환경변수 주입 및 배포 (실제 배포 스크립트는 추가 필요)

#### 2. Development Pipeline (`development.yml`)
- **트리거**: `develop` 브랜치에 push 또는 PR
- **단계**:
  1. **Test & Build**: 테스트 실행 및 빌드
  2. **Artifact Upload**: 빌드 결과물을 아티팩트로 저장

### 🔧 설정 방법

#### 1. GitHub Secrets 설정
GitHub 저장소의 Settings > Secrets and variables > Actions에서 다음 시크릿을 설정하세요:

```
POSTGRES_USER=your_postgres_user
POSTGRES_PASSWORD=your_postgres_password
POSTGRES_DB=your_postgres_db
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/your_db
SPRING_DATASOURCE_USERNAME=your_db_username
SPRING_DATASOURCE_PASSWORD=your_db_password
JWT_SECRET_KEY=your_jwt_secret_key
KAKAO_CLIENT_ID=your_kakao_client_id
KAKAO_CLIENT_SECRET=your_kakao_client_secret
KAKAO_REDIRECT_URI=your_kakao_redirect_uri
```

#### 2. GitHub Container Registry 권한 설정
- Settings > Actions > General에서 "Workflow permissions" 섹션에서 "Read and write permissions" 선택
- "Allow GitHub Actions to create and approve pull requests" 체크

### 🐳 Docker 환경변수 주입

워크플로우는 Docker Compose에서 사용하는 `.env` 파일을 GitHub Secrets로부터 자동 생성합니다:

```yaml
# .env 파일 자동 생성
POSTGRES_USER=${POSTGRES_USER}
POSTGRES_PASSWORD=${POSTGRES_PASSWORD}
POSTGRES_DB=${POSTGRES_DB}
# ... 기타 환경변수들
```

### 📦 빌드 결과물

- **Docker Image**: `ghcr.io/{username}/{repository}:latest`
- **Build Artifacts**: `build/libs/` 디렉토리의 JAR 파일들

### 🔍 모니터링

GitHub Actions 탭에서 워크플로우 실행 상태를 실시간으로 확인할 수 있습니다.

### 📝 추가 설정

실제 서버 배포를 위해서는 `ci-cd.yml`의 deploy 단계에 다음 중 하나를 추가하세요:

1. **SSH를 통한 서버 배포**
2. **Kubernetes 배포**
3. **AWS ECS/Fargate 배포**
4. **Google Cloud Run 배포**

## 🛠 기술 스택

- **Java**: OpenJDK 17
- **Framework**: Spring Boot 3.5.0
- **Build Tool**: Gradle
- **Database**: PostgreSQL
- **Container**: Docker & Docker Compose
- **CI/CD**: GitHub Actions

## 🔧 문제 해결

### JWT 토큰 오류 해결

`유효하지 않은 토큰입니다` 오류가 발생하는 경우:

#### 1. 환경변수 확인
다음 환경변수들이 제대로 설정되어 있는지 확인하세요:

```bash
# JWT 설정
JWT_ISSUER=petfit
JWT_SECRET=your_secure_jwt_secret_key_at_least_256_bits_long
JWT_ACCESS_TOKEN_TIME=3600000

# 데이터베이스 설정
DB_PASSWORD=your_database_password
POSTGRES_USER=postgres
POSTGRES_PASSWORD=your_postgres_password
POSTGRES_DB=petfit
```

#### 2. JWT Secret Key 생성
안전한 JWT Secret Key를 생성하려면:

```bash
# 256비트 (32바이트) 랜덤 키 생성 (권장)
openssl rand -base64 32

# 또는 더 긴 키 생성 (512비트)
openssl rand -base64 64
```

**중요**: JWT Secret Key는 최소 256비트(32바이트) 이상이어야 합니다. 
키가 너무 짧으면 애플리케이션이 자동으로 안전한 키를 생성하지만, 
환경변수에서 설정한 키와 다를 수 있으므로 토큰이 무효화될 수 있습니다.

#### 3. 환경변수 파일 설정
`env.example` 파일을 참고하여 `.env` 파일을 생성하세요:

```bash
cp env.example .env
# .env 파일을 편집하여 실제 값으로 변경
```

#### 4. 애플리케이션 재시작
환경변수를 변경한 후 애플리케이션을 재시작하세요:

```bash
docker-compose down
docker-compose up -d
```

#### 5. 로그 확인
JWT 관련 로그를 확인하여 문제를 진단할 수 있습니다:

```bash
docker-compose logs -f petfit-web
```

### 인증이 필요 없는 엔드포인트

다음 엔드포인트들은 JWT 토큰 없이 접근 가능합니다:

- `/api/auth/**` - 인증 관련 엔드포인트
- `/swagger-ui/**` - Swagger UI
- `/v3/api-docs/**` - API 문서
- `/health/**` - 헬스 체크
- `/api/pet/**` - 펫 관련 (개발용)
- `/api/routines/**` - 루틴 관련 (개발용)
- `/api/remarks/**` - 리마크 관련 (개발용)
- `/api/schedules/**` - 스케줄 관련 (개발용)
- `/api/slots/**` - 슬롯 관련 (개발용)
- `/api/entries/**` - 엔트리 관련 (개발용)
- `/api/members/**` - 멤버 관련 (개발용)

### 카카오 OAuth 설정

카카오 로그인 기능을 사용하려면 다음 환경변수를 설정해야 합니다:

```bash
# 카카오 OAuth 설정
KAKAO_CLIENT_ID=your_kakao_client_id
KAKAO_CLIENT_SECRET=your_kakao_client_secret
KAKAO_REDIRECT_URI=http://localhost:8080/api/auth/kakao/login
KAKAO_LOGOUT_REDIRECT_URI=http://localhost:3000
KAKAO_ADMIN_KEY=your_kakao_admin_key
```

#### 카카오 개발자 설정

1. **카카오 개발자 콘솔**에서 애플리케이션 생성
2. **플랫폼 > Web** 설정에서 사이트 도메인 등록
3. **카카오 로그인 > 동의항목** 설정
4. **카카오 로그인 > Redirect URI** 설정: `http://localhost:8080/api/auth/kakao/login`

### 500 오류 해결

`{"success":false,"code":"SERVER-500","message":"서버 내부 오류가 발생하였습니다."}` 오류가 발생하는 경우:

#### 1. 환경변수 확인
모든 필수 환경변수가 설정되어 있는지 확인:

```bash
# JWT 설정
JWT_ISSUER=petfit
JWT_SECRET=your_secure_jwt_secret_key_at_least_256_bits_long
JWT_ACCESS_TOKEN_TIME=3600000

# 데이터베이스 설정
DB_PASSWORD=your_database_password
POSTGRES_USER=postgres
POSTGRES_PASSWORD=your_postgres_password
POSTGRES_DB=petfit

# 카카오 OAuth 설정
KAKAO_CLIENT_ID=your_kakao_client_id
KAKAO_CLIENT_SECRET=your_kakao_client_secret
KAKAO_REDIRECT_URI=http://localhost:8080/api/auth/kakao/login
KAKAO_LOGOUT_REDIRECT_URI=http://localhost:3000
KAKAO_ADMIN_KEY=your_kakao_admin_key
```

#### 2. 로그 확인
상세한 오류 로그를 확인하세요:

```bash
docker-compose logs -f petfit-web
```

#### 3. 데이터베이스 연결 확인
PostgreSQL이 정상적으로 실행되고 있는지 확인:

```bash
docker-compose ps
docker-compose logs db
```

#### 4. 카카오 OAuth 설정 확인
- 카카오 개발자 콘솔에서 Redirect URI가 정확히 설정되어 있는지 확인
- Client ID와 Client Secret이 올바른지 확인
- 카카오 로그인 동의항목이 설정되어 있는지 확인
