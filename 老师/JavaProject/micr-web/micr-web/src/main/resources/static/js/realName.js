
//同意实名认证协议
$(function() {
	//姓名处理
	$("#realName").on("blur",function(){
		let name = $.trim( $("#realName").val() )
		if( name === undefined || name === null || name==""){
			showError("realName","请输入姓名")
		} else if( name.length < 2 ){
			showError("realName","姓名至少是2个字符")
		} else if( !/^[\u4e00-\u9fa5]{0,}$/.test(name)){
			showError("realName","姓名必须是中文")
		} else {
			showSuccess("realName")
		}
	})

	//身份证
	$("#idCard").on("blur",function(){
		let card = $.trim( $("#idCard").val() )
		if( card === undefined || card === null || card === ""){
			showError("idCard","必须输入身份证号")
		} else if( !/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(card)){
			showError("idCard","身份证号格式不正确")
		} else {
			showSuccess("idCard")
		}
	})

	//认证按钮
	$("#btnRegist").on("click",function(){
		//对姓名，身份证号做处理
		$("#realName").blur()
		$("#idCard").blur()

		//获取错误提示文本
		let errText = $("[id $= 'Err']").text()
		if( errText == ""){

			//发起ajax执行认证逻辑
			let name = $.trim( $("#realName").val() )
			let card = $.trim( $("#idCard").val() )
			let phone = $.trim( $("#phone").val() )

			$.ajax({
				url:contextPath+"/user/realname",
				type:"post",
				data:{
					name:name,
					idcard:card,
					phone:phone
				},
				dataType:"json",
				success:function(resp){
					if( resp.success ){
						window.location.href = contextPath + "/user/page/mycenter"
					} else {
						alert(resp.msg)
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