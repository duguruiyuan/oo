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
<title>善林财富定时任务</title>
</head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<script type="text/javascript" src="${ctx }/static/script/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="${ctx }/static/script/index.js"></script>
<%-- <link rel="stylesheet" href="${ctx }/static/css/style.css" type="text/css" media="screen"/> --%>
<%-- <link rel="stylesheet" href="${ctx }/static/css/menu1.css" type="text/css" media="screen"/> --%>
<%-- <link rel="stylesheet" href="${ctx }/static/css/menu2.css" type="text/css" media="screen"/>  --%>
<link rel="stylesheet" href="${ctx }/static/css/menu3.css" type="text/css" media="screen"/>
<link rel="stylesheet" href="${ctx }/static/css/hint.css" type="text/css" media="screen"/>
<body>
<!-- 请输入日期：<input type="text" id="currentDate" value=""> -->
<!-- 表格样式 begin -->
<!-- <div id="content">
	<h1>善林财富后台定时任务</h1>
	
<table class="table1">
 <thead>
  <tr>
     <th scope="col" abbr="Starter" width="20%">公用</th>
     <th scope="col" abbr="Starter" width="20%">活期宝</th>
     <th scope="col" abbr="Starter" width="20%">定期宝</th>
     <th scope="col" abbr="Starter" width="20%">体验宝</th>
     <th scope="col" abbr="Starter" width="20%">其他</th>
  </tr>
  </thead>
  <tbody>
  <tr>
    <td><a href="#" onclick="to('OpenJob')">定时发标</a></td>
    <td><a href="#" onclick="to('DailySettlementJob')">活期宝每日结息</a></td>
    <td><a href="#" onclick="to('TermAtoneWithdrawJob')">定期宝到期赎回[1]</a></td>
    <td><a href="#" onclick="to('TYBDailySettlementJob')">体验宝每日结息</a></td>
    <td><a href="#" onclick="to('TYBRecommendedAwardsJob')">推荐奖励</a></td>
  </tr>
  <tr>
    <td><a href="#" onclick="to('CloseJob')">定时关标</a></td>
    <td><a href="#" onclick="to('AtoneJob');">赎回详情(已作废)</a></td>
    <td><a href="#" onclick="to('TermAtoneBuyJob')">定期宝公司回购[2]</a></td>
    <td><a href="#" onclick="to('TYBWithdrawJob')">体验宝到期赎回</a></td>
    <td><a href="#" onclick="to('OpenServiceNotifyJob')">定时对外通知</a></td>
  </tr>
  <tr>
    <td><a href="#" onclick="to('ComputeLoanPvJob')">债权价值计算</a></td>
    <td><a href="#" onclick="to('RecoverAtoneJob')">活期宝赎回回收</a></td>
    <td><a href="#" onclick="to('TermAtoneSettlementJob')">定期宝赎回到帐[3]</a></td>
    <td><a href="#" onclick="to('ExperienceWithdrawSendSmsJob')">体验金到期赎回短信</a></td>
    <td><a href="#" onclick="to('MendBankJob')">补全银行卡协议号</a></td>
  </tr>
  <tr>
    <td><a href="#" onclick="to('ComputeOpenValueJob')">可开放价值计算</a></td>
    <td>&nbsp;</td>
    <td><a href="#" onclick="to('TermDailySettlementJob')">定期宝结息</a></td>
    <td>&nbsp;</td>
    <td><a href="#" onclick="to('FinancialStatisticsJob')">财务统计还款列表</a></td>
  </tr>
  <tr>
    <td><a href="#" onclick="to('RepaymentJob')">还款计算</a></td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td><a href="#" onclick="to('GoldDailySettlementJob')">金牌推荐人活期宝奖励</a></td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td><a href="#" onclick="to('GoldWithdrawJob')">金牌推荐人活期宝结算</a></td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td><a href="#" onclick="to('GoldMonthlySettlement')">金牌推荐人定期宝奖励</a></td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td><a href="#" onclick="to('SumBusinessHistory')">数据总览(业务数据汇总)</a></td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
  </tr>
  </tbody>
  <tfoot> 
	  <tr>
	    <td>&nbsp;</td>
	    <td>&nbsp;</td>
	    <td>&nbsp;</td>
	    <td>&nbsp;</td>
	    <td>&nbsp;</td>
	  </tr>
  </tfoot>
</table>
</div> -->
<!-- 表格样式 end -->

