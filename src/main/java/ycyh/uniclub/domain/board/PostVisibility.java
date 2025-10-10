package ycyh.uniclub.domain.board;

public enum PostVisibility {
    GROUP_ONLY("group_only"),
    PUBLIC("public");
    
    private final String value;
    
    PostVisibility(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}


