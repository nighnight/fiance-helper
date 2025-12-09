/**
 * record.js
 * 收支记录页面相关的JS逻辑
 */

let currentPageData = []; // 存储当前页面加载的记录数据

$(function() {
    // 初始化日期选择器 (如果使用jQuery UI Datepicker 或其他库，需要额外引入)
    $('#startDate, #endDate, #recordDate').attr('type', 'date');

    // 1. 加载筛选条件
    function loadFilterOptions() {
        // 加载账户列表
        sendAjaxRequest('/account/api/all', 'GET', null, function(accounts) {
            let accountSelect = $('#filterAccountId, #accountId');
            accountSelect.empty().append('<option value="">所有账户</option>');
            if (accounts && accounts.length > 0) {
                accounts.forEach(function(account) {
                    accountSelect.append(`<option value="${account.id}">${account.accountName} (${account.accountType})</option>`);
                });
            }
        });

        // 加载收入类别
        sendAjaxRequest('/category/api/all/1', 'GET', null, function(incomeCategories) {
            let categorySelect = $('#incomeCategoryId'); // 收入表单用
            categorySelect.empty().append('<option value="">请选择类别</option>');
            if (incomeCategories && incomeCategories.length > 0) {
                incomeCategories.forEach(function(category) {
                    categorySelect.append(`<option value="${category.id}">${category.categoryName}</option>`);
                });
            }
            // 筛选条件中的类别下拉会根据筛选类型动态加载，先留空
            // $('#filterCategoryId').empty().append('<option value="">所有类别</option>');
        });

        // 加载支出类别
        sendAjaxRequest('/category/api/all/2', 'GET', null, function(expenseCategories) {
            let categorySelect = $('#expenseCategoryId'); // 支出表单用
            categorySelect.empty().append('<option value="">请选择类别</option>');
            if (expenseCategories && expenseCategories.length > 0) {
                expenseCategories.forEach(function(category) {
                    categorySelect.append(`<option value="${category.id}">${category.categoryName}</option>`);
                });
            }
        });
    }

    // 根据筛选类型 (收入/支出) 动态加载类别下拉框
    $('#filterType').change(function() {
        let type = $(this).val();
        let categorySelect = $('#filterCategoryId');
        categorySelect.empty().append('<option value="">所有类别</option>');
        if (type) {
            sendAjaxRequest('/category/api/all/' + type, 'GET', null, function(categories) {
                if (categories && categories.length > 0) {
                    categories.forEach(function(category) {
                        categorySelect.append(`<option value="${category.id}">${category.categoryName}</option>`);
                    });
                }
            });
        }
    });

    // 2. 加载收支记录列表
    function loadRecords() {
        let startDate = $('#startDate').val();
        let endDate = $('#endDate').val();
        let type = $('#filterType').val() || null;
        let categoryId = $('#filterCategoryId').val() || null;
        let accountId = $('#filterAccountId').val() || null;

        let queryParams = {
            startDate: startDate,
            endDate: endDate,
            type: type,
            categoryId: categoryId,
            accountId: accountId
        };
        // 移除值为null或空字符串的参数
        for (let key in queryParams) {
            if (queryParams[key] === null || queryParams[key] === '') {
                delete queryParams[key];
            }
        }

        sendAjaxRequest('/record/api/list', 'GET', queryParams, function(records) {
            currentPageData = records; // 存储原始数据
            renderRecordsTable(records);
        });
    }

    // 渲染表格
    function renderRecordsTable(records) {
        let tbody = $('#recordTableBody');
        tbody.empty();
        if (records && records.length > 0) {
            records.forEach(function(record) {
                let row = `
                    <tr data-id="${record.id}">
                        <td>${formatDate(record.recordDate)}</td>
                        <td><span class="${record.type === 1 ? 'text-success' : 'text-danger'}">${record.typeName}</span></td>
                        <td>${record.categoryName}</td>
                        <td>${record.accountName}</td>
                        <td>¥ ${record.amount.toFixed(2)}</td>
                        <td>${record.remark || '无'}</td>
                        <td class="action-buttons">
                            <button class="btn btn-sm btn-info view-btn" data-id="${record.id}">查看/编辑</button>
                            <button class="btn btn-sm btn-danger delete-btn" data-id="${record.id}">删除</button>
                        </td>
                    </tr>
                `;
                tbody.append(row);
            });
        } else {
            tbody.append('<tr><td colspan="7" class="text-center">暂无记录</td></tr>');
        }
    }

    // 绑定筛选按钮事件
    $('#filterButton').click(function() {
        loadRecords();
    });

    // 绑定添加记录提交事件 (add.html)
    $('#addRecordForm').submit(function(e) {
        e.preventDefault();
        let type = $('input[name="type"]:checked').val();
        let categoryId = type == 1 ? $('#incomeCategoryId').val() : $('#expenseCategoryId').val();

        let recordData = {
            amount: parseFloat($('#amount').val()),
            type: parseInt(type),
            categoryId: parseInt(categoryId),
            accountId: parseInt($('#accountId').val()),
            recordDate: $('#recordDate').val(),
            remark: $('#remark').val()
        };

        sendAjaxRequest('/record/api/add', 'POST', recordData, function() {
            window.location.href = '/record/list'; // 添加成功后跳转回列表
        });
    });

    // 编辑记录页面的加载和提交 (detail.html / edit.html)
    function loadRecordDetail(recordId) {
        sendAjaxRequest('/record/api/detail/' + recordId, 'GET', null, function(record) {
            if (record) {
                $('#detailId').val(record.id);
                $('#detailRecordDate').val(formatDate(record.recordDate));
                if (record.type === 1) {
                    $('#detailTypeIncome').prop('checked', true);
                    $('#detailExpenseCategoryGroup').hide();
                    $('#detailIncomeCategoryGroup').show();
                    loadCategorySelect($('#detailIncomeCategoryId'), 1, record.categoryId);
                } else {
                    $('#detailTypeExpense').prop('checked', true);
                    $('#detailIncomeCategoryGroup').hide();
                    $('#detailExpenseCategoryGroup').show();
                    loadCategorySelect($('#detailExpenseCategoryId'), 2, record.categoryId);
                }
                $('#detailAmount').val(record.amount.toFixed(2));
                loadAccountSelect($('#detailAccountId'), record.accountId); // 重新加载账户下拉
                $('#detailRemark').val(record.remark);
                if(record.voucherUrl) {
                    $('#voucherImage').attr('src', record.voucherUrl).show();
                } else {
                    $('#voucherImage').hide();
                }
            }
        });
    }

    // 动态加载类别下拉框并选中
    function loadCategorySelect(selectElement, type, selectedId) {
        sendAjaxRequest('/category/api/all/' + type, 'GET', null, function(categories) {
            selectElement.empty();
            if (categories && categories.length > 0) {
                categories.forEach(function(category) {
                    selectElement.append(`<option value="${category.id}">${category.categoryName}</option>`);
                });
            }
            if (selectedId) {
                selectElement.val(selectedId);
            }
        });
    }

    // 动态加载账户下拉框并选中
    function loadAccountSelect(selectElement, selectedId) {
        sendAjaxRequest('/account/api/all', 'GET', null, function(accounts) {
            selectElement.empty();
            if (accounts && accounts.length > 0) {
                accounts.forEach(function(account) {
                    selectElement.append(`<option value="${account.id}">${account.accountName} (${account.accountType})</option>`);
                });
            }
            if (selectedId) {
                selectElement.val(selectedId);
            }
        });
    }

    // 记录详情页面的类型切换事件
    $('#detailTypeIncome, #detailTypeExpense').change(function() {
        let type = $('input[name="detailType"]:checked').val();
        if (type == 1) { // 收入
            $('#detailExpenseCategoryGroup').hide();
            $('#detailIncomeCategoryGroup').show();
            loadCategorySelect($('#detailIncomeCategoryId'), 1);
        } else { // 支出
            $('#detailIncomeCategoryGroup').hide();
            $('#detailExpenseCategoryGroup').show();
            loadCategorySelect($('#detailExpenseCategoryId'), 2);
        }
    });

    // 绑定更新记录提交事件 (detail.html的修改按钮)
    $(document).on('click', '#updateRecordButton', function() {
        let recordId = $('#detailId').val();
        let type = $('input[name="detailType"]:checked').val();
        let categoryId = type == 1 ? $('#detailIncomeCategoryId').val() : $('#detailExpenseCategoryId').val();

        let recordData = {
            id: recordId,
            amount: parseFloat($('#detailAmount').val()),
            type: parseInt(type),
            categoryId: parseInt(categoryId),
            accountId: parseInt($('#detailAccountId').val()),
            recordDate: $('#detailRecordDate').val(),
            remark: $('#detailRemark').val()
        };

        sendAjaxRequest('/record/api/update/' + recordId, 'POST', recordData, function() {
            showMessage('记录更新成功', 'success');
            // 更新成功后可以重新加载列表或者刷新本页
            // window.location.reload();
            history.back(); // 返回列表页
        });
    });

    // 绑定删除记录事件 (列表页)
    $('#recordTableBody').on('click', '.delete-btn', function() {
        let recordId = $(this).data('id');
        confirmAction('确定要删除这条收支记录吗？', function() {
            sendAjaxRequest('/record/api/delete/' + recordId, 'DELETE', null, function() {
                loadRecords(); // 重新加载列表
            });
        });
    });

    // 绑定查看/编辑按钮 (列表页)
    $('#recordTableBody').on('click', '.view-btn', function() {
        let recordId = $(this).data('id');
        window.location.href = '/record/detail/' + recordId;
    });

    // 凭证图片上传
    $('#voucherUploadBtn').click(function() {
        $('#voucherFile').click(); // 触发文件选择
    });

    $('#voucherFile').change(function() {
        let file = this.files[0];
        if (!file) return;

        let formData = new FormData();
        formData.append('file', file);
        let recordId = $('#detailId').val(); // 如果是编辑操作，带上recordId
        if (recordId) {
            formData.append('recordId', recordId);
        }

        $.ajax({
            url: '/upload/image',
            type: 'POST',
            data: formData,
            processData: false, // 告诉jQuery不要去处理发送的数据
            contentType: false, // 告诉jQuery不要去设置Content-Type请求头
            success: function(res) {
                if (res.code === 200) {
                    showMessage('凭证上传成功', 'success');
                    $('#voucherImage').attr('src', res.data).show();
                    // 如果是新增记录页面，需要在post recordData时将voucherUrl也带上
                    // 如果是详情页，后端已经更新了对应记录的voucherUrl
                } else {
                    showMessage(res.msg || '凭证上传失败', 'error');
                }
            },
            error: function(xhr, status, error) {
                showMessage(xhr.responseJSON ? xhr.responseJSON.msg : error, 'error');
            }
        });
    });

    // 初始化加载
    if (window.location.pathname.startsWith('/record/list')) {
        let defaultStartDate = $('#startDate').val();
        if (!defaultStartDate) $('#startDate').val(formatDate(new Date())); // 默认当前月第一天
        let defaultEndDate = $('#endDate').val();
        if (!defaultEndDate) $('#endDate').val(formatDate(new Date())); // 默认当前月最后一天
        loadFilterOptions();
        loadRecords();
    } else if (window.location.pathname.startsWith('/record/detail/')) {
        let recordId = window.location.pathname.split('/').pop();
        loadRecordDetail(recordId);
        loadFilterOptions(); // 兼容加载账户和类别下拉
    } else if (window.location.pathname.startsWith('/record/add')) {
        loadFilterOptions(); // 兼容加载账户和类别下拉
        // 绑定add页面类型切换
        $('input[name="type"]').change(function() {
            let type = $(this).val();
            if (type == 1) { // 收入
                $('#expenseCategoryGroup').hide();
                $('#incomeCategoryGroup').show();
            } else { // 支出
                $('#incomeCategoryGroup').hide();
                $('#expenseCategoryGroup').show();
            }
        }).filter(':checked').trigger('change'); // 触发一次初始状态
    }
});