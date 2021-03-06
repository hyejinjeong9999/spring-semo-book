package com.semobook.notice.service;

import com.semobook.common.StatusEnum;
import com.semobook.notice.domain.Notice;
import com.semobook.notice.dto.NoticeDto;
import com.semobook.notice.dto.NoticeResponse;
import com.semobook.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {
    private final NoticeRepository noticeRepository;

    private String hMessage = null;
    private StatusEnum hCode = null;
    /**
     * Notice findAll(전체 데이터 검색)
     *
     * @author juhan
     * @since 2021-07-17
    **/
    public NoticeResponse findAll(int pageNum){
        log.info(":: NoticeService_findAll :: pageNum is {} ", pageNum);
        hMessage = null;
        hCode = null;
        List<NoticeDto> result = null;
        try{
            Pageable pageAndTime = PageRequest.of(pageNum, 10, Sort.Direction.DESC,"createTime");
            Page<Notice> list = noticeRepository.findAll(pageAndTime);

            result = list.stream()
                    .map(n -> new NoticeDto(n))
                    .collect(Collectors.toList());
            hCode = StatusEnum.hd1004;
            hMessage = "공지사항 조회 성공";

        }catch (Exception e){
            hCode = StatusEnum.hd4444;
            hMessage = "공지사항 조회 실패";
        }

        return NoticeResponse.builder()
                .data(result)
                .hCode(hCode)
                .hMessage(hMessage)
                .build();
    }
}
