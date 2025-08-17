package ject.petfit.domain.remark.service;

import jakarta.transaction.Transactional;
import ject.petfit.domain.entry.entity.Entry;
import ject.petfit.domain.remark.entity.Remark;
import ject.petfit.domain.remark.repository.RemarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Transactional
@RequiredArgsConstructor
@Service
public class RemarkCommandService {
    private final RemarkRepository remarkRepository;

    public Remark createRemark(Entry entry, String title, String content, LocalDateTime localDateTime) {
        return remarkRepository.save(Remark.builder()
                .entry(entry)
                .title(title)
                .content(content)
                .remarkDate(localDateTime)
                .build()
        );
    }

    public Remark updateRemark(Remark remark, String title, String content) {
        if(title != null && !title.isEmpty()) {
            remark.updateTitle(title);
        }
        if(content != null && !content.isEmpty()) {
            remark.updateContent(content);
        }
        return remark;
    }

    public void deleteRemark(Remark remark) {
        remarkRepository.delete(remark);
    }
}
