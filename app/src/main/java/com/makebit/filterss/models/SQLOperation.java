package com.makebit.filterss.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SQLOperation implements Serializable {
    @SerializedName("fieldCount")
    @Expose
    int fieldCount;

    @SerializedName("affectedRows")
    @Expose
    int affectedRows;

    @SerializedName("insertId")
    @Expose
    int insertId;

    @SerializedName("serverStatus")
    @Expose
    int serverStatus;

    @SerializedName("warningCount")
    @Expose
    int warningCount;

    @SerializedName("message")
    @Expose
    String message;

    @SerializedName("protocol41")
    @Expose
    boolean protocol41;

    @SerializedName("changedRows")
    @Expose
    int changedRows;

    public SQLOperation(){

    }

    public SQLOperation(int fieldCount, int affectedRows, int insertId, int serverStatus, int warningCount, String message, boolean protocol41, int changedRows) {
        this.fieldCount = fieldCount;
        this.affectedRows = affectedRows;
        this.insertId = insertId;
        this.serverStatus = serverStatus;
        this.warningCount = warningCount;
        this.message = message;
        this.protocol41 = protocol41;
        this.changedRows = changedRows;
    }

    public int getFieldCount() {
        return fieldCount;
    }

    public void setFieldCount(int fieldCount) {
        this.fieldCount = fieldCount;
    }

    public int getAffectedRows() {
        return affectedRows;
    }

    public void setAffectedRows(int affectedRows) {
        this.affectedRows = affectedRows;
    }

    public int getInsertId() {
        return insertId;
    }

    public void setInsertId(int insertId) {
        this.insertId = insertId;
    }

    public int getServerStatus() {
        return serverStatus;
    }

    public void setServerStatus(int serverStatus) {
        this.serverStatus = serverStatus;
    }

    public int getWarningCount() {
        return warningCount;
    }

    public void setWarningCount(int warningCount) {
        this.warningCount = warningCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isProtocol41() {
        return protocol41;
    }

    public void setProtocol41(boolean protocol41) {
        this.protocol41 = protocol41;
    }

    public int getChangedRows() {
        return changedRows;
    }

    public void setChangedRows(int changedRows) {
        this.changedRows = changedRows;
    }

    @Override
    public String toString() {
        return "SQLOperation{" +
                "fieldCount=" + fieldCount +
                ", affectedRows=" + affectedRows +
                ", insertId=" + insertId +
                ", serverStatus=" + serverStatus +
                ", warningCount=" + warningCount +
                ", message='" + message + '\'' +
                ", protocol41=" + protocol41 +
                ", changedRows=" + changedRows +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLOperation that = (SQLOperation) o;

        if (fieldCount != that.fieldCount) return false;
        if (affectedRows != that.affectedRows) return false;
        if (insertId != that.insertId) return false;
        if (serverStatus != that.serverStatus) return false;
        if (warningCount != that.warningCount) return false;
        if (protocol41 != that.protocol41) return false;
        if (changedRows != that.changedRows) return false;
        return message != null ? message.equals(that.message) : that.message == null;
    }

    @Override
    public int hashCode() {
        int result = fieldCount;
        result = 31 * result + affectedRows;
        result = 31 * result + insertId;
        result = 31 * result + serverStatus;
        result = 31 * result + warningCount;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (protocol41 ? 1 : 0);
        result = 31 * result + changedRows;
        return result;
    }
}
