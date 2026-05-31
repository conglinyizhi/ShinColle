# ShinColle 深海日志 — 页面排版编辑技能

## 涉及文件

| 文件 | 路径 | 作用 |
|---|---|---|
| BookList 数据 | `src/main/java/org/trp/shincolle/reference/Values.java` (行 207-246) | 每页的元素布局（图片、图标、文字的位置） |
| 渲染引擎 | `src/main/java/org/trp/shincolle/client/renderer/BookRenderer.java` | SPACER、缩放、换行逻辑 |
| 中文文本 | `src/main/resources/assets/shincolle/lang/zh_cn.json` | 每页的文字内容 |

---

## BookList 数据结构

### 键值编码

```
bookID = 章节号 * 1000 + 页码
```

| 章节 | 章节号 | bookID 范围 |
|---|---|---|
| 方块/物品/资源 | 1 | 1000-1029 |
| 栖舰装备/附魔 | 2 | 2000-2004 |
| 栖舰操作指南 | 3 | 3000-3020+ |
| 我方舰娘资料 | 4 | 4000-4020+ |
| 敌方舰船资料 | 5 | 5000-5020+ |
| 指令/模拟战 | 6 | 6000-6004 |

**第 4 章和第 5 章的页码 0 是章封面**（无 BookList 条目，自动渲染标题），真正的内容从页码 1 开始。

### 元素类型 (Element Types)

每个页面是一个 `List<int[]>`，其中每个 `int[]` 代表一个页面元素：

#### Type 0 — 文字块

```java
{0, side, offX, offY}
```

| 索引 | 含义 | 说明 |
|---|---|---|
| 0 | 类型 | 固定为 0 |
| 1 | side | 0=左页, 1=右页 |
| 2 | offX | 水平偏移（像素，通常为 0） |
| 3 | offY | 垂直偏移（像素，0=页面顶部；负值=向上偏移） |

文字键自动生成为：`gui.shincolle.book.chap{章节}.text{页码}d{side}`

#### Type 1 — 图片/合成表

```java
{1, side, offX, offY, picNum, u, v, w, h}
```

| 索引 | 含义 |
|---|---|
| 0 | 固定为 1 |
| 1 | side: 0=左页, 1=右页 |
| 2 | offX |
| 3 | offY |
| 4 | picNum: 纹理编号 → `textures/gui/book/bookpic01.png` 等 |
| 5-8 | u, v, w, h: 纹理坐标和尺寸（最大 256×256） |

**这是触发 SPACER 的唯一元素类型。** 渲染时扫描所有 type=1 的垂直范围，推下方的文字。

#### Type 2 — 物品图标

```java
{2, side, offX, offY, iconID}
```

| 索引 | 含义 |
|---|---|
| 0 | 固定为 2 |
| 1 | side: 0=左页, 1=右页 |
| 2 | offX |
| 3 | offY |
| 4 | iconID: 对应的 ItemStack（查 `Values.ItemIconMap`） |

**不参与 SPACER**——图标不会推文字。如需为图标预留空间，用 type=1 空图片占位。

---

## 渲染常量 (BookRenderer.java 第 28 行)

```java
LX = 13    // 左页文字区 X 起点
RX = 132   // 右页文字区 X 起点
TY = 44    // 页面内容区 Y 起点
MAXW = 102 // 文字换行宽度（像素）
```

- 实际渲染坐标 = `x/y`（传入的参数） + `LX/RX/TY` + 元素 `offX/offY`
- 图片实际 Y = `y + TY + 4 + offY`（第 148 行，+4 是封面标题偏移）
- 图标实际 Y = `y + TY + 4 + offY`
- 文字实际 Y = `y + TY + offY`

---

## SPACER 机制

`BookRenderer.java` 第 56-83 行实现：

1. **预扫描**：遍历页面所有 type=1 元素，计算各自的 `{top, bottom}` 范围
2. **每个 type=0 文字块**：检查是否与同 side 的图片重叠
3. **如果重叠**：将文字起始 Y 推到 `max(图片 bottom) + 2`
4. **不重叠**：正常渲染，不推

### 局限性

- 只检查 type=1（图片），不检查 type=2（图标）
- 只推一次，不考虑推之后的二次重叠
- 多图片只取最大 bottom，不处理多段间隔

---

## 文字溢出检查

### 可视区域限制

- 每行最多 **12 个中文字符**（含标点、§ 色码不计宽度）
- 每页每半边最多 **17 行**
- 超出范围会渲染到页面之外，无法看到

### 字符宽度计算脚本

修改文本前必须用 Python 计算实际行数。UTF-8 编码下：

```python
import json, re

def real_w(c):
    """返回字符显示宽度：CJK=1.0 ASCII=0.5"""
    if '\u4e00' <= c <= '\u9fff' or '\u3000' <= c <= '\u303f' or '\uff00' <= c <= '\uffef':
        return 1.0
    return 0.5 if ord(c) < 128 else 1.0

def strip_colors(s):
    """去掉 § 色码"""
    return re.sub(r'§[0-9a-fk-or]', '', s)

def count_lines(text, max_width=12.0):
    """返回总行数"""
    total = 0
    for line in text.split('<br>'):
        line = strip_colors(line)
        w = sum(real_w(c) for c in line)
        total += max(1, int(w / max_width + 0.99))  # 向上取整
    return total

# 使用
with open('src/main/resources/assets/shincolle/lang/zh_cn.json') as f:
    d = json.load(f)
key = 'gui.shincolle.book.chap1.text11d1'
print(f'{key}: {count_lines(d[key])} lines')
```

---

## 常见操作

### 1. 调整文字垂直位置

改对应 type=0 元素的 `offY`：

```java
// 原来
new int[] {0, 0, 0, 0}   // 左页文字，从顶部开始

// 下移 20 像素
new int[] {0, 0, 0, 20}  // 左页文字，从顶部+20开始
```

### 2. 给居中图标加占位块

如果图标居中但文字需要从图标下方开始，不推文字的话加一个透明占位图：

```java
// type=1 占位，w=100 h=36 占据图标区域
new int[] {1, 0, 0, -6, 0, 100, 72, 100, 36}
```

这样 SPACER 会推文字到占位块下方。

### 3. 减少文字溢出

不修改排版的前提下，只能压缩文本：
- 缩短语句
- 用 `/` 替代 `、` 连接并列项
- 去掉重复的修饰词
- 合并相邻短行
- **保留所有 § 色码**

### 4. 添加/修改页面图片

```java
// 添加一张合成表（picNum=0, 纹理坐标 100,72, 宽 100, 高 62）
new int[] {1, 0, 0, -6, 0, 100, 72, 100, 62}
```

---

## 注意事项

- 修改 `Values.java` 后需要重新编译（`./gradlew build`）
- 修改 `zh_cn.json` 后无需重新编译，直接替换 jar 包内的对应文件即可测试
- SPACER 依赖 type=1 的 `offY` 和 `h`，同时修改两者时注意垂直范围计算
- 文字 `offY=0` 意味着从 `TY(44)` 开始，即页面顶部标题下方
- 不要合并单个页面的多个 type=0 为一个大块——左右页必须分开（side=0 和 side=1 各一个）
