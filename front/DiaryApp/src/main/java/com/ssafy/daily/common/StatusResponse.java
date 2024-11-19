package com.ssafy.daily.common;

import lombok.Data;

@Data
public class StatusResponse {
    private int status;
    private String msg;

    public StatusResponse(int status, String msg){
        this.status = status;
        this.msg = msg;
    }
}
