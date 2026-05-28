package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;
import roomescape.domain.Waiting;
import roomescape.domain.Waitings;
import roomescape.exception.client.BusinessRuleViolationException;
import roomescape.exception.client.ResourceNotFoundException;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;
import roomescape.repository.WaitingRepository;
import roomescape.service.dto.WaitingCreateCommand;
import roomescape.service.dto.WaitingResult;

@Service
public class WaitingService {

    private final WaitingRepository waitingRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;

    public WaitingService(WaitingRepository waitingRepository,
                          ReservationRepository reservationRepository,
                          ReservationTimeRepository reservationTimeRepository,
                          ThemeRepository themeRepository) {
        this.waitingRepository = waitingRepository;
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
    }

    //ReservationService.validateNotDuplicated는 SQL에 비즈니스 규칙이 박혀 있었음
    // 이번에는 도메인이 규칙을 책임지도록
    // 같은 검증을 하는데, 책임의 위치가 다룸 !
    //TODO : 단일 insert이지만 트랜잭션 어노테이션 필요할듯,방어적으로 미리에 추가 작업이 들어올 경우 ex 알림 발송
    public WaitingResult create(WaitingCreateCommand command) {
        ReservationTime time = reservationTimeRepository.findById(command.getTimeId())
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 시간입니다."));
        Theme theme = themeRepository.findById(command.getThemeId())
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 테마입니다."));

        // 대기는 "이미 예약된 슬롯"에만 가능
        if (!reservationRepository.existsByDateAndTimeAndTheme(
                command.getDate(), command.getTimeId(), command.getThemeId())) {
            throw new BusinessRuleViolationException(
                    "예약 가능한 시간입니다. 대기가 아닌 예약을 신청해 주세요.");
        }

        //TODO 도메인쪽으로 더 넣을수도 있을것 같지만 우선 기록만
        // 같은 슬롯에 본인이 이미 대기 중이면 거부
        Waitings existing = new Waitings(
                waitingRepository.findBySlot(command.getDate(), time.getId(), theme.getId())
        );
        existing.validateNoDuplicateBy(command.getName());

        Waiting newWaiting = Waiting.create(
                command.getName(), command.getDate(), time, theme, existing.nextOrder()
        );
        Waiting saved = waitingRepository.save(newWaiting);
        return WaitingResult.from(saved);
    }

    //TODO @Transactional
    public void cancelByOwner(Long id, String name) {
        Waiting waiting = waitingRepository.findById(id)
                .filter(w -> w.isOwnedBy(name))
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 대기입니다."));//남의 대기

        waitingRepository.deleteById(id);

        // 같은 슬롯의 뒤 대기 순번을 한 칸씩 당김
        //트랜잭션이 진짜로 필요한 첫 순간 가시적으로 발견 !
        Waitings remaining = new Waitings(
                waitingRepository.findBySlot(
                        waiting.getDate(),
                        waiting.getTime().getId(),
                        waiting.getTheme().getId())
        );
        for (Waiting w : remaining.reorderAfterRemoval(waiting.getOrder())) {
            waitingRepository.updateOrder(w.getId(), w.getOrder());
        }
    }
}
