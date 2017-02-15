package com.dragon4.owo.ar_trace.Model;

/**
 * Created by Mansu on 2017-02-14.
 */

public class TracePointer {
    private String buildingID;
    private String traceID;

    public TracePointer() {

    }

    public TracePointer(String buildingID, String traceID) {
        this.buildingID = buildingID;
        this.traceID = traceID;
    }

    public String getBuildingID() {
        return buildingID;
    }

    public void setBuildingID(String buildingID) {
        this.buildingID = buildingID;
    }

    public String getTraceID() {
        return traceID;
    }

    public void setTraceID(String traceID) {
        this.traceID = traceID;
    }
}
