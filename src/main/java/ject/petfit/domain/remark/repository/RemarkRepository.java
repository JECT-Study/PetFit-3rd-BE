package ject.petfit.domain.remark.repository;

import ject.petfit.domain.entry.entity.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import ject.petfit.domain.remark.entity.Remark;

public interface RemarkRepository extends JpaRepository<Remark, Long> {
    long countByEntry(Entry entry);
}

