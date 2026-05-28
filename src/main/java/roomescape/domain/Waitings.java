package roomescape.domain;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import roomescape.exception.client.BusinessRuleViolationException;


/*
Waiting하나로 표현할 수 없는 비즈니스 규칙들이 있다
- "같은 슬롯에 본인이 이미 대기 중인가?" -> 다른 대기들을 모두 봐야 알 수 있음
- "다음 순번은 무엇인가?" -> 전체 대기 수에 의존
- "가장 앞 대기는?" -> 전체 중에서 가장 작은 order
- "누가 빠지면 어떤 순번이 어떻게 당겨지나?"
위와같은 책임을 서비스에 두먼 서비스에 SQL과 비즈니스 규칙이 섞이게 됨.
특히 reorderAfterRemoval
*/
// 수정가능한 컬렉션으로 보는게 아니라, '정책이 반영된 결과 모델'로 봐야함
public class Waitings {
    private final List<Waiting> waitings;

    public Waitings(List<Waiting> waitings) {
        this.waitings = waitings.stream()
                .sorted(Comparator.comparingInt(Waiting::getOrder))
                .toList();
    }

    // 같은 슬롯 + 같은 사용자의 중복 대기 여부
    private boolean hasWaitingBy(String name) {
        return waitings.stream()
                .anyMatch(w -> w.isOwnedBy(name));
    }

    public void validateNoDuplicateBy(String name) {
        if (hasWaitingBy(name)) {
            throw new BusinessRuleViolationException("이미 해당 시간에 대기 신청한 내역이 있습니다.");
        }
    }

    // 새 대기에 부여할 순번
    public int nextOrder() {
        return waitings.size() + 1;
    }

    // 가장 앞 대기 (승격 대상) -생섲자 불변 정렬 활용
    // 예약자가 취소했을 때 누가 예약자가 되는가
    public Optional<Waiting> firstWaiting() {
        return waitings.stream()
                .min(Comparator.comparingInt(Waiting::getOrder));
    }

    // 특정 순번 이후의 대기들을 한 칸씩 당김(순번 재정렬) - 생성자 정렬활용
    // 빠진 순서을 입력으로 받고, 순번이 바꿔야하는 대기들만 새순번이 부여된상태로 반환
    public List<Waiting> reorderAfterRemoval(int removedOrder) {
        return waitings.stream()
                .filter(w -> w.getOrder() > removedOrder)
                .map(w -> w.withOrder(w.getOrder() - 1))
                .toList();
    }

    public int size() {
        return waitings.size();
    }
}
