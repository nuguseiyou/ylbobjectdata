$(function(){
	//手机号
	$("#phone").on("blur",function(){
		let phone = $.trim($("#phone").val());
		if(phone === null || phone== undefined || phone === ""){
			showError("phone","手机号不能为空")
		} else if( phone.length != 11){
			showError("phone","手机号必须是11位")
		} else if( !/^1[1-9]\d{9}$/.test(phone)) {
			showError("phone", "手机号格式不正确")
		} else {
			showSuccess("phone")
		}
	})

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

	//按钮单击
	$("#btnLogin").on("click",function(){
		//验证用户是否输入手机号， 密码值
		$("#phone").blur();
		$("#loginPassword").blur();

		let errText = $.trim( $("[ id $= 'Err']").text() );
		if( errText == ""){
			let phone = $.trim( $("#phone").val() )
			let mima = $.trim( $("#loginPassword").val() )
			$.ajax({
				url:contextPath + "/user/login",
				type:"post",
				data:{
					phone:phone,
					mima: $.md5(mima)
				},
				dataType:"json",
				success:function(res){
					if( res.success ){
						window.location.href = $("#returnUrl").val()
					} else {
						alert(res.msg)
					}
				},
				error:function(){
					alert("请求失败，稍后重试")
				}
			})
		}

	})

})

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