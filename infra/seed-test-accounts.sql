-- 테스트 계정 (비밀번호: qqqqqqq!)
INSERT INTO users (user_email, user_pwd, user_name, user_gender, user_birth, user_birth_back, user_phone, user_grade, user_marketing, user_privacy, user_status, user_sns, user_role, user_created_at)
VALUES
  ('1@1.1', '$2b$10$p802GNsSLEkTSrGRyl5Bluo1qFssYToLPb.KJzzBhPNiCcaKRqhuK', '관리자', 'M', '1990-01-01', '1234567', '01011111111', '대졸', true, true, 'ACTIVE', 'SELF', 'ADMIN', NOW()),
  ('2@2.2', '$2b$10$p802GNsSLEkTSrGRyl5Bluo1qFssYToLPb.KJzzBhPNiCcaKRqhuK', '강사', 'M', '1985-05-15', '1234567', '01022222222', '대졸', true, true, 'ACTIVE', 'SELF', 'INSTRUCTOR', NOW()),
  ('3@3.3', '$2b$10$p802GNsSLEkTSrGRyl5Bluo1qFssYToLPb.KJzzBhPNiCcaKRqhuK', '수강생', 'F', '2000-03-20', '2345678', '01033333333', '고졸', true, true, 'ACTIVE', 'SELF', 'USER', NOW())
ON DUPLICATE KEY UPDATE user_email=user_email;
