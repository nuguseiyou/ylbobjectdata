ge//错误提示
function showError(id, msg) {
    $("#" + id + "Ok").hide();
    $("#" + id + "Err").html("<i></i><p>" + msg + "</p>");
    $("#" + id + "Err").show();
    $("#" + id).addClass("input-red");
}

//错误隐藏
function hideError(id) {
    $("#" + id + "Err").hide();
    $("#" + id + "Err").html("");
    $("#" + id).removeClass("input-red");
}

//显示成功
function showSuccess(id) {
    $("#" + id + "Err").hide();
    $("#" + id + "Err").html("");
    $("#" + id + "Ok").show();
    $("#" + id).removeClass("input-red");
}


//打开注册协议弹层
function alertBox(maskid, bosid) {
    $("#" + maskid).show();
    $("#" + bosid).show();
}

//关闭注册协议弹层
function closeBox(maskid, bosid) {
    $("#" + maskid).hide();
    $("#" + bosid).hide();
}

//注册协议确认
$(function () {
    //手机号验证处理
    $("#phone").on("blur", function () {
        var phone = $.trim($("#phone").val());
        if (phone === "" || phone === undefined || phone === null) {
            showError("phone", "手机号不能为空");
        } else if (phone.length != 11) {
            showError("phone", "请输入11位的手机号")
        } else if (!/^1[1-9]\d{9}$/.test(phone)) {
            showError("phone", "输入的手机号格式不正确")
        } else {
            //alert(contextPath);
            showSuccess("phone");
            $.ajax({
                url: contextPath + "/user/regist/phone",
                async: false,
                data: {
                    "phone": phone
                },
                dataType: "json",
                success: function (resp) {
                    console.log(resp);
                    if (resp.success) {
                        showSuccess("phone");
                    } else {
                        showError("phone", resp.msg)
                    }
                },
                error: function () {
                    alert("请求失败,请稍后重试");
                }
            })
        }
    });
    //密码验证处理
    $("#loginPassword").on("blur", function () {
        var loginPassword = $.trim($("#loginPassword").val());
        if (loginPassword === null || loginPassword === "" || loginPassword === undefined) {
            showError("loginPassword", "密码不能为空");
        } else if (loginPassword.length < 6 && loginPassword > 20) {
            showError("loginPassword", "请设置6-20位密码");
        } else if (!/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(loginPassword)) {
            showError("loginPassword", "密码应同时包含英文和数字");
        } else {
            showSuccess("loginPassword");
        }
    });
    //验证码样式处理
    $("#messageCodeBtn").on("click", function () {
        $("#phone").blur();
        var errTest = $("#phoneErr").text();
        if (errTest === "") {
            var mes = $("#messageCodeBtn");
            if (!mes.hasClass("on")) {
                //给按钮追加样式
                mes.addClass("on");
                $.leftTime(10, function (d) {
                    var second = parseInt(d.s);
                    if (second == 0) {
                        mes.text("获取验证码");
                        mes.removeClass("on");
                    } else {
                        mes.text(second + "秒后获取");
                    }
                });
                var phone = $.trim($("#phone").val());
                $.ajax({
                    url: contextPath + "/sms/authcode",
                    type: "post",
                    data: {
                        phone: phone,
                        cmd:"regist"
                    },
                    dataType: "json",
                    success: function (resp) {
                        if (resp.success == false) {
                            showError("messageCode", "请重新获取验证码");
                        }
                    },
                    error: function () {
                        alert("请求失败,请稍后再试");
                    }
                })
            }
        }
    });
    //验证码格式处理
    $("#messageCode").on("blur", function () {
        var messageCode = $.trim($("#messageCode").val());
        if (messageCode === "" || messageCode === undefined || messageCode === null) {
            showError("messageCode", "验证码不能为空")
        } else if (messageCode.length != 6) {
           showError("messageCode","请输入长度为六位数字的验证码")
        }else{
            showSuccess("messageCode")
        }
    });
    //检查页面所有数据 并发送ajsx请求
    $("#btnRegist").on("click",function(){
        $("#phone").blur();
        $("#loginPassword").blur();
        $("#messageCode").blur();
        //检查数据
        var text = $("[id $='Err']").text();
        if(text == ""){
            //取值
            var phone = $.trim($("#phone").val());
            var loginPassword = $.trim($("#loginPassword").val());
            var messageCode = $.trim($("#messageCode").val());
            //将密码进行第一次md5加密
            var mimaMd5 = $.md5(loginPassword);
            $.ajax({
                url: contextPath+"/user/register",
                type: "post",
                data: {
                    "phone": phone,
                    "mimaMd5": mimaMd5,
                    "messageCode": messageCode
                },
                dataType: "json",
                success: function (resp) {
                    //alert(resp.success + ","+resp.msg+","+resp.errcode)
                    if(resp.success){
                        window.location.href = contextPath +"/user/page/realName";
                    }else{
                        alert("错误提示=" + resp.msg);
                    }
                },
                error: function(){
                    alert("请稍后重试")
                }
            })
        }
    });


    $("#agree").click(function () {
        var ischeck = document.getElementById("agree").checked;
        if (ischeck) {
            $("#btnRegist").attr("disabled", false);
            $("#btnRegist").removeClass("fail");
        } else {
            $("#btnRegist").attr("disabled", "disabled");
            $("#btnRegist").addClass("fail");
        }
    });
});
