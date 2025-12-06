## 问题定位
- 日记页：工具栏/标题区域与正文滚动区在不同约束下，键盘弹出时布局未充分“压缩”，FAB与内容可能相互遮挡。
- AI占卜师页：输入框与发送按钮在键盘弹出时未上移，底部导航与键盘导致可视区被遮挡。

## 修复方案
- 全局键盘适配：为 `MainActivity` 设置 `windowSoftInputMode=adjustResize`，让承载 Fragment 的容器随键盘缩短高度。
- AI页布局改造：
  - 用 `NestedScrollView` 包裹对话区（替换当前 TextView），设置高为 0dp，约束 `Top->Title`、`Bottom->Input`，键盘弹出时自动压缩；
  - 输入区与“发送”按钮保持底部对齐，按钮与输入框之间设定固定间距；
  - 根布局增加少量底部内边距，避免与底部导航视觉拥挤。
- 日记页细化：
  - 确保 FAB 不覆盖内容：为根或滚动区添加底部 `padding`（例如 80dp），或将 FAB 约束到父底部同时滚动区 `Bottom->image_preview` 保持；
  - 工具栏与心情图标保持单行，统一 40–48dp 尺寸与 8dp 间距；
  - 标题与时间区域明确约束，防止出现控件重叠。

## 具体改动
1) `AndroidManifest.xml`：为 `MainActivity` 增加 `android:windowSoftInputMode="adjustResize"`。
2) `fragment_ai.xml`：
   - 将 `text_conversation` 替换为 `NestedScrollView + TextView`，高度 0dp，`Top->text_ai_title`，`Bottom->edit_message`；
   - 为根布局增加 `android:paddingBottom="16dp"`；输入框 `End->button_send`，`Bottom->parent`。
3) `activity_edit.xml`：
   - 为 `ScrollView` 或根布局增加底部 `padding`（约 80dp）避免 FAB 遮挡；
   - 检查并统一工具栏与心情图标的尺寸与间距，防止横向挤压换行；
   - 保持滚动内容 `Bottom->image_preview`，心情栏 `Top->image_preview`，标签 `Top->mood_bar`。

## 验证
- 打开 AI页并弹出键盘：输入区域与发送按钮完整可见，对话区可滚动。
- 打开日记页并弹出键盘：正文可见，FAB 不遮挡；工具栏与心情图标不重叠。

如果同意，我将按此方案修改布局与 Manifest，并进行一次本地验证。