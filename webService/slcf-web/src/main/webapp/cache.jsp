<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<title>善林财富缓存</title>
<script type="text/javascript">
	function to(url) {
		
		
		if(url === 'all') {
			var tmpUrl = $("#txtCacheName").val() + "/keys";
			$.ajax({
				url : "${ctx}/localCache/" + tmpUrl,
				type : "get",
				dataType:'json',
				async : false,
				error : function(request) {
				},
				success : function(data) {
					
						var objects = data;
						
						// 展示克隆行，若不展示则后面克隆的均不展示
						var tr = $("#cloneTr"); 
						tr.show();
						
						var clonedA = tr.clone();
						clonedA.children("td").each(function(inner_index){  
	                        
	                        //根据索引为每一个td赋值  
	                        switch(inner_index){  
	                              case(0):   
	                                 $(this).html("");  
	                                 break;  
	                              case(1):  
	                                 $(this).html("");  
	                                 break;                     
	                        }//end switch                          
	                     });//end children.each 
						
						// 先清空tbody
						if(objects.length > 0) {
							$("table tbody").empty();	
						}
						else {
							$("table tbody").empty();
							$('table tbody').append(clonedA);
						}		
						
						// 根据取到的数据生成行
						 $.each(objects, function(index,item){   
						
	                         //克隆tr，每次遍历都可以产生新的tr                              
	                         var clonedTr = clonedA.clone();  
	                         var _index = index;  
	                          
	                         //循环遍历cloneTr的每一个td元素，并赋值  
	                         clonedTr.children("td").each(function(inner_index){  
	                              
	                              //根据索引为每一个td赋值  
	                              switch(inner_index){  
	                                    case(0):   
	                                       $(this).html(_index + 1);  
	                                       break;  
	                                    case(1):  
	                                       $(this).html(item);  
	                                       break;                     
	                              }//end switch                          
	                           });//end children.each  
	                         
	                           // 在tbody中添加新生成的克隆行
	                           $('table tbody').append(clonedTr);
	                       });//end $each 
	                       // 隐藏克隆行
	                       tr.hide();		
				}
			});
		}
		else if(url === 'del'){
			var tmpUrl = $("#txtCacheName").val();
			$.ajax({
				cache : true,
				type : "post",
				url : "${ctx}/localCache/" + tmpUrl,
				dataType:'json',
				async : false,
				error : function(request) {
				},
				success : function(data) {
					alert('删除成功');
				}
			});
		}
		else if(url === 'readKey'){
			var tmpUrl = $("#txtCacheName").val() + "/" + $("#txtReadCacheKey").val();
			$.ajax({
				cache : true,
				type : "get",
				url : "${ctx}/localCache/" + tmpUrl,
				dataType:'json',
				async : false,
				error : function(request) {
				},
				success : function(data) {
					alert(data);
				}
			});
		}
		else if(url === 'delKey'){
			var tmpUrl = $("#txtCacheName").val() + "/" + $("#txtDeleteCacheKey").val();
			$.ajax({
				cache : true,
				type : "post",
				url : "${ctx}/localCache/" + tmpUrl,
				dataType:'json',
				async : false,
				error : function(request) {
				},
				success : function(data) {
					alert('删除成功');
				}
			});
		}
		else if(url === 'test'){
			$.ajax({
				cache : true,
				type : "get",
				url : "${ctx}/localCache/" + url,
				async : false,
				dataType:'json',
				error : function(request) {
				},
				success : function(data) {
					alert(data);
				}
			});
		}
		else if(url === 'cacheDevice' 
				|| url === 'loadLoanGroup'){
			$.ajax({
				cache : true,
				type : "post",
				url : "${ctx}/localCache/" + url,
				async : false,
				dataType:'json',
				error : function(request) {
				},
				success : function(data) {
					alert(data);
				}
			});
		}
	}
</script>
</head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<script type="text/javascript" src="${ctx }/static/script/jquery-1.7.2.min.js"></script>
<link rel="stylesheet" href="${ctx }/static/css/style.css" type="text/css" media="screen"/>
<body>
<div id="content">
	<h1>善林财富缓存</h1>	
	<form id="form">	
	 <div class="divfont">缓存名称:<input type="text" id="txtCacheName" value="cache1" />
	 			 <input type="button" id="bntReadCacheAll" value="获取所有key" onclick="to('all')" />
	             <input type="button" id="bntDeleteCacheAll" value="删除所有值" onclick="to('del')" /></div> 
	 <div class="divfont">Key值:<input type="text" id="txtReadCacheKey"/>
	             <input type="button" id="bntReadCacheKey" value="读取缓存值" onclick="to('readKey')" /></div>
	 <div class="divfont">Key值:<input type="text" id="txtDeleteCacheKey" />
	             <input type="button" id="bntDeleteCacheKey" value="删除缓存值" onclick="to('delKey')" /></div>             
	 <div class="divfont"><input type="button" id="bntTest" value="测试" onclick="to('test')" />
	 				      <input type="button" id="bntCache" value="缓存Device(慎用)" onclick="to('cacheDevice')" /></div>  
	 <div class="divfont"><input type="button" id="bntCache" value="重新加载一键出借队列" onclick="to('loadLoanGroup')" /></div>  				      

	</form>
	
	<table class="table1" id="table1">
	    <thead>  
	         <tr>  
	             <th scope="col" abbr="Starter" width="20%">序号</th>
				 <th scope="col" abbr="Starter" width="40%">键</th> 
				 <th scope="col" abbr="Starter" width="40%">值</th>                     
	         </tr>  
	     </thead>
	           
		<tbody>  
             <tr id="cloneTr">  
	              <td></td>  
	              <td></td> 
	              <td></td>            
            </tr>
        </tbody>  
	</table>
	</div>
</body>

</html>