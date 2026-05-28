package roomescape.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import roomescape.domain.Waiting;

public interface WaitingRepository {

    Waiting save(Waiting waiting);

    Optional<Waiting> findById(Long id);

    void deleteById(Long id);

    void updateOrder(Long id, int newOrder);


    List<Waiting> findBySlot(LocalDate date, Long timeId, Long themeId);

    List<Waiting> findByName(String name);
}
