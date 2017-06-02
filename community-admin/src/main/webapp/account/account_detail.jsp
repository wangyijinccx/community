<%@ page language="java" pageEncoding="UTF-8"
	contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<%@ include file="/common/meta.jsp"%>
<style type="text/css">
.logo_top_left {
	background-color: #428bca;
	width: auto;
	height: 46px;
	margin: 0 5px;
	border-radius: 3px;
	float: left;
	box-sizing: border-box;
	display: block;
	padding-left: 5px;
	padding-right: 5px;
}

.border-bottom {
	border-bottom: 1px solid #eee;
	margin-left: 0px;
	margin-right: 0px;
}

.padding-top-15 {
	padding-top: 15px;
}

.padding-15-tb {
	padding: 15px 0px 15px 0px;
}

r
.line-height-34 {
	line-height: 34px;
}

div.dataTables_info {
	float: left;
	padding-top: 4px;
}

.padding-5 {
	padding: 15px;
}
</style>
<script type="text/javascript">
	var sessionUserId = '${sessionUserId}';
	var currentUserId = '${userInfo.id}';
</script>
</head>

<body>
	<c:set var="currentNav" value="search"></c:set>
	<%@ include file="/common/nav.jsp"%>
	<main>
	<div class="container-fluid">
		<div class="row border-bottom padding-top-15 ">
			<div class="col-xs-12 text-left">
				<ol class="breadcrumb">
					<li><a href="#">查询</a></li>
					<li><a href="${pageContext.request.contextPath}/account/list">成员信息</a></li>

					<li><a class="active">详细信息</a></li>
				</ol>
			</div>
		</div>
		<div class="padding-top-15">
			<ul class="nav nav-tabs" id="myTab">
				<li class="active"><a href="#home">用户信息</a></li>
			</ul>
			<div class="tab-content">
				<form class="tab-pane active form-horizontal" id="home">
					<input id="user_id" name="id" type="hidden" value="${userInfo.id}" />
					<div class="form-group">
						<label for="username" class="col-xs-3  control-label">注册时间</label>
						<div id="createTime">
							<p class="form-control-static" id="p_createTime">
								<fmt:formatDate value="${userInfo.createTime}"
									pattern="yyyy-MM-dd HH:mm:ss" />
							</p>
						</div>
					</div>
					<div class="form-group">
						<label for="username" class="col-xs-3  control-label">ID：</label>
						<div class="" id="id">
							<p class="form-control-static" id="p_id">${userInfo.id}</p>
						</div>
					</div>
					<div class="form-group">
						<label for="username" class="col-xs-3  control-label">帐号：</label>
						<div class="col-xs-3" id="username">
							<p class="form-control-static" id="p_username">${userInfo.username}</p>
						</div>
					</div>
					<c:if
						test="${sessionUserId eq userInfo.pid or userInfo.level eq 4}">
						<div class="form-group">
							<label for="username" class="col-xs-3  control-label">密码：</label>
							<div class="col-xs-3">
								<p class="form-control-static">
									<button id="btn_resetPassword" type="button"
										class="btn btn-primary btn-xs">密码重置</button>
									(默认：123456)
								</p>
							</div>
						</div>
					</c:if>
					<div class="form-group">
						<label for="username" class="col-xs-3  control-label">身份：</label>
						<div class="col-xs-2" id="level">
							<c:choose>
								<c:when test="${userInfo.level eq 4}">
									<p class="form-control-static" id="p_level">vip账号管理员</p>
								</c:when>
								<c:when test="${userInfo.level eq 0}">
									<p class="form-control-static" id="p_level">运维人员</p>
								</c:when>
								<c:when test="${userInfo.level eq 1 or userInfo.level eq 2 or userInfo.level eq 3}">
									<p class="form-control-static" id="p_level">${userInfo.level}级推广员</p>
								</c:when>
							</c:choose>
						</div>
					</div>
					<div class="form-group">
						<label for="username" class="col-xs-3  control-label">公司：</label>
						<div class="col-xs-2" id="companyName">
							<p class="form-control-static" id="p_companyName">${userInfo.companyName}</p>
						</div>
					</div>
					<div class="form-group">
						<label for="username" class="col-xs-3  control-label">联系人：</label>
						<div class="col-xs-3" id="linkman">
							<p class="form-control-static" id="p_linkman">${userInfo.linkman}</p>
						</div>
					</div>
					<div class="form-group">
						<label for="username" class="col-xs-3  control-label">电话：</label>
						<div class="col-xs-3" id="phone">
							<p class="form-control-static" id="p_phone">${userInfo.phone}</p>
						</div>
					</div>
					<c:if test="${userInfo.level ne 4}">
						<div class="form-group">
							<label for="username" class="col-xs-3  control-label">上级ID：</label>
							<div class="col-xs-2" id="pid">
								<p class="form-control-static" id="p_pid">${userInfo.pid}</p>
							</div>
						</div>
					</c:if>
					<div class="form-group">
						<label for="username" class="col-xs-3  control-label">备注：</label>
						<div class="col-xs-6" id="comment">
							<p class="form-control-static" id="p_comment">${userInfo.comment}</p>
						</div>
					</div>
					<c:if
						test="${sessionUserId eq userInfo.pid or userInfo.level eq 4}">
						<div class="row padding-top-15">
							<div class="col-xs-3 text-right">
								<!--<button id="btn_resetAccount" type="button"
									class="btn btn-primary">账号重置</button>-->
							</div>
							<div class="col-xs-9">
								<button id="btn_edit" type="button" class="btn btn-primary">修改</button>
								<a href="list" id="btn_edit" type="button"
									class="btn btn-default">返回</a>
							</div>
						</div>
					</c:if>
				</form>
			</div>
		</div>
	</div>

	</main>
	<script>
		$(function() {
			$('#myTab a:first').tab('show');//初始化显示哪个tab 

			$('#myTab a').click(function(e) {
				e.preventDefault();//阻止a链接的跳转行为 
				$(this).tab('show');//显示当前选中的链接及关联的content 
			});

			var table2 = $('#datalist2').dataTable({
				"bAutoWidth" : false,
				"bPaginate" : true, //是否分页。
				"bServerSide" : true,
				"bProcessing" : true,
				"searching" : false,
				"bLengthChange" : true,
				"scrollCollapse" : true,
				//"scrollY": "400px",
				"pagingType" : "full_numbers",
				// "dom": '<"toolbar">frtip',
				// "sAjaxSource": "Handler.ashx?actionname=getdatalist2",
				"ajax" : {
					"url" : "${pageContext.request.contextPath}/search/data_get_exchange",
					"data" : function(d) {

					},
					"dataType" : "json",
					"method" : "POST"
				},
				"columns" : [ {
					"data" : "date"
				}, {
					"data" : "newuser"
				}, {
					"data" : "visitor"
				}, {
					"data" : "alluser"
				}, {
					"data" : "visitortouser"
				}

				],
				"oLanguage" : {
					"sUrl" : "${pageContext.request.contextPath}/thirdpart_framework/jquery-datatable/language/zh_CN.json"
				}
			});
			$('#btn_edit').on('click', edit);
			$('#btn_resetPassword').on('click', resetPassword);
			$('#btn_resetAccount').on('click', resetAccount);
			function edit() {
				edit_text('username');
				edit_text('companyName');
				edit_text('linkman');
				edit_text('phone');
				//edit_text('level');
				edit_text('pid');
				edit_text('comment');
				$(this).text('保存');
				$(this).unbind('click', edit);
				$(this).on('click', save);
			}
			function edit_text(element_id) {
				var element = $("#" + element_id);
				element.html("<input id='edit_" + element_id + "' name='" + element_id + "' type=\"text\" value=\"" + element.children('#p_' + element_id).text() + "\" class=\"form-control\"/>")
			}
			function edit_select() {
				var element = $("#address_area");
				element.empty().append("<select class=\"form-control\"  name=\"province\" id=\"province\"></select>");
				element.append(" <select class=\"form-control\"  name=\"city\" id=\"city\"><option value=\"\">请选择</option></select>");
				element.append(" <select class=\"form-control\"  name=\"area\" id=\"area\"><option value=\"\">请选择</option></select>");
				findAreaByParent(0, province, $('#hd_province').val());

				$('#province').on('change', function() {
					if (this.value != "") {
						findAreaByParent(this.value, city);
					} else {
						$('#city').empty().append('<option value="">请选择</option>')
					}
					$('#area').empty().append('<option value="">请选择</option>');
					$('#hd_province').val(this.value);

				});
				$('#city').on('change', function() {
					if (this.value != "") {
						findAreaByParent(this.value, area);
					} else {
						$('#area').empty().append('<option value="">请选择</option>')
					}
					;
					$('#hd_city').val(this.value);
				});
				$('#area').on('change', function() {
					$('#hd_area').val(this.value);
				});
				findAreaByParent($('#hd_province').val(), city, $('#hd_city').val());
				findAreaByParent($('#hd_city').val(), area, $('#hd_area').val());
			}
			function caneledit_select() {
				var provinceName = $('#province').find("option:selected").text();
				var cityName = $('#city').find("option:selected").text();
				var areaName = $('#area').find("option:selected").text();
				var element = $("#address_area");
				element.empty().html("<p class=\"form-control-static\" id=\"p_address_area\">" + provinceName + '-' + cityName + '-' + areaName + "</p>");
			}
			function cancel_edit(element_id) {
				var element = $("#" + element_id);
				element.html("<p class=\"form-control-static\" id=\"p_"+ element_id +"\">" + $("#edit_" + element_id).val() + "</p>");
			}
			function save() {
				$.ajax({
					type : "POST",
					url : '${pageContext.request.contextPath}/account/AccountUpdate',
					data : $('#home').serialize(),
					error : function(request) {
						alert("Connection error");
					},
					success : function(data) {
						var jsonData = JSON.parse(data);
						if (jsonData.status) {
							cancel_edit('username');
							cancel_edit('companyName');
							cancel_edit('linkman');
							cancel_edit('phone');
							//cancel_edit('level');
							cancel_edit('pid');
							cancel_edit('comment');
							$('#btn_edit').unbind('click', save);
							$('#btn_edit').on('click', edit);
							caneledit_select();
							$('#btn_edit').text('修改');
						}
						bootbox.alert({
							buttons : {
								list : {
									label : '返回列表',
									className : 'btn-myStyle'
								},
								ok : {
									label : '确定',
									className : 'btn-myStyle'
								}
							},
							message : jsonData.msg,
							callback : function(e) {
								if (e == "list") {
									window.location = "list"
								}
							},
							title : "提示",
						});
					}
				});
			}

			function resetPassword() {
				$.ajax({
					type : "POST",
					url : '${pageContext.request.contextPath}/admin/user/resetpassword',
					data : {
						id : $('#user_id').val()
					},
					error : function(request) {
						console.info(request);
						alert("Connection error");
					},
					success : function(data) {

						bootbox.alert({
							buttons : {
								list : {
									label : '返回列表',
									className : 'btn-myStyle'
								},
								ok : {
									label : '确定',
									className : 'btn-myStyle'
								}
							},
							message : JSON.parse(data).msg,
							callback : function(e) {
								if (e == "list") {
									window.location = "list"
								}
							},
							title : "提示",
						});
					}
				});
			}
			function resetAccount() {
				$.ajax({
					type : "POST",
					url : '${pageContext.request.contextPath}/account/resetaccount',
					data : {
						id : $('#user_id').val()
					},
					error : function(request) {
						console.info(request);
						alert("Connection error");
					},
					success : function(data) {
						bootbox.alert({
							buttons : {
								list : {
									label : '返回列表',
									className : 'btn-myStyle'
								},
								ok : {
									label : '确定',
									className : 'btn-myStyle'
								}
							},
							message : JSON.parse(data).msg,
							callback : function(e) {
								if (e == "list") {
									window.location = "list"
								}
							},
							title : "提示",
						});
					}
				});
			}
		});
		function findAreaByParent(pid, target, selectedValue) {
			$.ajax({
				type : "POST",
				url : '${pageContext.request.contextPath}/Area/findAreaByParent',
				data : {
					pid : pid
				},
				error : function(request) {
					console.inf('获取数据错误');
				},
				success : function(data) {
					var jsonObjectData = JSON.parse(data);

					var target_select = $(target);
					target_select.empty();
					target_select.append('<option value="">请选择</option>');
					$.each(jsonObjectData, function(i) {
						var item = jsonObjectData[i];
						target_select.append('<option value="'+ item.id +'">' + item.areaName + '</option>')
					});
					target_select.val(selectedValue);
				}
			});
		}
	</script>
</body>
</html>
