package ycyh.uniclub.domain.board.enums;

public enum BoardType {
    NOTICE("notice"),
    FREE("free"),
    QNA("qna");
    
    private final String value;
    
    BoardType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}

