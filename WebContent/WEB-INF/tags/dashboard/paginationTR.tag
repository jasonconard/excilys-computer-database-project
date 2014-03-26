<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/common" %>

<tr>
	<td colspan="4" style="vertical-align:middle;">
			<c:if test="${lastPage!=-1}">
				<cm:url servlet="DashBoard" 
								pageNo="1"
								search="${page.filter}"
								order="${page.column}"
								dir="${page.direction}"
								icon="glyphicon glyphicon-fast-backward"/>
				<cm:url servlet="DashBoard" 
								pageNo="${lastPage}"
								search="${page.filter}"
								order="${page.column}"
								dir="${page.direction}"
								icon="glyphicon glyphicon-backward"/>
			</c:if>
			<c:if test="${lastPage==-1}">
				<span class="unclickable glyphicon glyphicon-fast-backward"></span>
				<span class="unclickable glyphicon glyphicon-backward"></span>
			</c:if>
			Page(${numero}/${nbPage}) 
			<c:if test="${nextPage!=-1}">
				<cm:url servlet="DashBoard" 
								pageNo="${nextPage}"
								search="${page.filter}"
								order="${page.column}"
								dir="${page.direction}"
								icon="glyphicon glyphicon-forward"/>
				<cm:url servlet="DashBoard" 
								pageNo="${nbPage}"
								search="${page.filter}"
								order="${page.column}"
								dir="${page.direction}"
								icon="glyphicon glyphicon-fast-forward"/>
			</c:if>
			<c:if test="${nextPage==-1}">
				<span class="unclickable glyphicon glyphicon-forward"></span>
				<span class="unclickable glyphicon glyphicon-fast-forward"></span>
			</c:if>
	</td>
	<td>	
		<form action="" class="form-inline" method="GET">
			<input type="number" id="searchbox" name="page" value="${numero}" placeholder="n°" min="1" max="${nbPage}"/> / ${nbPage} &nbsp;&nbsp;
			<input type="submit" id="pagesubmit" value="Go to page" class="btn btn-primary btn-xs"/>
			<input type="hidden" id="hiddenSearch" name="search" value="${page.filter}"/>
			<input type="hidden" id="hiddenOrder" name="order" value="${page.column}"/>
			<input type="hidden" id="hiddenDir" name="dir" value="${page.direction}"/>
		</form>				
	</td>
</tr>