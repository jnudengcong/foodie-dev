package com.cong.pojo.bo.center;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class CenterUserBO {

    @ApiModelProperty(value = "用户名", name = "username", example = "cong", required = false)
    private String username;

    @ApiModelProperty(value = "密码", name = "password", example = "123456", required = false)
    private String password;

    @ApiModelProperty(value = "确认密码", name = "confirmPassword", example = "123456", required = false)
    private String confirmPassword;

    @ApiModelProperty(value = "用户昵称", name = "nickname", example = "杰森", required = false)
    private String nickname;

    @ApiModelProperty(value = "真实姓名", name = "realname", example = "杰森", required = false)
    private String realname;

    @ApiModelProperty(value = "手机号", name = "mobile", example = "13999999999", required = false)
    private String mobile;

    @ApiModelProperty(value = "邮箱", name = "email", example = "123@qq.com", required = false)
    private String email;

    @ApiModelProperty(value = "性别", name = "sex", example = "0：女， 1：男， 2：保密", required = false)
    private Integer sex;

    @ApiModelProperty(value = "生日", name = "birthday", example = "1900-01-01", required = false)
    private Date birthday;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