<!-- menu1样式 begin -->
<!--  <div class="menuHolder">
	<div class="menuWindow">
		<ul class="p1">
			<li class="s1"><a href="#url">后台任务</a>
				<ul class="p2">
					<li class="s2"><a href="#url"><span>公用</span></a>
						<ul class="p3 a5">
							<li><a href="#" onclick="to('OpenJob')">定时发标</a></li>
							<li><a href="#" onclick="to('CloseJob')">定时关标</a></li>
							<li><a href="#" onclick="to('ComputeLoanPvJob')">债权价值计算</a></li>
                            <li><a href="#" onclick="to('ComputeOpenValueJob')">可开放价值计算</a></li>
                            <li><a href="#" onclick="to('RepaymentJob')">还款计算</a></li>
						</ul>
					</li>
					<li class="s2"><a href="#url"><span>活期宝</span></a>
						<ul class="p3 a3">
							<li><a href="#" onclick="to('DailySettlementJob')">每日结息</a></li>
							<li><a href="#" onclick="to('AtoneJob')">赎回详情(作废)</a></li>
							<li><a href="#" onclick="to('RecoverAtoneJob')">赎回回收</a></li>
						</ul>
					</li>
					<li class="s2"><a href="#url"><span>定期宝</span></a>
						<ul class="p3 a3">
                            <li><a href="#" onclick="to('TermAtoneWithdrawJob')">到期赎回</a></li>
							<li><a href="#" onclick="to('TermAtoneBuyJob')">公司回购</a></li>
							<li><a href="#" onclick="to('TermAtoneSettlementJob')">赎回到帐</a></li>
						</ul>
					</li>
					<li class="s2"><a href="#url"><span>体验宝</span></a>
						<ul class="p3 a3">
							<li><a href="#" onclick="to('TYBDailySettlementJob')">每日结息</a></li>
							<li><a href="#" onclick="to('TYBWithdrawJob')">到期赎回</a></li>
							<li><a href="#" onclick="to('ExperienceWithdrawSendSmsJob')">到期赎回短信</a></li>
						</ul>
					</li>
                    <li class="s2"><a href="#url"><span>金牌推荐人</span></a>
						<ul class="p3 a3">
							<li><a href="#" onclick="to('GoldDailySettlementJob')">活期宝奖励</a></li>
							<li><a href="#" onclick="to('GoldMonthlySettlement')">定期宝奖励</a></li>
							<li><a href="#" onclick="to('GoldWithdrawJob')">活期宝结算</a></li>
						</ul>
					</li>
					<li class="s2 b6"><a href="#url"><span>其他</span></a>
						<ul class="p3 a5">
							<li><a href="#" onclick="to('TYBRecommendedAwardsJob')">推荐奖励</a></li>
							<li><a href="#" onclick="to('OpenServiceNotifyJob')">定时对外通知</a></li>
							<li><a href="#" onclick="to('MendBankJob')">补全银行卡</a></li>
							<li><a href="#" onclick="to('FinancialStatisticsJob')">财务还款列表</a></li>
							<li><a href="#" onclick="to('SumBusinessHistory')">数据总览</a></li>
						</ul>
					</li>
				</ul>
			</li>
		</ul>
	</div>
</div>  -->
<!-- menu1样式end -->

