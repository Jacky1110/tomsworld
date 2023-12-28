package com.jotangi.tomsworld.entity;


/**
 * Created by carolyn on 2017/11/9.
 */

public class LogonInfo implements java.io.Serializable{
    private String MemberNo;
    private String MemberName;
    private String MemberType;
    private String MemberShipCardNo;

    public LogonInfo(String memberNo,  String memberName,String memberType, String memberShipCardNo) {

        MemberNo = memberNo;
        MemberName = memberName;
        MemberType = memberType;
        MemberShipCardNo = memberShipCardNo;
    }

    public String getMemberNo() {
        return MemberNo;
    }
    public void setMemberNo(String memberNo) {
        MemberNo = memberNo;
    }

    public String getMemberName() {
        return MemberName;
    }
    public void setMemberName(String memberName) {
        MemberName = memberName;
    }

    public String getMemberType() {
        return MemberType;
    }
    public void setMemberType(String memberType) {
        MemberType = memberType;
    }

    public String getMemberShipCardNo() { return MemberShipCardNo; }
    public void setMemberShipCardNo(String memberShipCardNo) {
        MemberShipCardNo = memberShipCardNo;
    }
}
