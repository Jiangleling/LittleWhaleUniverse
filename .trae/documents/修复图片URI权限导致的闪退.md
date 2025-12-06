## 问题与根因

* 日志显示 SecurityException：访问 `MediaDocumentsProvider` 需要通过 `ACTION_OPEN_DOCUMENT` 获取并“持久化”权限。

* 目前图片选择虽然用 `ACTION_OPEN_DOCUMENT`，但未给 Intent 添加 `FLAG_GRANT_PERSISTABLE_URI_PERMISSION` 和 `FLAG_GRANT_READ_URI_PERMISSION`，导致保存的 `content://` URI 在应用重启或列表渲染时没有权限，`ImageView` 在测量阶段尝试解码 URI 触发崩溃。

## 修复要点

选择图片时为 Intent 加上两类权限标志：

`Intent.FLAG_GRANT_READ_URI_PERMISSION`

`Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION`

在 `onActivityResult` 中调用：

`contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)`

列表加载缩略图时做权限与可读性校验：

尝试用 `ContentResolver.openInputStream(uri)` 读一小段；失败则隐藏缩略图，避免 `ImageView.setImageURI` 触发 provider 打开。

* 成功时用 `BitmapFactory.decodeStream` 或保持 `setImageURI`，但都放在 try/catch 并在失败时 `setImageDrawable(null)` + `GONE`。
* 兜底策略（可选，更稳健）：

  * 在选择图片后，拷贝一份到 `app` 私有缓存目录（如 `context.getCacheDir()/images/…`），以后列表统一加载私有路径，避免外部 URI 权限问题。

## 具体代码改动

* `EditDiaryActivity`（图片选择处）

  * 创建选择 Intent：

    ```java
    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
    intent.addCategory(Intent.CATEGORY_OPENABLE);
    intent.setType("image/*");
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
    startActivityForResult(intent, 1001);
    ```

  * 结果回调：

    ```java
    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
    ```

* `DiaryListAdapter`（绑定缩略图）

  * 在绑定时：

    ```java
    try (InputStream is = context.getContentResolver().openInputStream(Uri.parse(uri))) {
        if (is == null) { imageThumbView.setVisibility(View.GONE); } else {
            imageThumbView.setVisibility(View.VISIBLE);
            imageThumbView.setImageURI(Uri.parse(uri));
        }
    } catch (Exception e) {
        imageThumbView.setVisibility(View.GONE);
        imageThumbView.setImageDrawable(null);
    }
    ```

* （可选）私有拷贝：

  * 在 `EditDiaryActivity` 里读取选中流并写入 `cache/images`，持久化为 `file://` 或 `content://`（`FileProvider`），后续只使用内部路径。

## 兼容与清理

* 对旧数据库中已保存的不可读 URI，在绑定时探测失败则不显示缩略图，不再崩溃。

* 音频功能已移除，无需调整。

## 验证步骤

* 运行：选择一张图片，返回主界面应显示缩略图。

* 杀进程后重启 App，列表仍可显示缩略图（验证持久化权限生效）。

* Logcat 不应再出现该 SecurityException；若仍有，记录具体 URI scheme 与 provider，再按兜底策略改为私有拷贝。

是否同意以上改动？我将按此方案更新代码并自测。