<!-- menu2样式begin -->
<!--  <div class="container">
	<ul class="menu">
		<li><a href="#">公用</a>
			<ul class="submenu">
				<li><a href="#" onclick="to('OpenJob')">定时发标</a></li>
                <li><a href="#" onclick="to('CloseJob')">定时关标</a></li>
                <li><a href="#" onclick="to('ComputeLoanPvJob')">债权价值计算</a></li>
                <li><a href="#" onclick="to('ComputeOpenValueJob')">可开放价值计算</a></li>
                <li><a href="#" onclick="to('RepaymentJob')">还款计算</a></li>
			</ul>
		</li>
		<li class="active"><a href="#s2">活期宝</a>
			<ul class="submenu">
				<li><a href="#" onclick="to('DailySettlementJob')">每日结息</a></li>
                <li><a href="#" onclick="to('AtoneJob')">赎回详情(作废)</a></li>
                <li><a href="#" onclick="to('RecoverAtoneJob')">赎回回收</a></li>
			</ul>
		</li>
		<li><a href="#">定期宝</a>
			<ul class="submenu">
				<li><a href="#" onclick="to('TermAtoneWithdrawJob')">到期赎回</a></li>
                <li><a href="#" onclick="to('TermAtoneBuyJob')">公司回购</a></li>
                <li><a href="#" onclick="to('TermAtoneSettlementJob')">赎回到帐</a></li>
			</ul>
		</li>
		<li><a href="#">体验宝</a>
			<ul class="submenu">
				<li><a href="#" onclick="to('TYBDailySettlementJob')">每日结息</a></li>
                <li><a href="#" onclick="to('TYBWithdrawJob')">到期赎回</a></li>
                <li><a href="#" onclick="to('ExperienceWithdrawSendSmsJob')">到期赎回短信</a></li>
			</ul>
		</li>
        <li><a href="#">金牌推荐人</a>
			<ul class="submenu">
				<li><a href="#" onclick="to('GoldDailySettlementJob')">活期宝奖励</a></li>
                <li><a href="#" onclick="to('GoldMonthlySettlement')">定期宝奖励</a></li>
                <li><a href="#" onclick="to('GoldWithdrawJob')">活期宝结算</a></li>
			</ul>
		</li>
        <li><a href="#">其他</a>
			<ul class="submenu">
				<li><a href="#" onclick="to('TYBRecommendedAwardsJob')">推荐奖励</a></li>
                <li><a href="#" onclick="to('OpenServiceNotifyJob')">定时对外通知</a></li>
                <li><a href="#" onclick="to('MendBankJob')">补全银行卡</a></li>
                <li><a href="#" onclick="to('FinancialStatisticsJob')">财务还款列表</a></li>
                <li><a href="#" onclick="to('SumBusinessHistory')">数据总览</a></li>
			</ul>
		</li>
	</ul>
</div>  -->
<!-- menu2样式end -->

