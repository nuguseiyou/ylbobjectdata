
//同意实名认证协议
$(function() {
	//验证姓名格式是否正确
	$("#realName").on("blur", function () {
		var realName = $.trim($("#realName").val());
		if(realName === "" || realName === null || realName === undefined){
			showError("realName","姓名不能为空")
		}else if (realName.length < 2){
			showError("realName","姓名格式错误")
		}else if(!/^[\u4e00-\u9fa5]{0,}$/.test(realName)){
			showError("realName","姓名需为中文")
		}else {
			showSuccess("realName");
		}
	});
	//验证身份证格式
	$("#idCard").on("blur",function(){
		var idCard = $.trim($("#idCard").val());
		if(idCard === null || idCard === "" || idCard === undefined ){
			showError("idCard","身份证号不能为空")
		}else if(!/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(idCard)){
			showError("idCard","身份证格式不正确")
		}else{
			showSuccess("idCard")
		}
	});
	//认证
	$("#btnRegist").on("click",function(){
		$("#realName").blur();
		$("#idCard").blur();
		//属性选择器获取错误文本
		var errtext = $("[id $= 'Err']").text();
		if(errtext == ""){
			var phone = $.trim($("#phone").val());
			var realName = $.trim($("#realName").val());
			var idCard = $.trim($("#idCard").val());
			$.ajax({
				url: contextPath+"/user/realName",
				type: "post",
				data: {
					phone:phone,
					realName:realName,
					idCard:idCard
				},
				dataType:"json",
				success:function(response){
					//alert(response.msg)
					if(response.success ){
						window.location.href = contextPath+"/user/page/centre";
					}else{
						alert(response.msg)
					}
				},
				error:function(){
					alert("请稍后重试")
				}
			})
		}
	});

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