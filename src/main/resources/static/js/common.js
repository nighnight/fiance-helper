/**
 * common.js
 * 存放常用的工具函数和AJAX请求封装
 */

$(function() {
    // 全局AJAX错误处理
    $(document).ajaxError(function(event, xhr, settings, thrownError) {
        if (xhr.status === 401) {
            // 未登录或session过期，重定向到登录页
            alert("登录已过期，请重新登录。");
            window.location.href = "/user/login";
        } else if (xhr.responseJSON && xhr.responseJSON.msg) {
            // 后端返回的自定义错误信息
            showMessage(xhr.responseJSON.msg, 'error');
        } else {
            // 其他未知错误
            showMessage("请求失败：" + thrownError || "未知错误", 'error');
        }
    });

    // 格式化日期函数 (yyyy-MM-dd)
    window.formatDate = function(date) {
        if (!date) return '';
        let d = new Date(date);
        let year = d.getFullYear();
        let month = (d.getMonth() + 1).toString().padStart(2, '0');
        let day = d.getDate().toString().padStart(2, '0');
        return `${year}-${month}-${day}`;
    };

    // 格式化月份函数 (yyyy-MM)
    window.formatYearMonth = function(date) {
        if (!date) return '';
        let d = new Date(date);
        let year = d.getFullYear();
        let month = (d.getMonth() + 1).toString().padStart(2, '0');
        return `${year}-${month}`;
    };

    // 显示消息提示
    // type: 'success', 'error', 'info', 'warning'
    window.showMessage = function(message, type = 'info') {
        let alertId = 'global-alert-message';
        let alertDiv = $(`#${alertId}`);
        if (alertDiv.length === 0) {
            alertDiv = $(`<div id="${alertId}" class="alert" style="position: fixed; top: 60px; right: 20px; z-index: 1050; display: none;"></div>`);
            $('body').append(alertDiv);
        }

        alertDiv.removeClass('alert-success alert-danger alert-info alert-warning')
            .addClass(`alert-${type}`)
            .html(message)
            .fadeIn(300);

        setTimeout(function() {
            alertDiv.fadeOut(300, function() {
                // alertDiv.remove(); // 淡出后移除，如果需要
            });
        }, 3000); // 3秒后消失
    };

    // 通用确认弹窗
    window.confirmAction = function(message, callback) {
        if (confirm(message)) { // 使用浏览器自带的confirm简化
            callback();
        }
    };

    // 通用AJAX请求函数
    window.sendAjaxRequest = function(url, method, data, successCallback, errorCallback) {
        $.ajax({
            url: url,
            type: method,
            contentType: method === 'POST' || method === 'PUT' || method === 'DELETE' ? 'application/json' : 'application/x-www-form-urlencoded',
            data: data ? JSON.stringify(data) : null,
            success: function(res) {
                if (res.code === 200) {
                    if (successCallback) successCallback(res.data);
                    if (res.msg) showMessage(res.msg, 'success');
                } else {
                    if (errorCallback) errorCallback(res.msg);
                    showMessage(res.msg || '操作失败', 'error');
                }
            },
            error: function(xhr, status, error) {
                // 全局ajaxError已经处理了大部分，这里可以处理一些特定于业务的错误
                if (errorCallback) errorCallback(xhr.responseJSON ? xhr.responseJSON.msg : error);
            }
        });
    };

    // 初始化下拉菜单
    $('.dropdown-toggle').dropdown();

    // 激活当前导航链接
    let path = window.location.pathname;
    // Special handling for index page if needed
    if (path === '/' || path === '/index') {
        $('.navbar-nav .nav-item a[href="/"]').addClass('active');
    } else {
        $('.navbar-nav .nav-item a').each(function() {
            let href = $(this).attr('href');
            if (href && path.startsWith(href) && href !== '/') {
                $(this).addClass('active');
            }
        });
    }
});