<!-- menu3样式begin -->
<ul id="accordion" class="accordion">
<!-- 		<li>
			<div class="link">公用<i class="fa fa-chevron-down"></i></div>
			<ul class="submenu">
				<li><a href="#" onclick="to('OpenJob')" class="hint--right" data-hint="每天8点至23点每个整点执行">定时发标</a></li>
                <li><a href="#" onclick="to('CloseJob')" class="hint--right" data-hint="每天23点30执行">定时关标</a></li>
                <li><a href="#" onclick="to('ComputeLoanPvJob')" class="hint--right" data-hint="每隔15分钟执行一次">债权价值计算</a></li>
                <li><a href="#" onclick="to('ComputeOpenValueJob')" class="hint--right" data-hint="每天7点至22点每个半点执行">可开放价值计算</a></li>
                <li><a href="#" onclick="to('RepaymentJob')" class="hint--right" data-hint="每天22点执行">还款计算</a></li>
			</ul>
		</li>
		<li>
			<div class="link">活期宝<i class="fa fa-chevron-down"></i></div>
			<ul class="submenu">
				<li><a href="#" onclick="to('DailySettlementJob')" class="hint--right" data-hint="每天凌晨1点执行">每日结息</a></li>
                <li><a href="#" onclick="to('AtoneJob')">赎回详情(作废)</a></li>
                <li><a href="#" onclick="to('RecoverAtoneJob')" class="hint--right" data-hint="每天23点45分执行">赎回回收</a></li>
			</ul>
		</li>
		<li>
			<div class="link">定期宝<i class="fa fa-chevron-down"></i></div>
			<ul class="submenu">
				<li><a href="#" onclick="to('TermAtoneWithdrawJob')" class="hint--right" data-hint="每天凌晨5点执行">到期赎回</a></li>
                <li><a href="#" onclick="to('TermAtoneBuyJob')" class="hint--right" data-hint="每天凌晨4点执行">公司回购</a></li>
                <li><a href="#" onclick="to('TermAtoneSettlementJob')" class="hint--right" data-hint="每天8点和20点执行">赎回到帐</a></li>
                <li><a href="#" onclick="to('TermDailySettlementJob')" class="hint--right" data-hint="每天凌晨3点执行">定期宝结息</a></li>
			</ul>
		</li>
		<li><div class="link">体验宝<i class="fa fa-chevron-down"></i></div>
			<ul class="submenu">
				<li><a href="#" onclick="to('TYBDailySettlementJob')" class="hint--right" data-hint="每天凌晨1点执行">每日结息</a></li>
                <li><a href="#" onclick="to('TYBWithdrawJob')" class="hint--right" data-hint="每天凌晨3点执行">到期赎回</a></li>
                <li><a href="#" onclick="to('ExperienceWithdrawSendSmsJob')" class="hint--right" data-hint="每天9点执行">到期赎回短信</a></li>
			</ul>
		</li> -->
		<li>
			<div class="link">公用<i class="fa fa-chevron-down"></i></div>
			<ul class="submenu">
                <li><a href="#" onclick="to('ComputeLoanPvJob')" class="hint--right" data-hint="每隔15分钟执行一次">债权价值计算</a></li>
			</ul>
		</li>
		<li><div class="link">企业借款<i class="fa fa-chevron-down"></i></div>
			<ul class="submenu">
				<li><a href="#" onclick="to('autoPublishProject')" class="hint--right" data-hint="每天8点至23点每个整点和半点执行">定时发布</a></li>
                <li><a href="#" onclick="to('autoReleaseProject')" class="hint--right" data-hint="每天1点和17点执行">定时生效</a></li>
                <li><a href="#" onclick="to('autoUnReleaseProject')" class="hint--right" data-hint="每天20点10分执行">定时流标</a></li>
                <li><a href="#" onclick="to('autoCompensateProject')" class="hint--right" data-hint="每天0点30分执行">定时贴息</a></li>
                <li><a href="#" onclick="to('autoRepaymentProject')" class="hint--right" data-hint="每天8点至22点每个整点执行">定时还款</a></li>
                <li><a href="#" onclick="to('autoRiskRepaymentProject')" class="hint--right" data-hint="每天22点30分执行">定时垫付</a></li>
                <li><a href="#" onclick="to('autoAuditRefuseProject')" class="hint--right" data-hint="每天23点30分执行">定时拒绝</a></li>
			</ul>
		</li>
		<li><div class="link">优选计划<i class="fa fa-chevron-down"></i></div>
			<ul class="submenu">
				<li><a href="#" onclick="to('autoPublishWealth')" class="hint--right" data-hint="每天8点至23点每个整点和半点执行">定时发布</a></li>
                <li><a href="#" onclick="to('autoReleaseWealth')" class="hint--right" data-hint="每天8点至23点每个整点执行">定时生效</a></li>
                <li><a href="#" onclick="to('autoUnReleaseWealth')" class="hint--right" data-hint="每天23点执行">定时流标</a></li>
                <li><a href="#" onclick="to('autoMatchLoan')" class="hint--right" data-hint="每天8点至23点每个10分执行">定时撮合</a></li>
                <li><a href="#" onclick="to('autoRepaymentWealth')" class="hint--right" data-hint="每天22点执行">定时还款</a></li>
                <li><a href="#" onclick="to('autoRecoveryWealth')" class="hint--right" data-hint="每天1点执行">月返息</a></li>
                <li><a href="#" onclick="to('autoAtoneWealth')" class="hint--right" data-hint="每天3点执行">到期赎回和提前赎回</a></li>   
			</ul>
		</li>
		<li><div class="link">优选项目<i class="fa fa-chevron-down"></i></div>
			<ul class="submenu">
                <li><a href="#" onclick="to('loanUnReleaseJob')" class="hint--right" data-hint="每天23点执行">定时流标</a></li>
                <li><a href="#" onclick="to('loanRepaymentJob')" class="hint--right" data-hint="每天22点执行">定时还款</a></li>
                
                <li><a href="#" onclick="to('loanGrantJob')" class="hint--right" data-hint="每小时整点执行">定时放款</a></li>
                <li><a href="#" onclick="to('loanGrantYZJob')" class="hint--right" data-hint="每2分钟执行">意真定时放款</a></li>
				<li><a href="#" onclick="to('loanGrantJLJob')" class="hint--right" data-hint="每2分钟执行">巨涟定时放款</a></li>
				<li><a href="#" onclick="to('loanGrantCFJob')" class="hint--right" data-hint="每2分钟执行">财富现金贷定时放款</a></li>
                <li><a href="#" onclick="to('loanGrantConfirmJob')" class="hint--right" data-hint="每小时半点执行">定时放款确认</a></li>
                <li><a href="#" onclick="to('loanGrantConfirmYZJob')" class="hint--right" data-hint="每3分钟执行">意真定时放款确认</a></li>
				<li><a href="#" onclick="to('loanGrantConfirmJLJob')" class="hint--right" data-hint="每3分钟执行">巨涟定时放款确认</a></li>
                <li><a href="#" onclick="to('loanGrantConfirmCFJob')" class="hint--right" data-hint="每3分钟执行">财富现金贷定时放款确认</a></li>
                <li><a href="#" onclick="to('loanCancelJob')" class="hint--right" data-hint="每天8点至23点每个整点执行">定时撤销债权转让</a></li>
                <li><a href="#" onclick="to('loanPublishJob')" class="hint--right" data-hint="每10分钟执行">定时发布</a></li>
                <li><a href="#" onclick="to('autoTransfer')" class="hint--right" data-hint="每天凌晨0点执行">自动转让</a></li>
                <li><a href="#" onclick="to('autoPublishJobService')" class="hint--right" data-hint="满标后触发">自动发布</a></li>
                <li><a href="#" onclick="to('autoInvest')" class="hint--right" data-hint="有新标发布（包括转让审核通过的标的）时调用">智能投顾</a></li>
			</ul>
		</li>
		<li><div class="link">业绩报表<i class="fa fa-chevron-down"></i></div>
			<ul class="submenu">
				<li><a href="#" onclick="to('autoMonthlyWealth')" class="hint--right" data-hint="每天1点执行">生成业务报表(线下)</a></li>
				<li><a href="#" onclick="to('goldCommisionJob')" class="hint--right" data-hint="每天8点至23点每个整点执行">计算佣金(线上)</a></li>
                <li><a href="#" onclick="to('commisionWithdrawJob')" class="hint--right" data-hint="每天1点执行">佣金到期处理(线上)</a></li>
                <li><a href="#" onclick="to('calcPerformanceJob')" class="hint--right" data-hint="每月1号1点执行">计算业绩(新SOP)</a></li>
                <li><a href="#" onclick="to('calcCommissionJob')" class="hint--right" data-hint="手动执行">计算佣金(新SOP)</a></li>
			</ul>
		</li>
		<li><div class="link">线下业务<i class="fa fa-chevron-down"></i></div>
			<ul class="submenu">
				<li><a href="#" onclick="to('handleOriginalData')" class="hint--right" data-hint="每天1点执行">花名册处理</a></li>
				<li><a href="#" onclick="to('synchronizeCustInfoAndInvestInfoFromWealthToSlcf')" class="hint--right" data-hint="每天4点">同步线下理财系统</a></li>
				<li><a href="#" onclick="to('offLineTransferToSLFinanceJob')" class="hint--right" data-hint="每天4点">线下客户转为线上客户</a></li>
			</ul>
		</li>
		
		<li><div class="link">平台数据统计<i class="fa fa-chevron-down"></i></div>
			<ul class="submenu">
				<li><a href="#" onclick="to('timeTaskDay')" class="hint--right" data-hint="每天0点执行">每日统计定时任务</a></li>
				<li><a href="#" onclick="to('timeTaskMonth')" class="hint--right" data-hint="每月最后一天0点执行">每月统计定时任务</a></li>
				
			</ul>
		</li>
		
		<li><div class="link">过期红包状态更新<i class="fa fa-chevron-down"></i></div>
			<ul class="submenu">
				<li><a href="#" onclick="to('autoExpire')" class="hint--right" data-hint="每天0点执行">每日更新过期红包定时任务</a></li>
				
			</ul>
		</li>
		
		<li><div class="link">其他<i class="fa fa-chevron-down"></i></div>
			<ul class="submenu">
				<li><a href="#" onclick="to('MendBankJob')" class="hint--right" data-hint="每天22点执行">补全银行卡协议号</a></li>
				<!-- <li><a href="#" onclick="to('waitingAuditSendMessage')" class="hint--right" data-hint="每5分钟执行">线下审核数据监控</a></li>
				<li><a href="#" onclick="to('monitorPosOnOnlineAndOffline')" class="hint--right" data-hint="每5分钟执行">POS单据监控</a></li> -->
				<li><a href="#" onclick="to('leaveJob')" class="hint--right" data-hint="每天凌晨1点执行">业务员离职</a></li>
				<td><a href="#" onclick="to('OpenServiceNotifyJob')">定时对外通知</a></td>
				<li><a href="#" onclick="to('dailyDataSummary')" class="hint--right" data-hint="每天下午5点整执行一次">平台每日数据汇总发送邮件</a></li>
				<li><a href="#" onclick="to('dailyPushDataSummary')" class="hint--right" data-hint="每天凌晨1点执行">平台每日推送数据汇总发送邮件</a></li>
				<li><a href="#" onclick="to('sendRepayment')" class="hint--right" data-hint="每天下午5点半整执行一次">发送手动还款请求</a></li>
				<li><a href="#" onclick="to('batchAuditPass4NM')" class="hint--right" data-hint="测试使用">拿米借款标的批量审核通过、发布</a></li>
				<li><a href="#" onclick="to('batchAuditPass4YZ')" class="hint--right" data-hint="测试使用">意真借款标的批量审核通过、发布</a></li>
				<li><a href="#" onclick="to('batchAuditPass4JL')" class="hint--right" data-hint="测试使用">巨涟借款标的批量审核通过、发布</a></li>
				<li><a href="#" onclick="to('batchAuditPass4CF')" class="hint--right" data-hint="测试使用">财富现金贷借款标的批量审核通过、发布</a></li>
				<li><a href="#" onclick="to('dailyDataYZloanAccountSummary')" class="hint--right" data-hint="测试使用">意真放款统计发送邮件</a></li>
				<li><a href="#" onclick="to('activityId13Job')" class="hint--right" data-hint="2017.6.15到2017.7.15每一个小时执行一次">2017-6月市场部活动奖励统计</a></li>
				<li><a href="#" onclick="to('autoReserveCancel')" class="hint--right" data-hint="每一个小时执行一次">定时预约投标撤销</a></li>
				<li><a href="#" onclick="to('expLoanJob')" class="hint--right" data-hint="每天8点12点执行">体验金定时任务</a></li>
			</ul>
		</li>
		<li><div class="link">条件<i class="fa fa-chevron-down"></i></div>
			<ul class="submenu">
				<li><a href="#" class="hint--right" data-hint="本功能仅对月返息开放（用于指定返息日期），此处输入完日期后点击优选计划——>月返息"><input type="text" id="currentDate" value="" />(格式yyyyMMdd)</a></li>
				<li><a href="index.html" class="hint--right" data-hint="查看定时任务详细描述" target="_blank">定时任务描述</a></li>
			</ul>
		</li>
		
