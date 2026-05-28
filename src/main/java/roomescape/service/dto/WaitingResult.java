package roomescape.service.dto;

import java.time.LocalDate;
import roomescape.domain.Waiting;

public class WaitingResult {
    private final Long id;
    private final String name;
    private final LocalDate date;
    private final ReservationTimeResult time;
    private final ThemeResult theme;
    private final int order;

    public WaitingResult(Long id, String name, LocalDate date,
                         ReservationTimeResult time, ThemeResult theme, int order) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
        this.theme = theme;
        this.order = order;
    }

    public static WaitingResult from(Waiting w) {
        return new WaitingResult(
                w.getId(),
                w.getName(),
                w.getDate(),
                ReservationTimeResult.from(w.getTime()),
                ThemeResult.from(w.getTheme()),
                w.getOrder()
        );
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

    public ReservationTimeResult getTime() {
        return time;
    }

    public ThemeResult getTheme() {
        return theme;
    }

    public int getOrder() {
        return order;
    }
}
