package com.datasec.server;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobInQueue {
    protected Integer jobNumber;
    protected String jobFileName;

    public JobInQueue(Integer jobNumber, String jobFileName) {
        this.jobNumber = jobNumber;
        this.jobFileName = jobFileName;
    }
}
