<!DOCTYPE html>
<!-- saved from url=(0025)http://si1n3rd.github.io/ -->
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Server statistic</title>
    <meta charset="utf-8">
    <style type="text/css">
        body {
            margin: 0;
            padding: 25px 0;
        }

        table {
            border-spacing: 1px;
            color: #000;
            width: 100%;
        }

        .main {
            margin: 0 auto;
            width: 1000px;
        }

        .main caption {
            background: #67CD89;
            font-weight: 700;
            padding: 5px;
        }

        .main__info {
            border-spacing: 0;
        }

        .main__info td {
            background: #67CD89;
            padding: 5px;
            text-align: center;
        }

        .main__info td > span {
            font-weight: 700;
        }

        .main__ip th, .main__url th, .main__logs th {
            background: #7382CB;
            padding: 5px;
        }

        .main__ip td, .main__url td, .main__logs td {
            background: #65ABC2;
            padding: 5px;
            text-align: center;
        }
    </style>
</head>
<body>
<table class="main">
    <tbody>
    <tr>
        <td>
            <table class="main__info">
                <tbody>
                <tr>
                    <td>Общее количество запросов: <span>${statistic.totalRequestCount}</span></td>
                    <td>Количество уникальных запросов: <span>${statistic.uniqueRequestCount}</span></td>
                    <td>Количество открытых соединений: <span>${statistic.connectionsCurrentlyOpened}</span></td>
                </tr>
                </tbody>
            </table>
        </td>
    </tr>
    <#if statistic.connectionsFromIpCount?has_content>
    <tr>
        <td>
            <table class="main__ip">
                <caption>Счетчик запросов на каждый IP</caption>
                <tbody>
                <tr>
                    <th>IP</th>
                    <th>Количество запросов</th>
                    <th>Время последнего запроса</th>
                </tr>

                    <#list statistic.connectionsFromIpCount?keys as ip>
                    <tr>
                        <td>${ip}</td>
                        <td>${statistic.connectionsFromIpCount[ip]}</td>                        
                        <td>${statistic.millisToDateString(statistic.ipLastConnectionTmeMap[ip])}</td>
                    </tr>
                    </#list>

                </tbody>
            </table>
        </td>
    </tr>
    </#if>
    <#if statistic.redirectToUrlCount?has_content>
    <tr>
        <td>
            <table class="main__url">
                <caption>Количество переадресаций по url'ам</caption>
                <tbody>
                <tr>
                    <th>URL</th>
                    <th>Количество переадресаций</th>
                </tr>
                    <#list statistic.redirectToUrlCount?keys as url>
                    <tr>
                        <td>${url}</td>
                        <td>${statistic.redirectToUrlCount[url]}</td>
                    </tr>
                    </#list>
                </tbody>
            </table>
        </td>
    </tr>
    </#if>
    <#if statistic.connectionLogsTable?has_content>
    <tr>
        <td>
            <table class="main__logs">
                <caption>Лог из 16 последних обработанных соединений</caption>
                <tbody>
                <tr>
                    <th>SRC IP</th>
                    <th>URI</th>
                    <th>Timestamp</th>
                    <th>Sent Bytes</th>
                    <th>Received bytes</th>
                    <th>Speed (bytes/sec)</th>
                </tr>

                    <#list statistic.connectionLogsTable as connectionLog>
                        <#if connectionLog??>
                        <tr>
                            <td>${connectionLog.ip}</td>
                            <#if connectionLog.url??>
                                <td>${connectionLog.url}</td>
                            <#else>
                                <td>null</td>
                            </#if>
                            <td>${statistic.millisToDateString(connectionLog.time)}</td>
                            <td>${connectionLog.receivedBytes}</td>
                            <td>${connectionLog.sentBytes}</td>
                            <td>${connectionLog.speed}</td>
                        </tr>
                        </#if>
                    </#list>
                </tbody>
            </table>
        </td>
    </tr>
    </#if>
    </tbody>
</table>

</body>
</html>
