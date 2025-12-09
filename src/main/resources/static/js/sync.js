/**
 * sync.js
 * 数据同步页面相关的JS逻辑
 */

$(function() {
    function loadSyncInfo() {
        // 由于没有直接提供单个API获取所有SyncInfo，这里简单示例通过多次调用
        // 实际可以将后端API设计为一次性返回所有类型同步信息
        sendAjaxRequest('/sync/api/getSyncInfo?syncType=record', 'GET', null, function(data) {
            updateSyncStatus('record', data);
        }, function() { updateSyncStatus('record', null); });

        sendAjaxRequest('/sync/api/getSyncInfo?syncType=account', 'GET', null, function(data) {
            updateSyncStatus('account', data);
        }, function() { updateSyncStatus('account', null); });

        sendAjaxRequest('/sync/api/getSyncInfo?syncType=category', 'GET', null, function(data) {
            updateSyncStatus('category', data);
        }, function() { updateSyncStatus('category', null); });

        // 也可以直接在后端通过controller方法返回一个map<String, DataSync>对象
        // @GetMapping("/api/allSyncInfo")
        // @ResponseBody Result<Map<String, DataSync>> getAllSyncInfo() { ... }
        // 然后前端一次性渲染
    }

    function updateSyncStatus(syncType, syncData) {
        let typeName = '';
        let rowId = '';
        if (syncType === 'record') { typeName = '收支记录'; rowId = '#syncRowRecord'; }
        else if (syncType === 'account') { typeName = '账户'; rowId = '#syncRowAccount'; }
        else if (syncType === 'category') { typeName = '类别'; rowId = '#syncRowCategory'; }
        else return;

        let lastSyncTime = '未同步';
        let maxSyncId = '无数据';

        if (syncData) {
            lastSyncTime = syncData.lastSyncTime ? new Date(syncData.lastSyncTime).toLocaleString() : 'N/A';
            maxSyncId = syncData.maxSyncId !== undefined ? syncData.maxSyncId : 'N/A';
        }

        $(rowId + ' .sync-type').text(typeName);
        $(rowId + ' .max-sync-id').text(maxSyncId);
        $(rowId + ' .last-sync-time').text(lastSyncTime);
    }

    // 绑定同步按钮事件
    $('.sync-button').click(function() {
        let syncType = $(this).data('sync-type');
        confirmAction(`确定要同步 ${syncType} 类型的数据吗？(此操作会更新同步标记，但不拉取数据)`, function() {
            sendAjaxRequest('/sync/api/triggerSync', 'POST', { syncType: syncType }, function() {
                loadSyncInfo(); // 重新加载同步信息
            });
        });
    });

    // TODO: 离线存储的逻辑需要在前端实现
    // 例如： IndexedDB 存储，PWA Service Worker
    // 这里的"同步"仅仅是后端更新一个同步标记，实际的数据同步需要前端配合拉取和提交增量数据

    // 初始化加载
    if (window.location.pathname.startsWith('/sync/page')) {
        loadSyncInfo();
    }
});