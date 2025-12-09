/**
 * chart.js
 * 图表分析页面相关的JS逻辑
 */

$(function() {
    // 初始化日期选择器
    $('#chartDateRangeStart, #chartDateRangeEnd').attr('type', 'date');
    $('#keyIndexDate').attr('type', 'date');

    let monthlyTrendChart, expensePieChart, incomePieChart; // ECharts 实例

    // 初始化 ECharts
    function initCharts() {
        monthlyTrendChart = echarts.init(document.getElementById('monthlyTrendChart'));
        expensePieChart = echarts.init(document.getElementById('expensePieChart'));
        incomePieChart = echarts.init(document.getElementById('incomePieChart'));

        // 绑定窗口大小改变事件，使图表自适应
        $(window).on('resize', function() {
            monthlyTrendChart.resize();
            expensePieChart.resize();
            incomePieChart.resize();
        });
    }

    // 1. 加载月度收支趋势图
    function loadMonthlyTrendData() {
        let startDate = $('#chartDateRangeStart').val();
        let endDate = $('#chartDateRangeEnd').val();
        if (!startDate || !endDate) return showMessage('请选择趋势图的日期范围', 'warning');

        sendAjaxRequest('/chart/api/monthlyTrend', 'GET', { startDate: startDate, endDate: endDate }, function(data) {
            renderMonthlyTrendChart(data);
        });
    }

    function renderMonthlyTrendChart(data) {
        let months = data.map(item => item.month);
        let incomes = data.map(item => item.income.toFixed(2));
        let expenses = data.map(item => item.expense.toFixed(2));
        let balances = data.map(item => item.balance.toFixed(2));

        let option = {
            title: { text: '月度收支趋势' },
            tooltip: { trigger: 'axis' },
            legend: { data: ['收入', '支出', '结余'] },
            grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
            xAxis: {
                type: 'category',
                boundaryGap: false,
                data: months
            },
            yAxis: {
                type: 'value',
                axisLabel: {
                    formatter: '¥{value}'
                }
            },
            series: [
                {
                    name: '收入',
                    type: 'line',
                    stack: '总量',
                    data: incomes,
                    itemStyle: { color: '#28a745' } // 绿色
                },
                {
                    name: '支出',
                    type: 'line',
                    stack: '总量',
                    data: expenses,
                    itemStyle: { color: '#dc3545' } // 红色
                },
                {
                    name: '结余',
                    type: 'line',
                    data: balances,
                    itemStyle: { color: '#007bff' } // 蓝色
                }
            ]
        };
        monthlyTrendChart.setOption(option);
    }

    // 2. 加载支出类别饼图
    function loadExpensePieData() {
        let startDate = $('#chartDateRangeStart').val();
        let endDate = $('#chartDateRangeEnd').val();
        if (!startDate || !endDate) return showMessage('请选择饼图的日期范围', 'warning');

        sendAjaxRequest('/chart/api/expenseCategoryPie', 'GET', { startDate: startDate, endDate: endDate }, function(data) {
            renderExpensePieChart(data);
        });
    }

    function renderExpensePieChart(data) {
        let option = {
            title: { text: '支出类别分布', left: 'center' },
            tooltip: {
                trigger: 'item',
                formatter: '{b} <br/>金额: ¥{c} ({d}%)'
            },
            legend: {
                orient: 'vertical',
                left: 'left',
                data: data.map(item => item.name)
            },
            series: [
                {
                    name: '支出类别',
                    type: 'pie',
                    radius: '50%',
                    center: ['50%', '60%'],
                    data: data,
                    emphasis: {
                        itemStyle: {
                            shadowBlur: 10,
                            shadowOffsetX: 0,
                            shadowColor: 'rgba(0, 0, 0, 0.5)'
                        }
                    },
                    label: {
                        formatter: '{b}: {c} ({d}%)'
                    }
                }
            ]
        };
        expensePieChart.setOption(option);
    }

    // 3. 加载收入类别饼图
    function loadIncomePieData() {
        let startDate = $('#chartDateRangeStart').val();
        let endDate = $('#chartDateRangeEnd').val();
        if (!startDate || !endDate) return showMessage('请选择饼图的日期范围', 'warning');

        sendAjaxRequest('/chart/api/incomeCategoryPie', 'GET', { startDate: startDate, endDate: endDate }, function(data) {
            renderIncomePieChart(data);
        });
    }

    function renderIncomePieChart(data) {
        let option = {
            title: { text: '收入类别分布', left: 'center' },
            tooltip: {
                trigger: 'item',
                formatter: '{b} <br/>金额: ¥{c} ({d}%)'
            },
            legend: {
                orient: 'vertical',
                left: 'left',
                data: data.map(item => item.name)
            },
            series: [
                {
                    name: '收入类别',
                    type: 'pie',
                    radius: '50%',
                    center: ['50%', '60%'],
                    data: data,
                    emphasis: {
                        itemStyle: {
                            shadowBlur: 10,
                            shadowOffsetX: 0,
                            shadowColor: 'rgba(0, 0, 0, 0.5)'
                        }
                    },
                    label: {
                        formatter: '{b}: {c} ({d}%)'
                    }
                }
            ]
        };
        incomePieChart.setOption(option);
    }

    // 4. 加载关键财务指标
    function loadKeyFinancialIndex() {
        let date = $('#keyIndexDate').val(); // 用于获取当月数据
        if (!date) date = formatDate(new Date()); // 默认当前日期
        sendAjaxRequest('/chart/api/keyIndex', 'GET', { date: date }, function(data) {
            $('#totalIncomeMonth').text('¥ ' + data.totalIncomeMonth.toFixed(2));
            $('#totalExpenseMonth').text('¥ ' + data.totalExpenseMonth.toFixed(2));
            $('#monthBalance').text('¥ ' + data.monthBalance.toFixed(2));
            $('#totalAsset').text('¥ ' + data.totalAsset.toFixed(2));
            $('#netAsset').text('¥ ' + data.netAsset.toFixed(2));
        });
    }

    // 绑定筛选按钮事件
    $('#loadChartsButton').click(function() {
        loadMonthlyTrendData();
        loadExpensePieData();
        loadIncomePieData();
    });

    $('#loadKeyIndexButton').click(function() {
        loadKeyFinancialIndex();
    });

    // 初始化加载
    if (window.location.pathname.startsWith('/chart/analysis')) {
        let today = new Date();
        let firstDayOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);
        let lastDayOfMonth = new Date(today.getFullYear(), today.getMonth() + 1, 0);

        $('#chartDateRangeStart').val(formatDate(firstDayOfMonth));
        $('#chartDateRangeEnd').val(formatDate(lastDayOfMonth));
        $('#keyIndexDate').val(formatDate(today));

        initCharts(); // 初始化ECharts实例
        loadMonthlyTrendData();
        loadExpensePieData();
        loadIncomePieData();
        loadKeyFinancialIndex();
    }
});