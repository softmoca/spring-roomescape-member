package roomescape.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import roomescape.service.dto.WaitingResult;

public class WaitingResponse {
    private final Long id;
    private final String name;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate date;
    private final ReservationTimeResponse time;
    private final ThemeResponse theme;
    private final int order;

    public WaitingResponse(Long id, String name, LocalDate date,
                           ReservationTimeResponse time, ThemeResponse theme, int order) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
        this.theme = theme;
        this.order = order;
    }

    public static WaitingResponse from(WaitingResult r) {
        return new WaitingResponse(
                r.getId(),
                r.getName(),
                r.getDate(),
                ReservationTimeResponse.from(r.getTime()),
                ThemeResponse.from(r.getTheme()),
                r.getOrder()
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

    public ReservationTimeResponse getTime() {
        return time;
    }

    public ThemeResponse getTheme() {
        return theme;
    }

    public int getOrder() {
        return order;
    }
}
