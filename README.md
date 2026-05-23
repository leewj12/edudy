# Edudy — 교육 기관 ERP 시스템 (백엔드)

Spring Boot 기반의 교육 기관 관리 시스템 백엔드 API입니다.
수강생·강사·관리자 역할을 구분하고, 강의 관리부터 출결·상담·성적·설문까지 전 과정을 지원합니다.

---

## 목차

1. [프로젝트 소개](#프로젝트-소개)
2. [주요 기능](#주요-기능)
3. [기술 스택](#기술-스택)
4. [아키텍처](#아키텍처)
5. [실행 방법](#실행-방법)
6. [Docker 실행](#docker-실행)
7. [EC2 배포](#ec2-배포)
8. [환경 변수](#환경-변수)

---

## 프로젝트 소개

| 항목 | 내용 |
|------|------|
| 프로젝트 유형 | 팀 프로젝트 (포트폴리오) |
| 개발 기간 | 2024.11 ~ 2024.12 |
| 서버 포트 | 8083 (로컬) / 9092 (Docker) |
| 데이터베이스 | MySQL 8.0, 스키마: `proj4_db` |
| 프론트엔드 | [edudy-front](https://github.com/leewj12/edudy-front) (React + Vite, Vercel 배포) |
| 배포 주소 | https://edudy.wonjae.cloud |

---

## 주요 기능

### 회원
- 회원가입 / 로그인 / 로그아웃 (JWT 인증)
- 역할 기반 접근 제어 (STUDENT / INSTRUCTOR / ADMIN)
- 비밀번호 변경 및 프로필 관리

### 강의 관리
- 강의 등록 / 수정 / 삭제 (카테고리, 과목, 스태프 포함)
- 강의 목록 조회 및 우선순위 설정
- 교육 과정(Train) 등록 및 시간표 관리

### 출결 관리
- QR 코드 기반 출석 체크
- 자동 결석 처리 (스케줄러, 주말·공휴일 제외)
- 출결 현황 조회 및 사유 관리

### 수강생 관리
- 수강생 정보 / 상태 / 위험도 관리
- 경력·희망·자격증 정보 등록

### 상담
- 수강생 상담 기록 등록 / 수정 / 조회
- 상담 키워드별 집계

### 성적
- 성적 등록 / 수정 / 조회

### 설문
- 강의 만족도 설문 생성 및 응답 관리

### 관리자
- 대시보드 (출석률, 수강 현황 요약)
- 배너 관리 (이미지 업로드, 우선순위)
- SMS 발송 (Solapi 연동)
- 공휴일 관리 (공공 API 연동)

---

## 기술 스택

| 구분 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 3.4.5 |
| ORM | Spring Data JPA |
| Security | Spring Security 6, JWT (jjwt 0.12.5) |
| Database | MySQL 8.0 |
| Build | Gradle |
| SMS | Solapi SDK |
| Container | Docker, Docker Compose |
| CI/CD | GitHub Actions |
| Infra | AWS EC2 t3.small, Nginx, Let's Encrypt (HTTPS) |

---

## 아키텍처

```
[Browser / React App]
    │
    ▼
[Nginx :80/:443]  ← HTTPS (Let's Encrypt)
    │
    ▼
[Spring Boot API :9092]
    ├── RestController (JSON API)
    ├── Service
    ├── Spring Data JPA ──── [MySQL :3306]
    └── Spring Security + JWT (STUDENT / INSTRUCTOR / ADMIN)
```

---

## 실행 방법

### 사전 요구사항

- Java 17
- MySQL 8.0
- Gradle (gradlew 포함)

### 1. 데이터베이스 준비

```sql
CREATE DATABASE proj4_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 환경 설정

`src/main/resources/application-local.yml`을 생성합니다 (`.gitignore`에 포함되어 커밋되지 않습니다).

```yaml
DB_URL: jdbc:mysql://localhost:3306/proj4_db
DB_USERNAME: root
DB_PASSWORD: your_mysql_password
JWT_SECRET: your_jwt_secret_here
```

### 3. 실행

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

브라우저에서 `http://localhost:8083` 접속 (API 서버)

---

## Docker 실행

### 사전 요구사항

- Docker Desktop
- 기존 `projects-mysql` 컨테이너 및 `koslearn_default` 네트워크 필요

### 1. 환경 변수 파일 준비

```bash
cp .env.example .env
# .env 파일을 열어 비밀번호 및 JWT 시크릿 입력
```

### 2. JAR 빌드

```bash
./gradlew bootJar -x test
```

### 3. 데이터베이스 스키마 생성

기존 `projects-mysql` 컨테이너에 접속하여 스키마를 생성합니다.

```bash
docker exec -it projects-mysql mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS proj4_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

### 4. 빌드 및 실행

```bash
cp build/libs/*-SNAPSHOT.jar app.jar
docker compose up --build -d
```

### 5. 접속

`http://localhost:9092`

### 6. 종료

```bash
docker compose down          # 컨테이너 종료 (데이터 유지)
docker compose down -v       # 컨테이너 + 볼륨 삭제 (업로드 파일 초기화)
```

---

## CI/CD (GitHub Actions)

`main` 브랜치에 푸시하면 자동으로 빌드 → EC2 배포가 실행됩니다.

### GitHub Secrets 설정

GitHub 레포 → Settings → Secrets and variables → Actions에서 아래 3개를 등록합니다.

| 키 | 값 |
|----|-----|
| `EC2_HOST` | EC2 퍼블릭 IP 또는 도메인 |
| `EC2_USER` | `ubuntu` |
| `EC2_SSH_KEY` | `.pem` 파일을 base64 인코딩한 값 (`base64 -i key.pem \| tr -d '\n'`) |

### EC2 사전 설정 (최초 1회)

```bash
mkdir -p ~/edudy
cd ~/edudy
cp /path/to/.env.example .env
vi .env
```

### 동작 방식

1. GitHub Actions 러너에서 `./gradlew bootJar` 빌드
2. Docker 이미지를 빌드하여 `ghcr.io/leewj12/edudy:latest`로 Push
3. SSH로 EC2 접속 → `git pull` → `docker compose pull` → `docker compose up -d`

EC2에서는 이미지를 Pull만 하므로 서버 리소스를 절약하고 배포 안정성이 높습니다.

---

## EC2 배포

- **서버**: AWS EC2 t3.small (Ubuntu, 서울 리전)
- **도메인**: [edudy.wonjae.cloud](https://edudy.wonjae.cloud)
- **SSL**: Let's Encrypt (Certbot, 자동 갱신)
- **리버스 프록시**: Nginx (포트 80/443 → 앱 9092)
- **실행 방식**: Docker Compose (기존 mysql 컨테이너 공유)
- **재시작 정책**: `restart: unless-stopped`

### Nginx 설정

`infra/nginx/edudy.conf` 파일을 EC2의 `/etc/nginx/sites-available/`에 복사합니다.

```bash
sudo cp infra/nginx/edudy.conf /etc/nginx/sites-available/edudy
sudo ln -s /etc/nginx/sites-available/edudy /etc/nginx/sites-enabled/
sudo certbot --nginx -d edudy.wonjae.cloud
sudo nginx -t && sudo systemctl reload nginx
```

### 멀티 프로젝트 포트 계획 (동일 인스턴스)

| 프로젝트 | 앱 포트 |
|----------|---------|
| proj2 (KosLearn) | 9090 |
| proj3 (MetaLearning) | 9091 |
| proj4 (Edudy) | 9092 |

---

## 환경 변수

`.env.example`을 복사하여 `.env`를 생성한 뒤 실제 값을 입력합니다.

| 변수명 | 설명 |
|--------|------|
| `DB_USERNAME` | MySQL 사용자명 |
| `DB_PASSWORD` | MySQL 비밀번호 |
| `JWT_SECRET` | JWT 서명 키 (32자 이상) |
