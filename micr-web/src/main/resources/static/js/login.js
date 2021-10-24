//错误提示
function showError(id,msg) {
	$("#"+id+"Ok").hide();
	$("#"+id+"Err").html("<i></i><p>"+msg+"</p>");
	$("#"+id+"Err").show();
	$("#"+id).addClass("input-red");
}
//错误隐藏
function hideError(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id).removeClass("input-red");
}
//显示成功
function showSuccess(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id+"Ok").show();
	$("#"+id).removeClass("input-red");
}

$(function () {
	//验证手机号格式
	$("#phone").on("blur",function () {
		var phone = $.trim($("#phone").val());
		if (phone === "" || phone === undefined || phone === null) {
			showError("phone", "手机号不能为空");
		} else if (phone.length != 11) {
			showError("phone", "请输入11位的手机号")
		} else if (!/^1[1-9]\d{9}$/.test(phone)) {
			showError("phone", "输入的手机号格式不正确")
		} else {
			showSuccess("phone");
		}
	});
	//验证密码格式
	$("#loginPassword").on("blur",function () {
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
	//登录功能
	$("#loginBtn").on("click",function () {
		//参数验证
		$("#phone").blur();
		$("#loginPassword").blur();
		var errtext = $.trim($("[id $= 'Err']").text());
		if(errtext == ""){
			//获取参数
			var phone = $.trim($("#phone").val());
			var mima = $.trim($("#loginPassword").val());
			//将数据传给后端
			$.ajax({
				url: contextPath + "/user/login",
				type: "post",
				data:{
					phone: phone,
					mima: $.md5(mima)
				},
				dataType: "json",
				success: function (response) {
					if(response.success){
						window.location.href = $("#returnUrl").val();
					}else{
						alert(response.msg)
					}
				},
				error: function () {
					alert("请稍后重试")
				}
			})
		}

	})
});


