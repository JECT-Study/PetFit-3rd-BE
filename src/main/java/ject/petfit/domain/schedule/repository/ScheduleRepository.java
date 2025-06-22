package ject.petfit.domain.schedule.repository;

import ject.petfit.domain.entry.entity.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import ject.petfit.domain.schedule.entity.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

//    List<Schedule> findAllByEntryOrderByTargetDateAsc(Entry entry);
//
//    @Query(
//            value = "SELECT * FROM schedule " +
//                    "WHERE entry_id = :entryId AND target_date >= :todayDate AND target_date < (:todayDate + INTERVAL ':selectDays day') " +
//                    "ORDER BY target_date ASC",
//            nativeQuery = true)
//    List<Schedule> findRecentSchedulesByEntry(
//            @Param("entryId") Long entryId,
//            @Param("todayDate") LocalDateTime todayDate,
//            @Param("selectDays") int selectDays);
//
//    List<Schedule> findAllByEntryAndTargetDateAfterOrderByTargetDateAsc(Entry entry, LocalDateTime afterDate);

    long countByEntry(Entry entry);
}
