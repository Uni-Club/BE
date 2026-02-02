package ycyh.uniclub.domain.notification;

public enum NotificationType {

    COMMENT,              // 내 글에 댓글
    REPLY,                // 내 댓글에 대댓글
    MEMBER_APPROVED,      // 멤버 추가(승인)
    MEMBER_KICKED,        // 멤버 강제 퇴출
    LEAVE_REQUESTED,      // 탈퇴 신청(운영진 수신)
    LEAVE_APPROVED,       // 탈퇴 승인(신청자 수신)
    LEAVE_REJECTED,       // 탈퇴 거절(신청자 수신)
    APPLICATION_SUBMITTED, // 동아리 가입 신청(운영진 수신)
    SCHEDULE_CREATED,     // 일정 생성 (멤버 수신)
    SCHEDULE_UPDATED,     // 일정 변경 (멤버 수신)
    SCHEDULE_DELETED      // 일정 취소 (멤버 수신)
}
