drop table if exists waiting;
drop table if exists reservation;
drop table if exists reservation_time;
drop table if exists theme;

CREATE TABLE reservation_time (
                                  id       BIGINT       NOT NULL AUTO_INCREMENT,
                                  start_at TIME NOT NULL,
                                  PRIMARY KEY (id),
                                  UNIQUE (start_at)
);

CREATE TABLE theme (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(30) NOT NULL ,
  description   VARCHAR(255) NOT NULL ,
  thumbnail_url  VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
);


CREATE TABLE reservation (
                             id      BIGINT       NOT NULL AUTO_INCREMENT,
                             name    VARCHAR(30) NOT NULL,
                             date    DATE NOT NULL,
                             time_id BIGINT NOT NULL,
                             theme_id BIGINT NOT NULL,
                             PRIMARY KEY (id),
                             FOREIGN KEY (time_id) REFERENCES reservation_time (id),
                             FOREIGN KEY (theme_id) REFERENCES theme (id),
                             UNIQUE (date, time_id, theme_id)
);


CREATE TABLE waiting (
                         id         BIGINT       NOT NULL AUTO_INCREMENT,
                         name       VARCHAR(255) NOT NULL,
                         date       DATE NOT NULL,
                         time_id    BIGINT       NOT NULL,
                         theme_id   BIGINT       NOT NULL,
                         wait_order INT          NOT NULL,
                         PRIMARY KEY (id),
                         FOREIGN KEY (time_id) REFERENCES reservation_time (id),
                         FOREIGN KEY (theme_id) REFERENCES theme (id),
                         UNIQUE (date, time_id, theme_id,wait_order) -- 분리보델로 얻은 동시성 처리, DuplicateKeyException 처리 고려
    --WaitingService.create()에서 waitingRepository.save() 중복키 예외를 잡아 BusinessRuleViolationException으로 바꾸는 로직 등등..
     -- 순번 재정렬 업데이트 때 UNIQUE가 걸림돌이 될 수 있다.
    -- 예를 들어 wait_order가 1,2,3인데 1번이 빠져서 2번을 1로, 3번을 2로 바꾸는 건 괜찮아 보이지만,
    -- 업데이트 순서에 따라 순간적으로 충돌할 수 있다. 현재 코드는 삭제 후 뒤 순번만 -1 하니까 대체로 괜찮지만,
    -- DB에 따라 제약 검사가 statement 단위라 충돌 가능성을 신경 써야 함. DB에따라 다른 부분 고려 리뷰

);