<!--         <li><div class="link">金牌推荐人<i class="fa fa-chevron-down"></i></div>
			<ul class="submenu">
				<li><a href="#" onclick="to('GoldDailySettlementJob')" class="hint--right" data-hint="每天凌晨1点执行">活期宝奖励</a></li>
                <li><a href="#" onclick="to('GoldMonthlySettlement')" class="hint--right" data-hint="每天凌晨1点执行">定期宝奖励</a></li>
                <li><a href="#" onclick="to('GoldWithdrawJob')" class="hint--right" data-hint="每天凌晨2点15分执行">活期宝结算</a></li>
			</ul>
		</li>
        <li><div class="link">其他<i class="fa fa-chevron-down"></i></div>
			<ul class="submenu">
				<li><a href="#" onclick="to('TYBRecommendedAwardsJob')">推荐奖励</a></li>
                <li><a href="#" onclick="to('OpenServiceNotifyJob')" class="hint--right" data-hint="每隔5分钟执行一次">定时对外通知</a></li>
                <li><a href="#" onclick="to('MendBankJob')" class="hint--right" data-hint="每天20点执行">补全银行卡</a></li>
                <li><a href="#" onclick="to('FinancialStatisticsJob')" class="hint--right" data-hint="每天8点执行">财务还款列表</a></li>
                <li><a href="#" onclick="to('SumBusinessHistory')" class="hint--right" data-hint="每分钟执行一次">数据总览</a></li>
			</ul>
		</li> -->
	</ul>
<!-- menu3样式end -->
</body>
<script type="text/javascript">
	function to(url) {
		$.ajax({
			cache : true,
			type : "post",
			url : "${ctx}/job/" + url,
			contentType:"application/json",              
	    	dataType:"json",  
	        data:JSON.stringify({currentDate:$('#currentDate').val()}), 
			async : false,
			error : function(request) {
				notie.alert(3, '连接超时或者网络出错', 2.5);
			},
			success : function(data) {
				if (data.result.success) {
					notie.alert(1, '操作成功!', 2);
				} else {
					notie.alert(3, "请求失败:" + data.result.message, 2.5);
				}
			}
		});
	}
</script>
<script type="text/javascript" src="${ctx }/static/script/notie.js"></script>
</html>