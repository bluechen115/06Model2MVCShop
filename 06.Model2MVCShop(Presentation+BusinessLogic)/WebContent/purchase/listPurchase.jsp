<%@ page language="java" contentType="text/html; charset=EUC-KR"
	pageEncoding="EUC-KR"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%-- <%
	Map<String,Object> map=(HashMap<String,Object>)request.getAttribute("map");
	Search search=(Search)request.getAttribute("search");
	Page resultPage=(Page)request.getAttribute("resultPage");
	

	List<User> userList=(ArrayList<User>)map.get("userList");
	List<Purchase> purchaseList=(ArrayList<Purchase>)map.get("purchaseList");
	
	String searchCondition=CommonUtil.null2str(search.getSearchCondition());
	String searchKeyword=CommonUtil.null2str(search.getSearchKeyword());
	
%> --%>

<html>
<head>
<title>구매 목록조회</title>

<link rel="stylesheet" href="/css/admin.css" type="text/css">

<script type="text/javascript">
	function fncGetPurchaseList(currentPage) {
		var str = "/listPurchase.do?";
		str = str + "currentPage=" + currentPage;
		location.href = str;
	}
	function fncGetList(currentPage) {
		document.getElementById("currentPage").value = currentPage;
		document.detailForm.submit();
	}
</script>
</head>

<body bgcolor="#ffffff" text="#000000">

	<div style="width: 98%; margin-left: 10px;">

		<form name="detailForm" action="/listPurchase.do" method="post">

			<table width="100%" height="37" border="0" cellpadding="0"
				cellspacing="0">
				<tr>
					<td width="15" height="37"><img src="/images/ct_ttl_img01.gif"
						width="15" height="37"></td>
					<td background="/images/ct_ttl_img02.gif" width="100%"
						style="padding-left: 10px;">
						<table width="100%" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td width="93%" class="ct_ttl01">구매 목록조회</td>
							</tr>
						</table>
					</td>
					<td width="12" height="37"><img src="/images/ct_ttl_img03.gif"
						width="12" height="37"></td>
				</tr>
			</table>

			<table width="100%" border="0" cellspacing="0" cellpadding="0"
				style="margin-top: 10px;">
				<tr>
					<td colspan="11">전체 ${resultPage.totalCount} 건수, 현재
						${resultPage.currentPage} 페이지</td>
				</tr>
				<tr>
					<td class="ct_list_b" width="100">No</td>
					<td class="ct_line02"></td>
					<td class="ct_list_b" width="150">회원ID</td>
					<td class="ct_line02"></td>
					<td class="ct_list_b" width="150">회원명</td>
					<td class="ct_line02"></td>
					<td class="ct_list_b">전화번호</td>
					<td class="ct_line02"></td>
					<td class="ct_list_b">배송현황</td>
					<td class="ct_line02"></td>
					<td class="ct_list_b">정보수정</td>
				</tr>
				<tr>
					<td colspan="11" bgcolor="808285" height="1"></td>
				</tr>


				<c:set var="i" value="0" />
				<c:forEach var="purchase" items="${map.purchaseList}">
					<c:set var="user" value="${map.userList[i]}" />
					<tr class="ct_list_pop">
						<td align="center">
							<a href="/getPurchase.do?tranNo=${purchase.tranNo}">${i+1}</a>
						</td>
						<td></td>
						<td align="left">
							<a href="/getUser.do?userId=${user.userId}">${user.userId}</a>
						</td>
						<td></td>
			
						<td align="left">${user.userName}</td>
						<td></td>

						<td align="left">${user.phone}</td>
						<td></td>
						<td align="left">현재 
						
						<c:forEach items="${map.codeList}" var="code">
							<c:if test="${purchase.tranCode eq code.code}">
								${code.codeName}
							</c:if>
						</c:forEach>
						상태 입니다.
						</td>
						<td></td>
						<td align="left">
							<c:if test="${purchase.tranCode=='3'}">
								<a href="/updateTranCode.do?tranNo=${purchase.tranNo}&tranCode=4">물건도착</a>
							</c:if>
							<c:if test="${purchase.tranCode=='2'}">
								<a href="/updateTranCode.do?tranNo=${purchase.tranNo}&tranCode=5">구매취소</a>
							</c:if>
						</td>
					</tr>
					
					<c:set var="i" value="${i+1}" />
				</c:forEach>

				<tr>
					<td colspan="11" bgcolor="D6D7D6" height="1"></td>
				</tr>

			</table>

			<table width="100%" border="0" cellspacing="0" cellpadding="0"
				style="margin-top: 10px;">
				<tr>
					<td align="center">
					<input type="hidden" id="currentPage" name="currentPage" value="" /> 
						<jsp:include page="../common/pageNavigator.jsp" /></td>
				</tr>
			</table>

			<!--  페이지 Navigator 끝 -->
		</form>

	</div>

</body>
</html>