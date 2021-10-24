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


//打开注册协议弹层
function alertBox(maskid,bosid){
	$("#"+maskid).show();
	$("#"+bosid).show();
}
//关闭注册协议弹层
function closeBox(maskid,bosid){
	$("#"+maskid).hide();
	$("#"+bosid).hide();
}

//注册协议确认
$(function() {
	//手机号数据处理
	$("#phone").on("blur",function () {
		let phone = $.trim($("#phone").val());
		if(phone === null || phone== undefined || phone === ""){
			showError("phone","手机号不能为空")
		} else if( phone.length != 11){
			showError("phone","手机号必须是11位")
		} else if( !/^1[1-9]\d{9}$/.test(phone)){
			showError("phone","手机号格式不正确")
		} else{

			//成功
			showSuccess("phone")
			//ajax请求，检查手机号是否注册
			$.ajax({
				url: contextPath+"/user/regist/phone",
				async :false,
				data:{
					"phone":phone
				},
				dataType:"json",
				success:function(resp){
					console.log(resp);
					if( resp.success ){
						showSuccess("phone")
					} else {
						showError("phone",resp.msg)
					}
				},
				error:function(){
					alert("请求失败,请稍后重试");
				}
			})
		}
	})


	//密码的数据处理
	$("#loginPassword").on("blur",function(){
		let mima = $.trim( $("#loginPassword").val() )
		if( mima === null || mima === undefined || mima === ""){
			showError("loginPassword","必须输入密码")
		} else if( mima.length < 6 || mima.length> 20){
			showError("loginPassword","密码是6-20位")
		} else if( !/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(mima)){
			showError("loginPassword","密码内容是英文和数字混合")
		} else {
			showSuccess("loginPassword")
		}
	})

	//发送验证码按钮
	$("#messageCodeBtn").on("click",function(){
		//手机号数据处理
		$("#phone").blur()
		//判断数据处理是成功
		let errText = $.trim($("#phoneErr").text())
		if( errText == ""){
			let btn = $("#messageCodeBtn");
			if( !btn.hasClass("on")){
				//增加样式on
				btn.addClass("on")
				$.leftTime(5,function(d){
					//console.log("d.status="+ d.status+"======d.s="+d.s)
					let second = parseInt(d.s)
					if(second == 0 ){
						btn.text("获取验证码")
						btn.removeClass("on")
					} else{
						btn.text( second + "秒后获取")
					}
				})
				//给服务器发起请求，给指定的手机号发送短信验证码
				$.ajax({
					url:contextPath+"/sms/authcode",
					type:"post",
					data:{
						phone: $.trim($("#phone").val()),
						cmd:"regist"
					},
					dataType:"json",
					success:function(resp){
						if( resp.success == false ){
							showError("messageCode","请重新获取验证码")
						}
					},
					error:function(){
						alert("请求失败,请稍后重试");
					}
				})
			}
		}
	})


	//验证码文本框
	$("#messageCode").on("blur",function(){
		let code = $.trim( $("#messageCode").val() )
		if( code === undefined || code === null || code === ""){
			showError("messageCode","请输入验证码")
		} else if(code.length !=6){
			showError("messageCode","验证码格式不正确")
		} else {
			showSuccess("messageCode")
		}
	})

	//注册按钮click
	$("#btnRegist").on("click",function(){
		//检查数据
		$("#phone").blur()
		$("#loginPassword").blur()
		$("#messageCode").blur()

		//判断数据验证是否通过

		let errText = $("[id $='Err']").text();
		if(errText == ""){
			//数据正确，发起ajax注册请求， 注册成功后， 跳转到实名认证功能
			let phone = $.trim( $("#phone").val() )
			let mima = $.trim( $("#loginPassword").val() )
			let code = $.trim( $("#messageCode").val() )

			//密码要md5加密处理，在传递
			let mima_md5  = $.md5(mima)
			$.ajax({
				url:contextPath + "/user/register",
				type:"post",
				data:{
					phone:phone,
					mima:mima_md5,
					code:code
				},
				dataType:"json",
				success:function(resp){
					if( resp.success ){
						//进入到实名认证
						window.location.href=contextPath+"/user/page/realname"
					} else {
						//提示
						alert("注册提示："+resp.msg)
					}
				},
				error:function(){
					alert("请稍后重试")
				}
			})

		}


	})


	$("#agree").click(function(){
		var ischeck = document.getElementById("agree").checked;
		if (ischeck) {
			$("#btnRegist").attr("disabled", false);
			$("#btnRegist").removeClass("fail");
		} else {
			$("#btnRegist").attr("disabled","disabled");
			$("#btnRegist").addClass("fail");
		}
	});
});
