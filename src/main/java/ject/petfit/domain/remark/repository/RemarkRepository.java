package ject.petfit.domain.remark.repository;

import ject.petfit.domain.entry.entity.Entry;
import ject.petfit.domain.remark.entity.Remark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RemarkRepository extends JpaRepository<Remark, Long> {
    long countByEntry(Entry entry);
}

