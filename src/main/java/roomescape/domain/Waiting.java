package roomescape.domain;

import java.time.LocalDate;
import roomescape.domain.exception.InvalidDomainException;

public class Waiting {
    private static final int MAX_NAME_LENGTH = 30;

    private final Long id;
    private final String name;
    private final LocalDate date;
    private final ReservationTime time;
    private final Theme theme;
    private final int order;

    private Waiting(Long id, String name, LocalDate date,
                    ReservationTime time, Theme theme, int order) {
        validate(name, date, time, theme, order);
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
        this.theme = theme;
        this.order = order;
    }

    // 신규 대기 생성 (저장 전)
    public static Waiting create(String name, LocalDate date,
                                 ReservationTime time, Theme theme, int order) {
        return new Waiting(null, name, date, time, theme, order);
    }

    public static Waiting withId(Long id, String name, LocalDate date,
                                 ReservationTime time, Theme theme, int order) {
        return new Waiting(id, name, date, time, theme, order);
    }

    private static void validate(String name, LocalDate date,
                                 ReservationTime time, Theme theme, int order) {
        if (name == null || name.isBlank()) {
            throw new InvalidDomainException("대기자 이름은 비어 있을 수 없습니다.");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new InvalidDomainException(
                    "대기자 이름은 " + MAX_NAME_LENGTH + "자를 초과할 수 없습니다.");
        }
        if (date == null) {
            throw new InvalidDomainException("대기 날짜는 비어 있을 수 없습니다.");
        }
        if (time == null) {
            throw new InvalidDomainException("대기 시간은 비어 있을 수 없습니다.");
        }
        if (theme == null) {
            throw new InvalidDomainException("대기 테마는 비어 있을 수 없습니다.");
        }
        if (order < 1) {
            throw new InvalidDomainException("대기 순번은 1 이상이어야 합니다.");
        }
    }

    //순번이 바뀔 때 기존 객체를 수정하지 않고 새 객체를 만들어 반환.
    // Waitings안에서 여러 대기를 다룰 때 누가 누구의 순번을 바꿨는지 추적할 필요가 없어짐.
    public Waiting withOrder(int newOrder) {
        return new Waiting(this.id, this.name, this.date, this.time, this.theme, newOrder);
    }

    //테스트에서만 사용하는 메서드
    public boolean isSameSlot(LocalDate date, Long timeId, Long themeId) {
        return this.date.equals(date)
                && this.time.getId().equals(timeId)
                && this.theme.getId().equals(themeId);
    }

    public boolean isOwnedBy(String name) {
        return this.name.equals(name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }

    public ReservationTime getTime() {
        return time;
    }

    public Theme getTheme() {
        return theme;
    }

    public int getOrder() {
        return order;
    }
}
