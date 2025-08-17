package ject.petfit.domain.remark.service;

import ject.petfit.domain.entry.entity.Entry;
import ject.petfit.domain.remark.entity.Remark;
import ject.petfit.domain.remark.exception.RemarkErrorCode;
import ject.petfit.domain.remark.exception.RemarkException;
import ject.petfit.domain.remark.repository.RemarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RemarkQueryService {
    private final RemarkRepository remarkRepository;

    public Remark getRemarkOrThrow(Long remarkId) {
        return remarkRepository.findById(remarkId)
                .orElseThrow(() -> new RemarkException(RemarkErrorCode.REMARK_NOT_FOUND));
    }

    public long countByEntry(Entry entry) {
        return remarkRepository.countByEntry(entry);
    }
}
