<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<style>

* {
	maring: 0;
	padding: 0;
}

body {
	font-family: 'Source Sans Pro', sans-serif;
	background: #436e90;
	color: #1f3759;
}

.sample-title {
	color: white;
	text-align: center;
	margin: 20px;
}

.sample-content {
	border: 1px solid gray;
	padding: 20px;
}

table {
	border: 1px solid #444444;
	border-collapse: collapse;
}

th, td {
	border: 1px solid #444444;
	padding: 10px;
	color: white;
}

#hidden {
	width: 100%;
	height: 100%;
	background-color: white;
	border: 0;
}
</style>

<script type="text/javascript">

	function savePeriod() {
		form = document.hiddenForm;
		console.log('savePeriod');
	
		form.invalue.value = document.getElementById("period").value;
		form.intype.value = "period";
		
		form.action="/gpms/samples/saveValue";
		form.target = "hiddenifr";
		form.submit();
	}
	
	function saveLimit() {
		form = document.hiddenForm;
		console.log('saveLimit');
		
		form.invalue.value = document.getElementById("limit").value;
		form.intype.value = "limit";
		
		form.action="/gpms/samples/saveValue";
		form.target = "hiddenifr";
		form.submit();
	}
	
	function runMigration() {
		form = document.hiddenForm;
		console.log('runMigration');
		
		if(document.getElementById("isForceCheck").checked) {
			form.isforce.value = "1";
		} else {
			form.isforce.value = "0";
		}

		form.action="/gpms/samples/createMigSampleProcess";
		form.target = "hiddenifr";
		form.submit();
	}
	
</script>
</head>


<body>
	<div class="sample-title">외부연동 샘플 실행 페이지</div>

	<div class="sample-content">
		<table>
			<tr>
				<td>실행주기</td>
				<td><input type="text" id="period" /></td>
				<td><button onClick="javascript:savePeriod()">주기저장</button></td>
			</tr>
			<tr>
				<td>임계치</td>
				<td><input type="text" id="limit" /></td>
				<td><button onClick="javascript:saveLimit()">임계치저장</button></td>
			</tr>

			<tr>
				<td>강제진행</td>
				<td><input type="checkbox" id="isForceCheck"></td>
				<td></td>
			</tr>

			<tr>
				<td colspan="3" style="text-align: right;"><button onClick="javascript:runMigration()">바로실행</button></td>
			</tr>

			<tr>
				<td>결과</td>
				<td><iframe id="hidden" name="hiddenifr"></iframe></td>
				<td></td>
			</tr>
			
		</table>
	</div>




</body>

<form name="hiddenForm" action="" method="GET">
	<input type="hidden" name="invalue" value="" />
	<input type="hidden" name="intype" value="" />
	<input type="hidden" name="isforce" value="" />
</form>

</html>